package story.cheek.story.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import story.cheek.common.constant.SortType;
import story.cheek.common.dto.SliceResponse;
import story.cheek.common.exception.StoryForbiddenException;
import story.cheek.common.image.S3Service;
import story.cheek.highlight.domain.Highlight;
import story.cheek.highlight.domain.StoryHighlight;
import story.cheek.highlight.repository.HighlightRepository;
import story.cheek.highlight.repository.StoryHighlightRepository;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Status;
import story.cheek.member.repository.MemberRepository;
import story.cheek.question.domain.Occupation;
import story.cheek.question.domain.Question;
import story.cheek.question.repository.QuestionRepository;
import story.cheek.story.domain.Story;
import story.cheek.story.dto.request.StoryCreateRequest;
import story.cheek.story.dto.response.StoryDetailResponse;
import story.cheek.story.dto.response.StoryResponse;
import story.cheek.story.repository.StoryRepository;

@SpringBootTest
class StoryServiceTest {

    @MockBean
    S3Service s3Service;

    @MockBean
    ApplicationEventPublisher eventPublisher;

    @Autowired
    StoryService storyService;

    @Autowired
    StoryRepository storyRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    HighlightRepository highlightRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    StoryHighlightRepository storyHighlightRepository;

    Member member;

    Question question;

    Story story;


    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
                .status(Status.ACTIVE)
                .isMentor(false)
                .build()
        );

        question = questionRepository.save(Question.createQuestion(
                Occupation.DEVELOP,
                "chick",
                "백엔드란?",
                member
        ));

        story = storyRepository.save(Story.createStory(
                Occupation.DEVELOP,
                "image.png",
                question,
                member
        ));
    }

    @Test
    void 멘토가_아니면_스토리를_생성할_수_없다() {
        MockMultipartFile image = new MockMultipartFile(
                "request",
                "request",
                APPLICATION_JSON_VALUE,
                "image".getBytes()
        );

        StoryCreateRequest request = new StoryCreateRequest(question.getId(),
                Occupation.DEVELOP,
                image);

        Assertions.assertThatThrownBy(() -> storyService.save(member, request))
                .isInstanceOf(StoryForbiddenException.class);
    }

    @Test
    void 스토리_상세_정보를_조회한다() {
        StoryDetailResponse response = storyService.findDetailById(story.getId());

        Assertions.assertThat(response).usingRecursiveComparison()
                .isEqualTo(StoryDetailResponse.from(story));
    }

    @Test
    void 스토리_목록을_조회한다() {
        member.approveMentor();
        List<StoryResponse> values = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Story savedStory = storyRepository.save(Story.createStory(
                    Occupation.DEVELOP,
                    "image.png",
                    question, member));

            values.add(StoryResponse.from(savedStory));
        }

        Collections.reverse(values);
        SliceResponse<StoryResponse> response = storyService.findAll(5, SortType.LATEST, null);

        Assertions.assertThat(response.values()).usingRecursiveComparison().isEqualTo(values);
    }

    @Test
    void 하이라이트에_속한_스토리를_조회한다() {
        Highlight highlight = highlightRepository.save(Highlight.createHighlight(
                member,
                "하이라이트"
        ));

        List<StoryResponse> values = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Story savedStory = storyRepository.save(Story.createStory(
                    Occupation.DEVELOP,
                    "image.png",
                    question,
                    member
                    ));
            StoryHighlight storyHighlight = StoryHighlight.of(highlight, savedStory);
            savedStory.addStoryHighlight(storyHighlight);
            storyHighlightRepository.save(storyHighlight);
            values.add(StoryResponse.from(savedStory));
        }

        Collections.reverse(values);

        SliceResponse<StoryResponse> response = storyRepository.findAllByHighlightOrderByIdDesc(5,
                null, highlight, SortType.LATEST);

        Assertions.assertThat(response.values()).usingRecursiveComparison().isEqualTo(values);
    }
}
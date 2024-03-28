package story.cheek.highlight.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import story.cheek.common.dto.SliceResponse;
import story.cheek.common.exception.ForbiddenHighlightException;
import story.cheek.common.exception.StoryForbiddenException;
import story.cheek.common.exception.StoryHighlightDuplicateException;
import story.cheek.highlight.domain.Highlight;
import story.cheek.highlight.domain.StoryHighlight;
import story.cheek.highlight.dto.request.HighlightCreateRequest;
import story.cheek.highlight.dto.request.HighlightStoryCreateRequest;
import story.cheek.highlight.dto.response.HighlightResponse;
import story.cheek.highlight.repository.HighlightRepository;
import story.cheek.highlight.repository.StoryHighlightRepository;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Status;
import story.cheek.member.repository.MemberRepository;
import story.cheek.question.domain.Occupation;
import story.cheek.question.domain.Question;
import story.cheek.question.repository.QuestionRepository;
import story.cheek.story.domain.Story;
import story.cheek.story.repository.StoryRepository;

@SpringBootTest
class HighlightServiceTest {

    @Autowired
    HighlightRepository highlightRepository;

    @Autowired
    StoryHighlightRepository storyHighlightRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    StoryRepository storyRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    HighlightService highlightService;

    Story story;

    Story story2;

    Question question;

    Member member;

    Member member2;

    Highlight highlight;

    Highlight highlight2;

    StoryHighlight storyHighlight;

    StoryHighlight storyHighlight2;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(member.builder()
                .status(Status.ACTIVE)
                .build());

        member2 = memberRepository.save(member.builder()
                .status(Status.ACTIVE)
                .build());

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

        story2 = storyRepository.save(Story.createStory(
                Occupation.PLAN,
                "image.png",
                question,
                member2
        ));

        highlight = highlightRepository.save(Highlight.createHighlight(
                member,
                "chick"
        ));

        highlight2 = highlightRepository.save(Highlight.createHighlight(
                member2,
                "chick"
        ));

        storyHighlight = storyHighlightRepository.save(StoryHighlight.of(highlight, story));
        storyHighlight2 = storyHighlightRepository.save(StoryHighlight.of(highlight2, story2));
    }

    @Test
    void 멘토가_아니면_하이라이트를_생성할_수_없다() {
        HighlightCreateRequest request = new HighlightCreateRequest("chick2");

        Assertions.assertThatThrownBy(() -> highlightService.save(member, request))
                .isInstanceOf(ForbiddenHighlightException.class);
    }

    @Test
    void 하이라이트를_삭제한다() {
        highlightService.delete(member, highlight.getId());

        Assertions.assertThat(highlightRepository.findById(highlight.getId()).isEmpty()).isTrue();
    }

    @Test
    void 하이라이트가_삭제되면_연관된_모든_스토리_하이라이트가_삭제된다() {
        Story savedStory = storyRepository.save(Story.createStory(
                Occupation.DEVELOP,
                "image.png",
                question,
                member
        ));

        storyHighlightRepository.save(StoryHighlight.of(highlight, savedStory));
        highlightService.delete(member, highlight.getId());

        Assertions.assertThat(storyHighlightRepository.findAllByHighlightId(highlight.getId()).isEmpty())
                .isTrue();
    }

    @Test
    void 본인이_아니면_하이라이트를_삭제할_수_없다() {
        Assertions.assertThatThrownBy(() -> highlightService.delete(member2, highlight.getId()))
                .isInstanceOf(ForbiddenHighlightException.class);
    }

    @Test
    void 하이라이트_목록을_조회한다() {
        List<HighlightResponse> values = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Highlight savedHighlight = highlightRepository.save(Highlight.createHighlight(member, "chick@" + i));
            values.add(new HighlightResponse(savedHighlight.getId(), savedHighlight.getTitle()));
        }

        Collections.reverse(values);
        SliceResponse<HighlightResponse> response = highlightService.findAll(5, member.getId(), null);

        Assertions.assertThat(response.values()).usingRecursiveComparison().isEqualTo(values);
    }

    @Test
    void 하이라이트에_스토리를_추가한다() {
        Story savedStory = storyRepository.save(storyRepository.save(Story.createStory(
                Occupation.DEVELOP,
                "image.png",
                question,
                member
        )));
        HighlightStoryCreateRequest request = new HighlightStoryCreateRequest(highlight.getId(), savedStory.getId());

        Long savedId = highlightService.saveStoryHighlight(member, request);
        Assertions.assertThat(savedId).isEqualTo(savedStory.getId());
    }

    @Test
    void 본인의_스토리가_아니면_하이라이트에_추가할_수_없다() {
        HighlightStoryCreateRequest request = new HighlightStoryCreateRequest(highlight.getId(), story2.getId());

        Assertions.assertThatThrownBy(() -> highlightService.saveStoryHighlight(member, request))
                .isInstanceOf(StoryForbiddenException.class);
    }

    @Test
    void 본인의_하이라이트가_아니면_스토리를_추가할_수_없다() {
        HighlightStoryCreateRequest request = new HighlightStoryCreateRequest(highlight.getId(), story.getId());

        Assertions.assertThatThrownBy(() -> highlightService.saveStoryHighlight(member2, request))
                .isInstanceOf(ForbiddenHighlightException.class);
    }

    @Test
    void 하이라이트에_동일한_스토리를_2번_이상_추가할_수_없다() {
        HighlightStoryCreateRequest request = new HighlightStoryCreateRequest(highlight.getId(), story.getId());

        Assertions.assertThatThrownBy(() -> highlightService.saveStoryHighlight(member, request))
                .isInstanceOf(StoryHighlightDuplicateException.class);
    }

    @Test
    void 하이라이트에서_스토리를_삭제한다() {
        highlightService.deleteStoryHighlight(member, story.getId(), highlight.getId());

        Assertions.assertThat(storyHighlightRepository.findById(storyHighlight.getId()).isEmpty()).isTrue();
    }

    @Test
    void 본인이_아니면_하이라이트에서_스토리_하이라이트를_삭제할_수_없다() {
        Assertions.assertThatThrownBy(() ->
                highlightService.deleteStoryHighlight(member2, story.getId(), highlight.getId()))
                .isInstanceOf(ForbiddenHighlightException.class);
    }
}
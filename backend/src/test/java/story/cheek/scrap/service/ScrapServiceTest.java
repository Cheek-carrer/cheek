package story.cheek.scrap.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import story.cheek.common.exception.ScrapDuplicationException;
import story.cheek.common.exception.ScrapForbiddenException;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Status;
import story.cheek.member.repository.MemberRepository;
import story.cheek.question.domain.Occupation;
import story.cheek.question.domain.Question;
import story.cheek.question.repository.QuestionRepository;
import story.cheek.scrap.dto.response.ScrapResponse;
import story.cheek.scrap.repository.ScrapRepository;
import story.cheek.story.domain.Scrap;
import story.cheek.story.domain.Story;
import story.cheek.story.repository.StoryRepository;

@SpringBootTest
class ScrapServiceTest {

    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    StoryRepository storyRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ScrapService scrapService;

    Story story;

    Question question;

    Member member;

    Scrap scrap;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(member.builder()
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

        scrap = scrapRepository.save(Scrap.of(
                member,
                story
        ));
    }

    @Test
    void 동일한_스토리를_중복해서_스크랩할_수_없다() {
        Assertions.assertThatThrownBy(() -> scrapService.save(member, story.getId()))
                .isInstanceOf(ScrapDuplicationException.class);
    }

    @Test
    void 유저_아이디로_스크랩한_스토리_목록을_조회한다() {
        List<ScrapResponse> values = new ArrayList<>();
        values.add(ScrapResponse.from(scrap));
        for (int i = 0; i < 5; i++) {
            Story savedStory = storyRepository.save(Story.createStory(
                    Occupation.DEVELOP,
                    "image.png",
                    question,
                    member
            ));
            Scrap savedScrap = Scrap.of(member, savedStory);
            scrapRepository.save(savedScrap);
            values.add(ScrapResponse.from(savedScrap));
        }

        Collections.reverse(values);

        List<ScrapResponse> response = scrapService.findAllByMemberId(member.getId());
        Assertions.assertThat(response).usingRecursiveComparison().isEqualTo(values);
    }

    @Test
    void 스크랩을_삭제한다() {
        scrapService.delete(member, scrap.getId());
        Assertions.assertThat(scrapRepository.findById(scrap.getId()).isEmpty()).isTrue();
    }

    @Test
    void 스크랩한_스토리는_다른_유저가_삭제할_수_없다() {
        Member member2 = memberRepository.save(Member.builder().build());

        Assertions.assertThatThrownBy(() -> scrapService.delete(member2, scrap.getId()))
                .isInstanceOf(ScrapForbiddenException.class);
    }
}
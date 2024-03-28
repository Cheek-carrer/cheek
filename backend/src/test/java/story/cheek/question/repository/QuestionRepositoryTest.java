package story.cheek.question.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import story.cheek.DatabaseCleanup;
import story.cheek.common.dto.SliceResponse;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Role;
import story.cheek.member.repository.MemberRepository;
import story.cheek.question.domain.Occupation;
import story.cheek.question.domain.Question;
import story.cheek.question.dto.response.QuestionResponse;
import story.common.annotation.RepositoryTest;

@RepositoryTest
class QuestionRepositoryTest {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DatabaseCleanup databaseCleanup;

    Member member;


    @BeforeEach
    void setUp() {
        databaseCleanup.execute();
        member = memberRepository.save(Member.builder().role(Role.ROLE_USER).build());
    }

    @Test
    void 직종을_선택하지_않으면_모든_직종에_대한_질문이_조회된다() {
        List<QuestionResponse> values = new ArrayList<>();
        Question question1 = questionRepository.save(Question.createQuestion(
                Occupation.DEVELOP,
                "chick",
                "chick 백엔드란",
                member
        ));

        Question question2 = questionRepository.save(Question.createQuestion(
                Occupation.PLAN,
                "chick",
                "chick 기획이란",
                member
        ));

        Question question3 = questionRepository.save(Question.createQuestion(
                Occupation.MARKETING,
                "chick",
                "chick 마켓팅이란",
                member
        ));

        values.add(QuestionResponse.from(question3));
        values.add(QuestionResponse.from(question2));
        values.add(QuestionResponse.from(question1));

        SliceResponse<QuestionResponse> response = questionRepository.findAllByOrderByIdDesc(3, null, null);

        Assertions.assertThat(response.values()).usingRecursiveComparison().isEqualTo(values);
    }

    @Test
    void 직종을_선택하면_해당_직종의_질문만_조회된다() {
        List<QuestionResponse> values = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Question question = questionRepository.save(Question.createQuestion(
                    Occupation.DEVELOP,
                    "chick",
                    "chick 백엔드란",
                    member
            ));

            values.add(QuestionResponse.from(question));
        }

        questionRepository.save(Question.createQuestion(
                Occupation.PLAN,
                "chick",
                "chick 기획이란",
                member
        ));

        Collections.reverse(values);

        SliceResponse<QuestionResponse> response = questionRepository.findAllByOrderByIdDesc(3, null,
                Occupation.DEVELOP);

        Assertions.assertThat(response.values()).usingRecursiveComparison().isEqualTo(values);
    }

    @Test
    void 커서값을_세팅하면_커서값보다_작은_Id를_가진_질문이_조회된다() {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Question question = questionRepository.save(Question.createQuestion(
                    Occupation.DEVELOP,
                    "chick",
                    "chick 백엔드란",
                    member
            ));

            ids.add(question.getId());
        }

        Collections.reverse(ids);
        SliceResponse<QuestionResponse> response1 = questionRepository.findAllByOrderByIdDesc(3, null, null);
        Assertions.assertThat(response1.values().get(0).id()).isEqualTo(ids.get(0));
        Assertions.assertThat(response1.hasNext()).isTrue();
        Assertions.assertThat(response1.cursor()).isEqualTo(String.valueOf(ids.get(2)));

        String cursor = response1.cursor();
        System.out.println(cursor);

        SliceResponse<QuestionResponse> response2 = questionRepository.findAllByOrderByIdDesc(3, cursor, null);
        Assertions.assertThat(response2.values().get(0).id()).isEqualTo(ids.get(3));
        Assertions.assertThat(response2.hasNext()).isFalse();
        Assertions.assertThat(response2.cursor()).isEqualTo(null);
    }
}
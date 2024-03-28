package story.cheek.question.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import story.cheek.common.dto.SliceResponse;
import story.cheek.common.exception.BlockedMemberException;
import story.cheek.common.exception.QuestionForbiddenException;
import story.cheek.member.domain.Member;
import story.cheek.member.domain.Status;
import story.cheek.member.repository.MemberRepository;
import story.cheek.question.domain.Occupation;
import story.cheek.question.domain.Question;
import story.cheek.question.dto.request.QuestionCreateRequest;
import story.cheek.question.dto.request.QuestionUpdateRequest;
import story.cheek.question.dto.response.QuestionDetailResponse;
import story.cheek.question.dto.response.QuestionResponse;
import story.cheek.question.repository.QuestionRepository;


@SpringBootTest
class QuestionServiceTest {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionService questionService;

    @Autowired
    MemberRepository memberRepository;

    Member member;

    Question question;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
                .status(Status.ACTIVE)
                .build());

        question = questionRepository.save(Question.createQuestion(
                Occupation.DEVELOP,
                "chick",
                "백엔드란?",
                member
        ));
    }

    @Test
    void 차단된_유저는_질문을_생성할_수_없다() {
        Member suspendedMember = memberRepository.save(Member.builder()
                .status(Status.SUSPENDED)
                .build());

        QuestionCreateRequest request = new QuestionCreateRequest("chick", "백엔드란?", Occupation.DEVELOP);

        Assertions.assertThatThrownBy(() -> questionService.save(suspendedMember, request))
                .isInstanceOf(BlockedMemberException.class);
    }

    @Test
    void 질문_상세_정보를_조회한다() {
        Long id = question.getId();
        QuestionDetailResponse response = questionService.findDetailById(id);

        Assertions.assertThat(response).usingRecursiveComparison()
                .isEqualTo(QuestionDetailResponse.from(question));
    }

    @Test
    void 질문을_수정한다() {
        QuestionUpdateRequest request = new QuestionUpdateRequest(
                "updated",
                "프런트는 무엇인가요?",
                Occupation.PLAN);

        questionService.update(member, question.getId(), request);

        Question findQuestion = questionRepository.findById(question.getId()).get();
        Assertions.assertThat(findQuestion.getTitle()).isEqualTo("updated");
        Assertions.assertThat(findQuestion.getContent()).isEqualTo("프런트는 무엇인가요?");
        Assertions.assertThat(findQuestion.getOccupation()).isEqualTo(Occupation.PLAN);
    }

    @Test
    void 본인이_작성한_질문이_아니면_수정할_수_없다() {
        Member member1 = memberRepository.save(Member.builder()
                .status(Status.ACTIVE)
                .build());

        QuestionUpdateRequest request = new QuestionUpdateRequest(
                "updated",
                "프런트는 무엇인가요?",
                Occupation.DEVELOP);


        Assertions.assertThatThrownBy(() -> questionService.update(member1, question.getId(), request))
                .isInstanceOf(QuestionForbiddenException.class);
    }

    @Test
    void 질문_정보_리스트를_조회한다() {
        List<QuestionResponse> values = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Question createdQuestion = Question.createQuestion(
                    Occupation.DEVELOP,
                    "chick",
                    "chick 백엔드란",
                    member
            );

            questionRepository.save(createdQuestion);
            values.add(QuestionResponse.from(createdQuestion));
        }

        Collections.reverse(values);

        SliceResponse<QuestionResponse> response = questionRepository.findAllByOrderByIdDesc(5, null,
                Occupation.DEVELOP);

        Assertions.assertThat(response.values()).usingRecursiveComparison().isEqualTo(values);
    }
}
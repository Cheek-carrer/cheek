package story.cheek.question.service;

import static story.cheek.common.exception.ErrorCode.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import story.cheek.common.dto.SliceResponse;
import story.cheek.common.exception.BlockedMemberException;
import story.cheek.common.exception.NotFoundQuestionException;
import story.cheek.common.exception.QuestionForbiddenException;
import story.cheek.member.domain.Member;
import story.cheek.question.domain.Occupation;
import story.cheek.question.domain.Question;
import story.cheek.question.dto.request.QuestionCreateRequest;
import story.cheek.question.dto.request.QuestionUpdateRequest;
import story.cheek.question.dto.response.QuestionDetailResponse;
import story.cheek.question.dto.response.QuestionResponse;
import story.cheek.question.repository.QuestionRepository;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Long save(Member member, QuestionCreateRequest request) {
        validateMemberStatus(member);
        Question question = Question.createQuestion(
                request.occupation(),
                request.title(),
                concatenateTitleAndContent(request),
                member
        );

        return questionRepository.save(question).getId();
    }

    private String concatenateTitleAndContent(QuestionCreateRequest request) {
        return request.title() + " " + request.content();
    }

    public QuestionDetailResponse findDetailById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundQuestionException(QUESTION_NOT_FOUND));

        return QuestionDetailResponse.from(question);
    }

    @Transactional
    public void update(
            Member member,
            Long questionId,
            QuestionUpdateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundQuestionException(QUESTION_NOT_FOUND));

        validateQuestionUpdate(member, question);

        question.update(request.occupation(), request.title(), request.content());
    }

    private void validateQuestionUpdate(Member member, Question question) {
        if (!member.hasAuthority(question.getWriter().getId())) {
            throw new QuestionForbiddenException(FORBIDDEN_QUESTION_UPDATE);
        }
    }

    public SliceResponse<QuestionResponse> findAll(int pageSize, String cursor, Occupation occupation) {
        return questionRepository.findAllByOrderByIdDesc(pageSize, cursor, occupation);
    }

    private void validateMemberStatus(Member member) {
        if (!member.isActive()) {
            throw new BlockedMemberException(INACTIVE_MEMBER);
        }
    }
}

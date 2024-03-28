package story.cheek.question.repository;

import static story.cheek.member.domain.QMember.member;
import static story.cheek.question.domain.QQuestion.question;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import story.cheek.common.dto.SliceResponse;
import story.cheek.question.domain.Occupation;
import story.cheek.question.dto.response.QuestionResponse;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public SliceResponse<QuestionResponse> findAllByOrderByIdDesc(int pageSize, String cursor, Occupation occupation) {
        List<QuestionResponse> questions = queryFactory.select(Projections.constructor(
                        QuestionResponse.class,
                        question.id,
                        question.title,
                        question.content.substring(question.title.length().add(1)),
                        member.name
                ))
                .from(question)
                .join(question.writer, member)
                .where(ltQuestionId(cursor), occupationEq(occupation))
                .orderBy(question.id.desc())
                .limit(pageSize + 1)
                .fetch();

        return convertToSlice(questions, pageSize);
    }

    private BooleanExpression ltQuestionId(String cursor) {
        if (cursor != null) {
            return question.id.lt(Long.valueOf(cursor));
        }

        return null;
    }

    private BooleanExpression occupationEq(Occupation occupation) {
        if (occupation != null) {
            return question.occupation.eq(occupation);
        }

        return null;
    }

    private SliceResponse<QuestionResponse> convertToSlice(List<QuestionResponse> questions, int pageSize) {
        if (questions.isEmpty()) {
            return SliceResponse.of(questions, false, null);
        }

        boolean hasNext = existNextPage(questions, pageSize);
        String nextCursor = null;
        if (hasNext) {
            deleteLastPage(questions, pageSize);
            nextCursor = String.valueOf(questions.get(questions.size() - 1).id());
        }

        return SliceResponse.of(questions, hasNext, nextCursor);
    }

    private boolean existNextPage(List<QuestionResponse> questions, int pageSize) {
        if (questions.size() > pageSize){
            return true;
        }
        return false;
    }

    private void deleteLastPage(List<QuestionResponse> questions, int pageSize) {
        questions.remove(pageSize);
    }
}

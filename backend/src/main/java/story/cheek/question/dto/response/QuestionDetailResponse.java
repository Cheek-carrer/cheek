package story.cheek.question.dto.response;

import java.time.LocalDateTime;
import story.cheek.question.domain.Occupation;
import story.cheek.question.domain.Question;

public record QuestionDetailResponse(
        Long id,
        Occupation occupation,
        String title,
        String content,
        LocalDateTime updatedAt
) {

    public static QuestionDetailResponse from(Question question) {
        return new QuestionDetailResponse(
                question.getId(),
                question.getOccupation(),
                question.getTitle(),
                question.getContent(),
                question.getUpdateAt()
        );
    }
}

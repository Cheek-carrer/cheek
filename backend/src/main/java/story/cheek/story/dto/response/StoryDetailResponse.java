package story.cheek.story.dto.response;

import java.time.LocalDateTime;
import story.cheek.question.domain.Occupation;
import story.cheek.story.domain.Story;

public record StoryDetailResponse(
        Long storyId,
        Long questionId,
        Long memberId,
        Occupation occupation,
        String imageUrl,
        LocalDateTime createdAt
) {

    public static StoryDetailResponse from(Story story) {
        return new StoryDetailResponse(
                story.getId(),
                story.getQuestion().getId(),
                story.getMember().getId(),
                story.getOccupation(),
                story.getImageUrl(),
                story.getCreatedAt()
        );
    }
}

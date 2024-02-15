package story.cheek.follow.dto;

public record FollowResponse(
        Long sequenceNumber,
        Long memberId,
        String name,
        String imageUrl
) {
}

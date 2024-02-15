package story.cheek.follow.dto;

import story.cheek.follow.domain.Follow;
import story.cheek.member.domain.Member;

public record FollowRequest(
        Long followerId
) {
    public Follow toEntity(Member following, Member follower) {
        return Follow.builder()
                .followingSequenceNumber((long) following.getFollowingList().size() + 1)
                .followerSequenceNumber((long) follower.getFollowerList().size() + 1)
                .followingMember(following)
                .follower(follower)
                .build();
    }
}

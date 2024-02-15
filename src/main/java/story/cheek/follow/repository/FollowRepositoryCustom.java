package story.cheek.follow.repository;

import story.cheek.common.constant.SortType;
import story.cheek.common.dto.SliceResponse;
import story.cheek.follow.dto.FollowResponse;
import story.cheek.member.domain.Member;

public interface FollowRepositoryCustom {
    SliceResponse<FollowResponse> findFollowingMembersByOrderByFollowingSequenceNumberDesc(SortType sortType, Member member, String cursor);

    SliceResponse<FollowResponse> findFollowersByOrderByFollowerSequenceNumberDesc(SortType sortType, Member member, String cursor);
}

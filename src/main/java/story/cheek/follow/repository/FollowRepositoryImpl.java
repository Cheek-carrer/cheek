package story.cheek.follow.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import story.cheek.common.constant.SortType;
import story.cheek.common.dto.SliceResponse;
import story.cheek.common.exception.ErrorCode;
import story.cheek.common.exception.NotFoundFollowException;
import story.cheek.follow.dto.FollowResponse;
import story.cheek.member.domain.Member;

import java.util.List;

import static story.cheek.follow.domain.QFollow.follow;

@Slf4j
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryCustom {
    private static final int PAGE_SIZE = 10;
    private final JPAQueryFactory queryFactory;

    @Override
    public SliceResponse<FollowResponse> findFollowingMembersByOrderByFollowingSequenceNumberDesc(SortType sortType, Member member, String cursor) {
        List<FollowResponse> follows = queryFactory.select(Projections.constructor(FollowResponse.class,
                        follow.followingSequenceNumber,
                        follow.follower.id,
                        follow.follower.name,
                        follow.follower.image
                ))
                .from(follow)
                .where(followingMemberIdEq(member.getId()),
                        ltFollowingSequenceNumber(cursor)
                )
                .orderBy(follow.followingSequenceNumber.desc())
                .limit(PAGE_SIZE + 1)
                .fetch();

        return convertToSlice(sortType, follows);
    }

    @Override
    public SliceResponse<FollowResponse> findFollowersByOrderByFollowerSequenceNumberDesc(SortType sortType, Member member, String cursor) {
        List<FollowResponse> follows = queryFactory.select(Projections.constructor(FollowResponse.class,
                        follow.followerSequenceNumber,
                        follow.followingMember.id,
                        follow.followingMember.name,
                        follow.followingMember.image
                ))
                .from(follow)
                .where(followerIdEq(member.getId()),
                        ltFollowerSequenceNumber(cursor)
                )
                .orderBy(follow.followerSequenceNumber.desc())
                .limit(PAGE_SIZE + 1)
                .fetch();

        return convertToSlice(sortType, follows);
    }

    private BooleanExpression followingMemberIdEq(Long currentMemberId) {
        return follow.followingMember.id.eq(currentMemberId);
    }

    private BooleanExpression followerIdEq(Long currentMemberId) {
        return follow.follower.id.eq(currentMemberId);
    }

    private SliceResponse<FollowResponse> convertToSlice(SortType sortType, List<FollowResponse> follows) {
        validateFollowExist(sortType, follows);
        boolean hasNext = existNextPage(follows);
        String nextCursor = generateCursor(follows);
        return SliceResponse.of(follows, hasNext, nextCursor);
    }

    private void validateFollowExist(SortType sortType, List<FollowResponse> follows) {
        if (follows.isEmpty()) {
            if (sortType.equals(SortType.FOLLOWING)) {
                throw new NotFoundFollowException(ErrorCode.FOLLOWING_NOT_FOUND);
            }

            throw new NotFoundFollowException(ErrorCode.FOLLOWER_NOT_FOUND);
        }
    }

    private BooleanExpression ltFollowingSequenceNumber(String cursor) {
        if (cursor != null) {
            return follow.followingSequenceNumber.lt(Long.valueOf(cursor));
        }

        return null;
    }

    private BooleanExpression ltFollowerSequenceNumber(String cursor) {
        if (cursor != null) {
            return follow.followerSequenceNumber.lt(Long.valueOf(cursor));
        }

        return null;
    }

    private String generateCursor(List<FollowResponse> follows) {
        FollowResponse lastFollow = follows.get(follows.size() - 1);
        return String.valueOf(lastFollow.sequenceNumber());
    }

    private boolean existNextPage(List<FollowResponse> follows) {
        if (follows.size() > PAGE_SIZE) {
            follows.remove(PAGE_SIZE);
            return true;
        }

        return false;
    }
}

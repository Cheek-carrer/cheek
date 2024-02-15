package story.cheek.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import story.cheek.follow.domain.Follow;
import story.cheek.member.domain.Member;

import java.util.Optional;


public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {
    boolean existsFollowByFollowingMemberAndFollower(Member followingMember, Member follower);

    Optional<Follow> findFollowByFollowingMemberIdAndFollowerId(Long followingMemberId, Long followerId);
}

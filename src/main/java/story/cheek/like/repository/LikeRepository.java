package story.cheek.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import story.cheek.like.domain.Like;
import story.cheek.member.domain.Member;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByMember(Member member);
}

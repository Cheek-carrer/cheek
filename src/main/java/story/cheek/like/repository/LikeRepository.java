package story.cheek.like.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import story.cheek.like.domain.Like;
import story.cheek.member.domain.Member;
import story.cheek.story.domain.Story;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByMember(Member member);

    Optional<Like> findByMemberAndStory(Member member, Story story);
}

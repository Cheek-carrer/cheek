package story.cheek.scrap.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import story.cheek.member.domain.Member;
import story.cheek.story.domain.Scrap;
import story.cheek.story.domain.Story;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    @Query("select sc from Scrap sc join fetch sc.story s where sc.member = :member order by sc.id desc")
    List<Scrap> findAllByMemberWithStoryOrderByIdDesc(Member member);

    boolean existsByMemberAndStory(Member member, Story story);
}

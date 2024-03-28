package story.cheek.highlight.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import story.cheek.highlight.domain.Highlight;
import story.cheek.highlight.domain.StoryHighlight;
import story.cheek.story.domain.Story;

public interface StoryHighlightRepository extends JpaRepository<StoryHighlight, Long> {
    Optional<StoryHighlight> findByHighlightAndStory(Highlight highlight, Story story);

    boolean existsByHighlightAndStory(Highlight highlight, Story story);

    @Query("select sh from StoryHighlight sh where sh.highlight.id = :highlightId")
    List<StoryHighlight> findAllByHighlightId(Long highlightId);

    @Modifying(clearAutomatically = true)
    @Query("delete from StoryHighlight sh where sh.highlight.id = :highlightId")
    void deleteAllByHighlightId(Long highlightId);
}

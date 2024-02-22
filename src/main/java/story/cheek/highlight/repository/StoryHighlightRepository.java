package story.cheek.highlight.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import story.cheek.highlight.domain.Highlight;
import story.cheek.highlight.domain.StoryHighlight;
import story.cheek.story.domain.Story;

public interface StoryHighlightRepository extends JpaRepository<StoryHighlight, Long> {
    Optional<StoryHighlight> findByHighlightAndStory(Highlight highlight, Story story);

    boolean existsByHighlightAndStory(Highlight highlight, Story story);
}

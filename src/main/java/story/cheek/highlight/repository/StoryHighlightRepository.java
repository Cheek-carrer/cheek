package story.cheek.highlight.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import story.cheek.highlight.domain.StoryHighlight;

public interface StoryHighlightRepository extends JpaRepository<StoryHighlight, Long> {
}

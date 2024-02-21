package story.cheek.highlight.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import story.cheek.highlight.domain.Highlight;

public interface HighlightRepository extends JpaRepository<Highlight, Long>, HighlightRepositoryCustom {
}

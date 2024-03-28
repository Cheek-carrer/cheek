package story.cheek.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import story.cheek.question.domain.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
}

package story.cheek.question.repository;

import story.cheek.common.dto.SliceResponse;
import story.cheek.question.domain.Occupation;
import story.cheek.question.dto.response.QuestionResponse;

public interface QuestionRepositoryCustom {
    SliceResponse<QuestionResponse> findAllByOrderByIdDesc(int pageSize, String cursor, Occupation occupation);
}

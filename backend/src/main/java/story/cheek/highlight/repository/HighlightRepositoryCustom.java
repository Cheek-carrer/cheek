package story.cheek.highlight.repository;

import story.cheek.common.dto.SliceResponse;
import story.cheek.highlight.dto.response.HighlightResponse;
import story.cheek.member.domain.Member;

public interface HighlightRepositoryCustom {

    SliceResponse<HighlightResponse> findAllByMemberOrderByIdDesc(int pageSize, Member member, String cursor);
}

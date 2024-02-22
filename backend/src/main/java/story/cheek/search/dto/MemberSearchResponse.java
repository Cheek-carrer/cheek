package story.cheek.search.dto;

import story.cheek.question.domain.Occupation;
import story.cheek.search.document.SearchMember;

public record MemberSearchResponse(
        Long memberId,
        String name,
        Occupation occupation,
        String image,
        String description
) {
    public static MemberSearchResponse from(SearchMember searchMember) {
        return new MemberSearchResponse(
                searchMember.getMemberId(),
                searchMember.getName(),
                searchMember.getOccupation(),
                searchMember.getImage(),
                searchMember.getDescription()
        );
    }
}

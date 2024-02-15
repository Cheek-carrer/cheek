package story.cheek.member.dto;

import story.cheek.member.domain.Member;
import story.cheek.question.domain.Occupation;

public record MemberResponse(
        // 마이페이지 구성인 안되어있어 기본적인 요소만 반환
        Long id,
        Occupation occupation,
        String name,
        String email,
        String image,
        boolean isMentor
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getOccupation(),
                member.getName(),
                member.getEmail(),
                member.getImage(),
                member.isMentor()
        );
    }

}

package story.cheek.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import story.cheek.common.exception.ErrorCode;
import story.cheek.common.exception.NotFoundMemberException;
import story.cheek.common.image.S3Service;
import story.cheek.member.domain.Member;
import story.cheek.member.dto.MemberBasicInfoUpdateRequest;
import story.cheek.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final S3Service s3Service;
    private final MemberRepository memberRepository;

    @Transactional
    public void updateMemberImage(Member uploadedMember, MultipartFile file) {
        Member member = findMember(uploadedMember.getId());
        String updateImageUrl = s3Service.upload(file);
        s3Service.deleteFile(uploadedMember.getImage());
        member.updateImage(updateImageUrl);
    }

    @Transactional
    public void updateMemberBasicInfo(Member uploadedMember, MemberBasicInfoUpdateRequest memberBasicInfoUpdateRequest) {
        Member member = findMember(uploadedMember.getId());
        member.updateBasicInfo(memberBasicInfoUpdateRequest);
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(ErrorCode.MEMBER_NOT_FOUND));
    }
}

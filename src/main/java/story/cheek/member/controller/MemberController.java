package story.cheek.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import story.cheek.member.domain.Member;
import story.cheek.member.dto.MemberBasicInfoUpdateRequest;
import story.cheek.member.dto.MemberResponse;
import story.cheek.member.service.MemberService;
import story.cheek.security.CurrentMember;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<MemberResponse> getMemberInfo(@CurrentMember Member member) {
        return ResponseEntity.ok().body(MemberResponse.from(member));
    }

    @PutMapping("/image")
    public ResponseEntity<Void> updateMemberImage(@CurrentMember Member member,
                                                  @RequestParam MultipartFile file) {
        memberService.updateMemberImage(member, file);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/basic-info")
    public ResponseEntity<Void> updateMemberBasicInfo(@CurrentMember Member member,
                                                      @RequestBody MemberBasicInfoUpdateRequest memberBasicInfoUpdateRequest) {
        memberService.updateMemberBasicInfo(member, memberBasicInfoUpdateRequest);
        return ResponseEntity.ok().build();
    }
}

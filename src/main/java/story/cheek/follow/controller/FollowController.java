package story.cheek.follow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import story.cheek.common.constant.SortType;
import story.cheek.common.dto.SliceResponse;
import story.cheek.follow.dto.FollowRequest;
import story.cheek.follow.dto.FollowResponse;
import story.cheek.follow.service.FollowService;
import story.cheek.member.domain.Member;
import story.cheek.security.CurrentMember;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/follows")
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<Void> followRequest(@CurrentMember Member member,
                                              @RequestBody FollowRequest followRequest) {
        Long followId = followService.followMember(member, followRequest);
        return ResponseEntity.created(URI.create("/api/v1/follow/" + followId)).build();
    }

    @GetMapping
    public ResponseEntity<SliceResponse<FollowResponse>> getFollows(@CurrentMember Member member,
                                                                    SortType sortType,
                                                                    @RequestParam(required = false) String cursor) {
        return ResponseEntity.ok().body(followService.getFollows(member, sortType, cursor));
    }

    @DeleteMapping("/{followId}")
    public ResponseEntity<Void> cancelFollow(@CurrentMember Member member,
                                             @PathVariable Long followId) {
        followService.deleteFollow(member, followId);
        return ResponseEntity.noContent().build();
    }
}

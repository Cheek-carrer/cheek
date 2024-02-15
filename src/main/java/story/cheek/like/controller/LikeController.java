package story.cheek.like.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import story.cheek.like.service.LikeService;
import story.cheek.member.domain.Member;
import story.cheek.security.CurrentMember;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{storyId}")
    public ResponseEntity<Void> create(@CurrentMember Member member, @PathVariable Long storyId) {
        Long likeId = likeService.likeStory(member, storyId);

        return ResponseEntity.created(URI.create("/api/v1/likes/" + likeId))
                .build();
    }

    @PostMapping("/cancellations/{storyId}")
    public ResponseEntity<Void> delete(@CurrentMember Member member, @PathVariable Long storyId) {
        likeService.cancelStoryLike(member, storyId);

        return ResponseEntity.noContent()
                .build();
    }
}

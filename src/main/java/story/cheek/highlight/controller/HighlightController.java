package story.cheek.highlight.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import story.cheek.highlight.dto.request.HighlightCreateRequest;
import story.cheek.highlight.service.HighlightService;
import story.cheek.member.domain.Member;
import story.cheek.security.CurrentMember;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/highlights")
public class HighlightController {

    private final HighlightService highlightService;

    @PostMapping
    public ResponseEntity<Void> create(
            @CurrentMember Member member,
            @RequestBody HighlightCreateRequest request
    ) {

        Long highlightId = highlightService.save(member, request);

        return ResponseEntity.created(URI.create("/api/v1/highlights/" + highlightId))
                .build();
    }

    @DeleteMapping("/{highlightId}")
    public ResponseEntity<Void> delete(
            @CurrentMember Member member,
            @PathVariable Long highlightId
    ) {

        highlightService.delete(member, highlightId);

        return ResponseEntity.noContent()
                .build();
    }
}

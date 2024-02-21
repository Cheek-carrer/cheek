package story.cheek.highlight.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import story.cheek.common.dto.SliceResponse;
import story.cheek.highlight.dto.request.HighlightCreateRequest;
import story.cheek.highlight.dto.response.HighlightResponse;
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

    @GetMapping("/members")
    public ResponseEntity<SliceResponse<HighlightResponse>> findAll(
            @RequestParam("id") Long memberId,
            @RequestParam(required = false) String cursor) {
        // TODO 기획 : 하이라이트 정렬 기준은?

        SliceResponse<HighlightResponse> response = highlightService.findAll(memberId, cursor);

        return ResponseEntity.ok(response);
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

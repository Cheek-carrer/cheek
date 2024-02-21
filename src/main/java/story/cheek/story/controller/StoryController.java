package story.cheek.story.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import story.cheek.common.constant.SortType;
import story.cheek.member.domain.Member;
import story.cheek.security.CurrentMember;
import story.cheek.story.dto.request.StoryCreateRequest;
import story.cheek.story.dto.request.StoryCreateRequestWithoutImage;
import story.cheek.common.dto.SliceResponse;
import story.cheek.story.dto.response.StoryDetailResponse;
import story.cheek.story.dto.response.StoryResponse;
import story.cheek.story.service.StoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stories")
public class StoryController {

    private final StoryService storyService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> create(
            @CurrentMember Member member,
            @RequestPart StoryCreateRequestWithoutImage request,
            @RequestPart MultipartFile image
            ) {

        StoryCreateRequest storyCreateRequest = StoryCreateRequest.of(request, image);
        Long storyId = storyService.save(member, storyCreateRequest);

        return ResponseEntity.created(URI.create("/api/v1/stories/" + storyId))
                .build();
    }

    @GetMapping("/{storyId}")
    public ResponseEntity<StoryDetailResponse> findById(
            @CurrentMember Member member,
            @PathVariable Long storyId
    ) {

        StoryDetailResponse response = storyService.findDetailById(member, storyId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SliceResponse<StoryResponse>> findAll(
            @CurrentMember Member member,
            SortType sortType,
            @RequestParam(required = false) String cursor
    ) {

        SliceResponse<StoryResponse> response = storyService.findAll(member, sortType, cursor);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/highlights")
    public ResponseEntity<SliceResponse<StoryResponse>> findAllByHighlight(
            @RequestParam("id") Long highlightId,
            SortType sortType,
            @RequestParam(required = false) String cursor
    ) {

        SliceResponse<StoryResponse> response = storyService.findAllByHighlightId(highlightId, cursor, sortType);
        return ResponseEntity.ok(response);
    }
}

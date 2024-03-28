package story.cheek.question.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import story.cheek.common.dto.SliceResponse;
import story.cheek.member.domain.Member;
import story.cheek.question.domain.Occupation;
import story.cheek.question.dto.request.QuestionCreateRequest;
import story.cheek.question.dto.request.QuestionUpdateRequest;
import story.cheek.question.dto.response.QuestionDetailResponse;
import story.cheek.question.dto.response.QuestionResponse;
import story.cheek.question.service.QuestionService;
import story.cheek.security.CurrentMember;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<Void> create(
            @CurrentMember Member member,
            @RequestBody QuestionCreateRequest questionCreateRequest
    ) {

        Long savedId = questionService.save(member, questionCreateRequest);

        return ResponseEntity.created(URI.create("/api/v1/questions/" + savedId))
                .build();
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDetailResponse> findById(
            @PathVariable Long questionId
    ) {
        QuestionDetailResponse response = questionService.findDetailById(questionId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<Void> update(
            @CurrentMember Member member,
            @PathVariable Long questionId,
            @RequestBody QuestionUpdateRequest questionUpdateRequest
    ) {
        questionService.update(member, questionId, questionUpdateRequest);
        return ResponseEntity.ok()
                .header("Location", "/api/v1/questions/" + questionId)
                .build();
    }

    @GetMapping
    public ResponseEntity<SliceResponse<QuestionResponse>> findAll(
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Occupation occupation
            ) {

        SliceResponse<QuestionResponse> response = questionService.findAll(pageSize, cursor, occupation);
        return ResponseEntity.ok(response);
    }
}

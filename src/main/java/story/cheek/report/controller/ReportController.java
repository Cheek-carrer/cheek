package story.cheek.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import story.cheek.member.domain.Member;
import story.cheek.report.dto.ReportRequest;
import story.cheek.report.service.ReportService;
import story.cheek.security.CurrentMember;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> report(@CurrentMember Member member,
                                       @RequestBody ReportRequest reportRequest) {
        Long reportId = reportService.report(member.getId(), reportRequest);
        return ResponseEntity.created(URI.create("/api/v1/reports/" + reportId)).build();
    }
}

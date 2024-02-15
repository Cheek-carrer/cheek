package story.cheek.report.domain;

import jakarta.persistence.*;
import lombok.*;
import story.cheek.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_member_id")
    private Member reportingMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_member_id")
    private Member reportedMember;

    @Builder
    public Report(String content, Member reportingMember, Member reportedMember) {
        this.content = content;
        this.reportingMember = reportingMember;
        this.reportedMember = reportedMember;
    }
}

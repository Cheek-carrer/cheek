package story.cheek.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import story.cheek.member.domain.Member;
import story.cheek.report.domain.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsReportByReportingMemberAndReportedMember(Member reprotingMember, Member reportedMember);
}

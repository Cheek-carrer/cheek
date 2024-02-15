package story.cheek.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import story.cheek.common.exception.DuplicateReportException;
import story.cheek.common.exception.ErrorCode;
import story.cheek.common.exception.NotFoundFollowException;
import story.cheek.common.exception.NotFoundMemberException;
import story.cheek.follow.domain.Follow;
import story.cheek.follow.repository.FollowRepository;
import story.cheek.member.domain.Member;
import story.cheek.member.repository.MemberRepository;
import story.cheek.report.domain.Report;
import story.cheek.report.dto.ReportRequest;
import story.cheek.report.repository.ReportRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    @Transactional
    public Long report(Long reportingMemberId, ReportRequest reportRequest) {
        Member reportingMember = findmember(reportingMemberId);
        Member reportedMember = findmember(reportRequest.reportedMemberId());

        manageReportAndFollowRelationship(reportingMember, reportedMember);

        Report report = reportRepository.save(reportRequest.toEntity(reportingMember, reportedMember));
        changeStatusIfReportedCountOverTen(reportedMember);

        reportedMember.addReportedList(report);
        reportingMember.addReportingList(report);

        return report.getId();
    }

    private void manageReportAndFollowRelationship(Member reportingMember, Member reportedMember) {
        if (reportRepository.existsReportByReportingMemberAndReportedMember(reportingMember, reportedMember)) {
            throw new DuplicateReportException(ErrorCode.DUPLICATED_REPORT);
        }

        if (followRepository.existsFollowByFollowingMemberAndFollower(reportingMember, reportedMember)) {
            Follow follow = followRepository.findFollowByFollowingMemberIdAndFollowerId(reportingMember.getId(), reportedMember.getId())
                    .orElseThrow(() -> new NotFoundFollowException(ErrorCode.FOLLOW_NOT_FOUND));

            followRepository.deleteById(follow.getId());
        }
    }

    private void changeStatusIfReportedCountOverTen(Member reportedMember) {
        if (reportedMember.getReportedListCount() > 10) {
            reportedMember.changeStatusToSuspend();
        }
    }

    private Member findmember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(ErrorCode.MEMBER_NOT_FOUND));
    }
}

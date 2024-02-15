package story.cheek.member.domain;

import jakarta.persistence.*;
import lombok.*;
import story.cheek.application.domain.Application;
import story.cheek.common.domain.BaseEntity;
import story.cheek.follow.domain.Follow;
import story.cheek.member.dto.MemberBasicInfoUpdateRequest;
import story.cheek.question.domain.Occupation;
import story.cheek.report.domain.Report;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Occupation occupation;

    private String name;

    private String email;

    private String image;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private boolean isMentor;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    @OneToOne(mappedBy = "member")
    private Application application;

    @OneToMany(mappedBy = "followingMember")
    private List<Follow> followingList = new ArrayList<>();

    @OneToMany(mappedBy = "follower")
    private List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "reportingMember")
    private List<Report> reportingList = new ArrayList<>();

    @OneToMany(mappedBy = "reportedMember")
    private List<Report> reportedList = new ArrayList<>();

    public String roleName() {
        return role.name();
    }

    public void updateImage(String imageUrl) {
        this.image = imageUrl;
    }

    public void updateBasicInfo(MemberBasicInfoUpdateRequest memberBasicInfoUpdateRequest) {
        this.name = memberBasicInfoUpdateRequest.name();
        this.occupation = memberBasicInfoUpdateRequest.occupation();
        this.description = memberBasicInfoUpdateRequest.description();
    }

    public int getReportedListCount() {
        return this.reportedList.size();
    }

    public void changeStatusToSuspend() {
        this.status = Status.SUSPENDED;
    }

    public void addFollowingMemberList(Follow follow) {
        this.followingList.add(follow);
    }

    public void addFollowerList(Follow follow) {
        this.followerList.add(follow);
    }

    public void addReportedList(Report report) {
        this.reportedList.add(report);
    }

    public void addReportingList(Report report) {
        this.reportingList.add(report);
    }

    public boolean canMakeStory() {
        return isMentor;
    }

    public boolean hasAuthority(Long memberId) {
        return !this.id.equals(memberId);

    public boolean isAdmin() {
        return role == Role.ROLE_ADMIN;
    }

    public void approveMentor() {
        isMentor = true;
    }
}

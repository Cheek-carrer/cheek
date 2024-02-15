package story.cheek.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import story.cheek.application.domain.Application;
import story.cheek.common.domain.BaseEntity;
import story.cheek.member.dto.MemberBasicInfoUpdateRequest;
import story.cheek.question.domain.Occupation;
import story.cheek.report.domain.Report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import story.cheek.story.domain.Scrap;
import story.cheek.story.domain.Story;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id @GeneratedValue
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

    @OneToMany(mappedBy = "reportingMember")
    private List<Report> reportingList = new ArrayList<>();

    @OneToMany(mappedBy = "reportedMember")
    private List<Report> reportedList = new ArrayList<>();

    public String roleName() {
        return role.name();
    }

    public void updateImage(MultipartFile file) {
        this.image = file.getOriginalFilename();
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

    public void addReportedList(Report report) {
        this.reportedList.add(report);
    }

    public void addReportingList(Report report) {
        this.reportingList.add(report);
    }

    public boolean canMakeStory() {
        return isMentor;
    }

    public boolean isScrapPermission(Scrap scrap) {
        return this.equals(scrap.getMember());
    }

    public boolean isAdmin() {
        return role == Role.ROLE_ADMIN;
    }

    public void approveMentor() {
        isMentor = true;
    }
}

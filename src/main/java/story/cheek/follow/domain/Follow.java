package story.cheek.follow.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import story.cheek.common.domain.BaseEntity;
import story.cheek.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    private Long followingSequenceNumber;

    private Long followerSequenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id")
    private Member followingMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private Member follower;

    @Builder
    public Follow(Long followingSequenceNumber, Long followerSequenceNumber, Member followingMember, Member follower) {
        this.followingMember = followingMember;
        this.follower = follower;
        this.followingSequenceNumber = followingSequenceNumber;
        this.followerSequenceNumber = followerSequenceNumber;
    }
}

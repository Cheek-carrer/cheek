package story.cheek.story.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import story.cheek.common.domain.BaseEntity;
import story.cheek.member.domain.Member;
import story.cheek.question.domain.Occupation;
import story.cheek.question.domain.Question;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Story extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Occupation occupation;

    @Column(nullable = false)
    private String imageUrl;

    @ColumnDefault("0")
    int likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Story(
            Occupation occupation,
            String imageUrl,
            Question question,
            Member writer
    ) {
        this.occupation = occupation;
        this.imageUrl = imageUrl;
        this.question = question;
        this.member = writer;
    }

    public static Story createStory(
            Occupation occupation,
            String imageUrl,
            Question question,
            Member writer
    ) {
        return new Story(
                occupation,
                imageUrl,
                question,
                writer
        );
    }

    public void like() {
        likeCount++;
    }
}

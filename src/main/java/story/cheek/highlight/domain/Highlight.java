package story.cheek.highlight.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import story.cheek.common.domain.BaseEntity;
import story.cheek.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Highlight extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "highlight")
    private List<StoryHighlight> storyHighlights = new ArrayList<>();

    @Column(nullable = false, length = 25)
    private String title;

    private Highlight(Member member, String title) {
        this.member = member;
        this.title = title;
    }

    public static Highlight createHighlight(Member member, String title) {
        return new Highlight(member, title);
    }
}

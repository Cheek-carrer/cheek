package story.cheek.highlight.repository;

import static story.cheek.highlight.domain.QHighlight.*;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import story.cheek.common.dto.SliceResponse;
import story.cheek.highlight.dto.response.HighlightResponse;
import story.cheek.member.domain.Member;

@RequiredArgsConstructor
public class HighlightRepositoryImpl implements HighlightRepositoryCustom {

    private static final int PAGE_SIZE = 20;

    private final JPAQueryFactory queryFactory;

    @Override
    public SliceResponse<HighlightResponse> findAllByMemberOrderByIdDesc(Member member, String cursor) {

        List<HighlightResponse> highlights = queryFactory.select(Projections.constructor(HighlightResponse.class,
                        highlight.id,
                        highlight.title))
                .from(highlight)
                .where(ltHighlightId(cursor),
                        isMatchMember(member))
                .orderBy(highlight.id.desc())
                .fetch();

        return convertToSlice(highlights);
    }

    private BooleanExpression isMatchMember(Member member) {
        return highlight.member.eq(member);
    }

    private BooleanExpression ltHighlightId(String cursor) {
        if (cursor != null) {
            return highlight.id.lt(Long.valueOf(cursor));
        }

        return null;
    }

    private SliceResponse<HighlightResponse> convertToSlice(List<HighlightResponse> highlights) {
        if (highlights.isEmpty()) {
            return SliceResponse.of(highlights, false, null);
        }

        boolean hasNext = existNextPage(highlights);
        String nextCursor = generateCursor(highlights);

        return SliceResponse.of(highlights, hasNext, nextCursor);
    }

    private String generateCursor(List<HighlightResponse> highlights) {
        HighlightResponse lastHighlight = highlights.get(highlights.size() - 1);

        return String.valueOf(lastHighlight.id());
    }


    private boolean existNextPage(List<HighlightResponse> highlights) {
        if (highlights.size() > PAGE_SIZE) {
            highlights.remove(PAGE_SIZE);
            return true;
        }

        return false;
    }
}

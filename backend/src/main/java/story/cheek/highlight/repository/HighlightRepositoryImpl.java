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

    private final JPAQueryFactory queryFactory;

    @Override
    public SliceResponse<HighlightResponse> findAllByMemberOrderByIdDesc(int pageSize, Member member, String cursor) {

        List<HighlightResponse> highlights = queryFactory.select(Projections.constructor(HighlightResponse.class,
                        highlight.id,
                        highlight.title))
                .from(highlight)
                .where(ltHighlightId(cursor),
                        isMatchMember(member))
                .orderBy(highlight.id.desc())
                .limit(pageSize + 1)
                .fetch();

        return convertToSlice(highlights, pageSize);
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

    private SliceResponse<HighlightResponse> convertToSlice(List<HighlightResponse> highlights, int pageSize) {
        if (highlights.isEmpty()) {
            return SliceResponse.of(highlights, false, null);
        }

        boolean hasNext = existNextPage(highlights, pageSize);
        String nextCursor = generateCursor(highlights);

        return SliceResponse.of(highlights, hasNext, nextCursor);
    }

    private String generateCursor(List<HighlightResponse> highlights) {
        HighlightResponse lastHighlight = highlights.get(highlights.size() - 1);

        return String.valueOf(lastHighlight.id());
    }


    private boolean existNextPage(List<HighlightResponse> highlights, int pageSize) {
        if (highlights.size() > pageSize) {
            highlights.remove(pageSize);
            return true;
        }

        return false;
    }
}

package story.cheek.story.repository;

import static story.cheek.highlight.domain.QStoryHighlight.storyHighlight;
import static story.cheek.story.domain.QStory.*;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import story.cheek.common.constant.SortType;
import story.cheek.common.dto.SliceResponse;
import story.cheek.highlight.domain.Highlight;
import story.cheek.story.dto.response.StoryResponse;

@RequiredArgsConstructor
@Slf4j
public class StoryRepositoryImpl implements StoryRepositoryCustom {

    private static final int MAX_LIKE_DIGIT = 6;
    private static final int MAX_ID_DIGIT = 8;

    private final JPAQueryFactory queryFactory;

    @Override
    public SliceResponse<StoryResponse> findAllByOrderByIdDesc(int pageSize, String cursor, SortType sortType) {
        List<StoryResponse> stories = queryFactory.select(Projections.constructor(StoryResponse.class,
                story.id,
                story.imageUrl,
                story.createdAt,
                story.likeCount
                ))
                .from(story)
                .where(ltStoryId(cursor))
                .orderBy(story.id.desc())
                .limit(pageSize + 1)
                .fetch();

        return convertToSlice(stories, sortType, pageSize);
    }

    @Override
    public SliceResponse<StoryResponse> findAllByHighlightOrderByIdDesc(
            int pageSize,
            String cursor,
            Highlight highlight,
            SortType sortType) {
        List<StoryResponse> stories = queryFactory.select(Projections.constructor(StoryResponse.class,
                        story.id,
                        story.imageUrl,
                        story.createdAt,
                        story.likeCount))
                .from(story)
                .join(story.storyHighlights, storyHighlight)
                .where(highlightIdEq(highlight),
                        ltStoryId(cursor))
                .orderBy(story.id.desc())
                .fetch();

        return convertToSlice(stories, sortType, pageSize);
    }

    private BooleanExpression highlightIdEq(Highlight highlight) {
        return storyHighlight.highlight.id.eq(highlight.getId());
    }

    @Override
    public SliceResponse<StoryResponse> findAllByOrderByLikeCountDesc(int pageSize, String cursor, SortType sortType) {
        List<StoryResponse> stories = queryFactory.select(Projections.constructor(StoryResponse.class,
                        story.id,
                        story.imageUrl,
                        story.createdAt,
                        story.likeCount
                ))
                .from(story)
                .where(ltStoryLikeCount(cursor))
                .orderBy(story.likeCount.desc(), story.createdAt.desc())
                .limit(pageSize + 1)
                .fetch();

        return convertToSlice(stories, sortType, pageSize);
    }

    private BooleanExpression ltStoryId(String cursor) {
        if (cursor != null) {
            return story.id.lt(Long.valueOf(cursor));
        }

        return null;
    }

    private BooleanExpression ltStoryLikeCount(String cursor) {
        if (cursor == null) {
            return null;
        }

        return StringExpressions.lpad(story.likeCount.stringValue(),MAX_LIKE_DIGIT,'0')
                .concat(StringExpressions.lpad(story.id.stringValue(),MAX_ID_DIGIT,'0'))
                .lt(cursor);
    }

    private SliceResponse<StoryResponse> convertToSlice(List<StoryResponse> stories, SortType sortType, int pageSize) {
        if (stories.isEmpty()) {
            return SliceResponse.of(stories, false, null);
        }

        boolean hasNext = existNextPage(stories, pageSize);
        String nextCursor = generateCursor(stories, sortType);

        return SliceResponse.of(stories, hasNext, nextCursor);
    }

    private String generateCursor(List<StoryResponse> stories, SortType sortType) {
        StoryResponse lastStory = stories.get(stories.size() - 1);

        if (sortType == SortType.LATEST) {
            return String.valueOf(lastStory.storyId());
        }

        return String.format("%06d", lastStory.likeCount())
                + String.format("%08d", lastStory.storyId());
    }


    private boolean existNextPage(List<StoryResponse> stories, int pageSize) {
        if (stories.size() > pageSize) {
            stories.remove(pageSize);
            return true;
        }

        return false;
    }
}

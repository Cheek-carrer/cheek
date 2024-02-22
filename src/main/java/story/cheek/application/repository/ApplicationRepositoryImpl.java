package story.cheek.application.repository;

import static story.cheek.application.domain.QApplication.application;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import story.cheek.application.dto.response.ApplicationResponse;
import story.cheek.common.dto.SliceResponse;

@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom {

    private static final int PAGE_SIZE = 10;

    private final JPAQueryFactory queryFactory;

    @Override
    public SliceResponse<ApplicationResponse> findAllExceptDeleted(String cursor) {

        List<ApplicationResponse> applications = queryFactory
                .select(Projections.constructor(ApplicationResponse.class,
                        application.id,
                        application.createdAt))
                .from(application)
                .where(gtApplicationId(cursor),
                        isDeletedEq(false))
                .orderBy(application.id.desc())
                .limit(PAGE_SIZE + 1)
                .fetch();

        return convertToSlice(applications);
    }

    private SliceResponse<ApplicationResponse> convertToSlice(List<ApplicationResponse> applications) {
        if (applications.isEmpty()) {
            return SliceResponse.of(applications, false, null);
        }

        boolean hasNext = existNextPage(applications);
        String nextCursor = generateCursor(applications);

        return SliceResponse.of(applications, hasNext, nextCursor);
    }

    private String generateCursor(List<ApplicationResponse> applications) {
        ApplicationResponse application = applications.get(applications.size() - 1);

        return String.valueOf(application.applicationId());
    }

    private boolean existNextPage(List<ApplicationResponse> applications) {
        if (applications.size() > PAGE_SIZE) {
            applications.remove(PAGE_SIZE);
            return true;
        }

        return false;
    }

    private BooleanExpression gtApplicationId(String cursor) {
        if (cursor != null) {
            return application.id.gt(Long.valueOf(cursor));
        }

        return null;
    }

    private BooleanExpression isDeletedEq(Boolean cond) {
        return application.isDelete.eq(cond);
    }
}

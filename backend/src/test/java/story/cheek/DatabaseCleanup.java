package story.cheek;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleanup implements InitializingBean {

    private static final String TRUNCATE_QUERY = "TRUNCATE TABLE %s";
    private static final String SET_INTEGRITY = "SET FOREIGN_KEY_CHECKS = %s";

    @PersistenceContext
    private EntityManager em;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = em.getMetamodel().getEntities().stream()
                .filter(entity -> entity.getJavaType().getAnnotation(Entity.class) != null)
                .map(entity -> {
                    Table table = entity.getJavaType().getAnnotation(Table.class);
                    if (table == null) {
                        return entity.getName().transform(camelToSnake());
                    } else {
                        return table.name();
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        em.createNativeQuery(String.format(SET_INTEGRITY, false)).executeUpdate();
        for (String tableName : tableNames) {
            em.createNativeQuery(String.format(TRUNCATE_QUERY, tableName)).executeUpdate();
        }

        em.createNativeQuery(String.format(SET_INTEGRITY, true)).executeUpdate();
    }

    private Function<String, String> camelToSnake() {
        return s -> s.replaceAll("(.)(\\p{Upper})", "$1_$2");
    }
}

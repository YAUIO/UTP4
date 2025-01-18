package db;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class Tools {
    public static void checkAndCommit(Object[] args, Field[] fields, Object target) {
        check(fields,args);
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(target);
        em.getTransaction().commit();
    }

    public static void checkAndCommit(Object[] args, Field[] fields, Object target, Boolean merge) {
        check(fields,args);
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        if (merge) {
            em.persist(em.merge(target));
        }
        em.getTransaction().commit();
    }

    private static void check(Field[] fields, Object[] args) {
        int i = 0;
        for (Field f : fields) {
            Optional<Annotation> searchAnnotation = Arrays.stream(f.getDeclaredAnnotations()).filter(a -> a.annotationType() == Column.class).findFirst();
            if (searchAnnotation.isPresent()) {
                Column specs = (Column) searchAnnotation.get();
                if (!specs.nullable() && args[i] == null) {
                    throw new NullPointerException("Argument " + f.getName() + " can't be null");
                }
            } else {
                throw new RuntimeException("Passed type isn't a column");
            }
        }
    }
}

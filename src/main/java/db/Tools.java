package db;

import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class Tools {
    public static void checkAndCommit(Object[] args, Field[] fields, Object target) {
        check(args, fields);
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(target);
        em.getTransaction().commit();
    }

    public static void checkAndCommit(Object[] args, Field[] fields, Object target, Boolean merge) {
        check(args, fields);
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        if (merge) {
            em.persist(em.merge(target));
        }
        em.getTransaction().commit();
    }

    private static void check( Object[] args, Field[] fields) {
        fields = Arrays.copyOfRange(fields, 1, fields.length);
        int i = 0;
        for (Field f : fields) {
            System.out.println(f.getName() + " " + args[i]);
            Optional<Annotation> searchAnnotation = Arrays.stream(f.getDeclaredAnnotations()).filter(a -> a.annotationType() == Column.class || a.annotationType() == JoinColumn.class).findFirst();
            if (searchAnnotation.isPresent()) {
                if (searchAnnotation.get().annotationType() == Column.class) {
                    Column specs = (Column) searchAnnotation.get();
                    checkArg(args[i],specs.nullable(),f);
                } else if (searchAnnotation.get().annotationType() == JoinColumn.class) {
                    JoinColumn specs = (JoinColumn) searchAnnotation.get();
                    checkArg(args[i],specs.nullable(),f);
                }
            } else {
                System.out.println(f.getName() + " " + Arrays.toString(f.getDeclaredAnnotations()));
                throw new RuntimeException("Passed type isn't a column");
            }
            i++;
        }
    }

    private static void checkArg(Object arg, Boolean nullable, Field f) {
        if (!nullable && arg == null) {
            throw new NullPointerException("Argument \"" + f.getName() + "\" can't be null");
        } else if (!nullable && arg.getClass() == String.class) {
            if (((String)arg).isEmpty() || ((String)arg).isBlank()) {
                throw new NullPointerException("Argument \"" + f.getName() + "\" can't be null");
            }
        }
    }
}

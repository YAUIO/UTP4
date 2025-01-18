package OtherTests;

import ClassTests.Utils;
import db.*;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Input Validation, Null and concurrency tests
 */
public class InputValidationTests {

    @BeforeAll
    public static void before() {
        Init.setDB("LibraryManagement");
        Init.getEntityManager();
    }

    @Test
    public void isbnTest() {
        Publisher p = new Publisher("I", "M", "+375293453646");
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            new Book("a", "b", p, 2007, "xd");
        });
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            new Book("a", "b", p, 2007, "LDFKSDKFPER");
        });
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            new Book("a", "b", p, 2007, "019345kd93");
        });

        Book b = new Book("a", "b", p, 2007, "9304825738");
        Assertions.assertNotNull(b);

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(b));
        em.remove(em.merge(p));
        em.getTransaction().commit();
    }

    @Test
    public void emailTest() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            new User("Artiom", "sdfsd", "+375293843284", "Warszawa");
        });
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            new User("Artiom", "sdfdsg@sdkgljd", "+375293843284", "Warszawa");
        });
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            new User("Artiom", "fdglfdgdfg@3;3.33", "+375293843284", "Warszawa");
        });

        User b = new User("Artiom", "nbyauio@gmail.com", "+375293843284", "Warszawa");
        Assertions.assertNotNull(b);

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(b));
        em.getTransaction().commit();
    }

    @Test
    public void phoneTest() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            new User("Artiom", "nbyauio@gmail.com", "+375", "Warszawa");
        });
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            new User("Artiom", "nbyauio@gmail.com", "37284", "Warszawa");
        });
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            new User("Artiom", "nbyauio@gmail.com", "+3752938432g84", "Warszawa");
        });

        User b = new User("Artiom", "nbyauio@gmail.com", "+375293843284", "Warszawa");
        Assertions.assertNotNull(b);

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(b));
        em.getTransaction().commit();
    }

    @Test
    public void nullableCheck() {
        HashMap<String, Object> validArgs = new HashMap<>();
        Publisher p = new Publisher("ge","ge","+456545654565");
        Book b = new Book("sdfdsf","sdfsdf",p,2007,"3453453453455");
        Copy copy = new Copy(b,0,"FREE");
        User u = new User("asd","sdfsdf@dsf.sdf","+75384834834","asdsfsdf345");

        validArgs.put("phonenumber","+3756874845");
        validArgs.put("email","nbjd@ldfs.dfd");
        validArgs.put("isbn","4534543454");
        validArgs.put("publisher",p);
        validArgs.put("book",b);
        validArgs.put("copy",copy);
        validArgs.put("user",u);

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage("db")
                        .addScanners(new SubTypesScanner(false))
        );
        reflections.getSubTypesOf(Object.class).stream()
                .filter(_class -> _class.getName().contains("db.")) //search for subclasses of package db
                .filter(_class -> _class.isAnnotationPresent(Entity.class))
                .forEach(_class -> {
                    Optional<Constructor<?>> search = Arrays.stream(_class.getDeclaredConstructors())
                            .filter(c -> c.isAnnotationPresent(FullArgsConstructor.class)).findFirst();
                    if (search.isPresent()) {
                        Field[] fields = _class.getDeclaredFields();
                        Constructor<?> constructor = search.get();
                        fields = Arrays.copyOfRange(fields, 1, fields.length);
                        Field[] finalFields = fields;
                        Arrays.stream(fields)
                                .forEach(f -> {
                                    Optional<Annotation> searchAnnotation = Arrays.stream(f.getDeclaredAnnotations()).filter(a -> a.annotationType() == Column.class || a.annotationType() == JoinColumn.class).findFirst();
                                    if (searchAnnotation.isPresent()) {
                                        int i = 0;
                                        Object[] args = new Object[finalFields.length];
                                        for (Field fi : finalFields){
                                            if (fi.equals(f)) {
                                                args[i] = null;
                                                i++;
                                                continue;
                                            }
                                            if (fi.getType().isAnnotationPresent(Entity.class)) {
                                                args[i] = validArgs.get(fi.getType().getSimpleName().toLowerCase());
                                            } else if (validArgs.containsKey(fi.getName().toLowerCase())) {
                                                args[i] = validArgs.get(fi.getName().toLowerCase());
                                            } else {
                                                if (fi.getType() == Integer.class) {
                                                    args[i] = 2000;
                                                } else if (fi.getType() == String.class) {
                                                    args[i] = "hello";
                                                } else if (fi.getType() == java.util.Date.class){
                                                    if (fi.getName().equalsIgnoreCase("borrowdate")) {
                                                        try {
                                                            args[i] = DateFormat.getDateInstance(DateFormat.SHORT).parse("01.01.2026");
                                                        } catch (ParseException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    } else {
                                                        try {
                                                            args[i] = DateFormat.getDateInstance(DateFormat.SHORT).parse("06.01.2026");
                                                        } catch (ParseException e) {
                                                            throw new RuntimeException(e);
                                                        }
                                                    }
                                                } else {
                                                    args[i] = new Object();
                                                    System.out.println("Unhandled type: " + fi.getType());
                                                }
                                            }
                                            i++;
                                        }
                                        if (searchAnnotation.get().annotationType() == Column.class) {
                                            Column specs = (Column) searchAnnotation.get();
                                            if (specs.nullable() || f.getName().equalsIgnoreCase("status")) { //status handles any wrong input internally
                                                Object o = null;
                                                try {
                                                    o = constructor.newInstance(args);
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                                Assertions.assertNotNull(o);
                                                EntityManager em = Init.getEntityManager();
                                                em.getTransaction().begin();
                                                em.remove(em.merge(o));
                                                em.getTransaction().commit();
                                            } else {
                                                Assertions.assertThrows(InvocationTargetException.class, () -> {
                                                    constructor.newInstance(args);
                                                });
                                            }
                                        } else if (searchAnnotation.get().annotationType() == JoinColumn.class) {
                                            JoinColumn specs = (JoinColumn) searchAnnotation.get();
                                            if (specs.nullable()) {
                                                Object o = null;
                                                try {
                                                    o = constructor.newInstance(args);
                                                } catch (Exception e) {
                                                    throw new RuntimeException(e);
                                                }
                                                Assertions.assertNotNull(o);
                                                EntityManager em = Init.getEntityManager();
                                                em.getTransaction().begin();
                                                em.remove(em.merge(o));
                                                em.getTransaction().commit();
                                            } else {
                                                Assertions.assertThrows(InvocationTargetException.class, () -> {
                                                    constructor.newInstance(args);
                                                });
                                            }
                                        }
                                    }
                                });
                    } else {
                        throw new RuntimeException("FullArgsConstructor is not present in class " + _class.getName());
                    }
                });

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(copy));
        em.remove(em.merge(b));
        em.remove(em.merge(p));
        em.remove(em.merge(u));
        em.getTransaction().commit();
    }

    @Test
    public void concurrencyTest() {
        Publisher p = new Publisher("ge","ge","+456545654565");
        Book b = Utils.getBook(p);
        User u = Utils.getUser();
        Copy c = new Copy(b,0,"FREE");

        Assertions.assertEquals(0, Init.getEntityManager().createQuery("SELECT b from Borrowing b where b.copy=:c", Borrowing.class)
                .setParameter("c",c)
                .getResultList().size());

        Thread t1 = new Thread(() -> {
            try {
                Borrowing bor = new Borrowing(u,c,DateFormat.getDateInstance(DateFormat.SHORT).parse("01.01.2026"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                Borrowing bor = new Borrowing(u,c,DateFormat.getDateInstance(DateFormat.SHORT).parse("01.01.2026"));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        List<Borrowing> borrowings = em.createQuery("SELECT b from Borrowing b where b.copy=:c", Borrowing.class)
                .setParameter("c",c)
                .getResultList();
        System.out.println(borrowings);
        Assertions.assertEquals(1, borrowings.size());

        for (Borrowing bor : borrowings) {
            List<String> list = em.createQuery("SELECT b.copy.status from Borrowing b where b.id=:id", String.class)
                    .setParameter("id",bor.getId())
                    .getResultList();
            System.out.println(list);
            Assertions.assertEquals(1, list.size());
            Assertions.assertEquals("BORROWED", list.getFirst());
            em.remove(em.merge(bor));
        }
        em.getTransaction().commit();

        em = Init.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(c));
        em.remove(em.merge(b));
        em.remove(em.merge(p));
        em.remove(em.merge(u));
        em.getTransaction().commit();
    }
}

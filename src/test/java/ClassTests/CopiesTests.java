package ClassTests;

import GUI.DisplayTable;
import GUI.LibrarianUI;
import GUI.TableWrapper;
import db.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CopiesTests {
    private static Copy b;
    private static Book book;
    private static Publisher p;

    @BeforeAll
    public static void before() {
        db.Init.setDB("LibraryManagement");
        db.Init.getEntityManager();
        p = new Publisher("hello", "its", "3466436436");
        book = Utils.getBook(p);
    }

    @AfterAll
    public static void after() {
        if (b != null) {
            try {
                EntityManager em = db.Init.getEntityManager();
                em.getTransaction().begin();
                em.remove(em.merge(book));
                em.remove(em.merge(p));
                em.getTransaction().commit();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @Order(0)
    public void createTest() {
        List<Copy> pre = Utils.getAllEntities(Copy.class);

        b = new Copy(book, 0, "BORROWED");

        List<Copy> after = Utils.getAllEntities(Copy.class);

        after.removeAll(pre);

        Assertions.assertEquals(b, after.getFirst());
    }

    @Test
    @Order(1)
    public void readTest() {
        DisplayTable dt = new DisplayTable(Copy.class);
        ArrayList<Object> objects = new ArrayList<>();
        Arrays.stream(dt.getClass().getDeclaredFields())
                .filter(f -> f.getName().equals("objects"))
                .forEach(f -> {
                    f.setAccessible(true);
                    try {
                        objects.addAll((ArrayList<?>) f.get(dt));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        List<Copy> query = Utils.getAllEntities(Copy.class);

        Assertions.assertEquals(objects.stream().toList(), query);
    }

    @Test
    @Order(2)
    public void updateTest() {
        List<Copy> pre = Utils.getAllEntities(Copy.class);
        b.setCopyNumber(2);
        List<Copy> after = Utils.getAllEntities(Copy.class);
        after.removeAll(pre);
        Optional<Field> res = Arrays.stream(after.getFirst().getClass().getDeclaredFields())
                .filter(f -> f.getName().equals("copyNumber"))
                .findFirst();

        Integer name = null;

        if (res.isPresent()) {
            res.get().setAccessible(true);
            try {
                name = (Integer) res.get().get(after.getFirst());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        Assertions.assertEquals(name, 2);
    }

    @Test
    @Order(3)
    public void removeFailTest() {
        List<Copy> before = Utils.getAllEntities(Copy.class);

        db.User user = Utils.getUser();
        Borrowing bor = null;
        try {
            bor = new Borrowing(user, b, DateFormat.getDateInstance(DateFormat.SHORT).parse("01.01.2026"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        LibrarianUI lu = new LibrarianUI(new Librarian());
        Arrays.stream(lu.getClass().getDeclaredFields()).filter(f -> f.getName().equals("frame")).forEach(f -> {
            f.setAccessible(true);
            try {
                ((TableWrapper) f.get(lu)).setVisible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Arrays.stream(lu.getClass().getDeclaredFields()).filter(f -> f.getName().equals("table"))
                        .forEach(f -> {
                            f.setAccessible(true);
                            try {
                                DisplayTable dt = new DisplayTable(Copy.class);
                                dt.getTable().setColumnSelectionInterval(0,0);
                                dt.getTable().setRowSelectionInterval(dt.getTable().getRowCount() - 1, dt.getTable().getRowCount() - 1);
                                f.set(lu, dt);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        });

        Arrays.stream(lu.getClass().getDeclaredMethods()).filter(m -> m.getName().equals("getDeleteLambda")).forEach(m -> {
            m.setAccessible(true);
            try {
                Runnable r = (Runnable) m.invoke(lu);
                r.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        EntityManager em = db.Init.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(bor));
        em.remove(em.merge(user));
        em.getTransaction().commit();

        Assertions.assertEquals(before, Utils.getAllEntities(Copy.class));
    }

    @Test
    @Order(4)
    public void deleteTest() {
        List<Copy> before = Utils.getAllEntities(Copy.class);
        before.remove(b);

        LibrarianUI lu = new LibrarianUI(new Librarian());
        Arrays.stream(lu.getClass().getDeclaredFields()).filter(f -> f.getName().equals("frame")).forEach(f -> {
            f.setAccessible(true);
            try {
                ((TableWrapper) f.get(lu)).setVisible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        Arrays.stream(lu.getClass().getDeclaredFields()).filter(f -> f.getName().equals("table"))
                .forEach(f -> {
                    f.setAccessible(true);
                    try {
                        DisplayTable dt = new DisplayTable(Copy.class);
                        dt.getTable().setColumnSelectionInterval(0,0);
                        dt.getTable().setRowSelectionInterval(dt.getTable().getRowCount() - 1, dt.getTable().getRowCount() - 1);
                        f.set(lu, dt);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });

        Arrays.stream(lu.getClass().getDeclaredMethods()).filter(m -> m.getName().equals("getDeleteLambda")).forEach(m -> {
            m.setAccessible(true);
            try {
                Runnable r = (Runnable) m.invoke(lu);
                r.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        List<Copy> after = Utils.getAllEntities(Copy.class);

        Assertions.assertEquals(before, after);
    }
}

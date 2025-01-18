package ClassTests;

import GUI.DisplayTable;
import GUI.LibrarianUI;
import GUI.TableWrapper;
import db.Book;
import db.Init;
import db.Publisher;
import db.Librarian;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Tests for Publisher entity
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PublisherTests {
    private static Publisher b;

    @BeforeAll
    public static void before() {
        db.Init.setDB("LibraryManagement");
        db.Init.getEntityManager();
    }

    @AfterAll
    public static void after() {
        if (b != null) {
            try {
                EntityManager em = db.Init.getEntityManager();
                em.getTransaction().begin();
                em.getTransaction().commit();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    @Order(0)
    public void createTest() {
        List<Publisher> pre = Utils.getAllEntities(Publisher.class);

        b = new Publisher("kgdfkgfd", "xd", "+5738492054");

        List<Publisher> after = Utils.getAllEntities(Publisher.class);

        after.removeAll(pre);

        Assertions.assertEquals(b, after.getFirst());
    }

    @Test
    @Order(1)
    public void readTest() {
        DisplayTable dt = new DisplayTable(Publisher.class);
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

        List<Publisher> query = Utils.getAllEntities(Publisher.class);

        Assertions.assertEquals(objects.stream().toList(), query);
    }

    @Test
    @Order(2)
    public void updateTest() {
        List<Publisher> pre = Utils.getAllEntities(Publisher.class);
        b.setName("9impulseGOAT");
        List<Publisher> after = Utils.getAllEntities(Publisher.class);
        after.removeAll(pre);
        Optional<Field> res = Arrays.stream(after.getFirst().getClass().getDeclaredFields())
                .filter(f -> f.getName().equals("name"))
                .findFirst();

        String name = null;

        if (res.isPresent()) {
            res.get().setAccessible(true);
            try {
                name = (String) res.get().get(after.getFirst());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        Assertions.assertEquals(name, "9impulseGOAT");
    }

    @Test //Return if Book.Publisher.getType == Publisher && Book.Publisher.getType != String
    @Order(3)
    public void removeFailTest() {
        List<Publisher> before = Utils.getAllEntities(Publisher.class);

        Book bk = new Book("h", "asd", b, 2006, "4305837283");

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
                        DisplayTable dt = new DisplayTable(Publisher.class);
                        dt.getTable().setColumnSelectionInterval(0, 0);
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

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(bk));
        em.getTransaction().commit();

        Assertions.assertEquals(before, Utils.getAllEntities(Publisher.class));
    }

    @Test
    @Order(4)
    public void deleteTest() {
        List<Publisher> before = Utils.getAllEntities(Publisher.class);
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
                        DisplayTable dt = new DisplayTable(Publisher.class);
                        dt.getTable().setColumnSelectionInterval(0, 0);
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

        List<Publisher> after = Utils.getAllEntities(Publisher.class);

        Assertions.assertEquals(before, after);
    }
}

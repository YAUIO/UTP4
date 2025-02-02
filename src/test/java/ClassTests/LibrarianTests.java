package ClassTests;

import GUI.DisplayTable;
import GUI.LibrarianUI;
import GUI.TableWrapper;
import db.*;
import db.Librarian;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Tests for Librarian entity
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LibrarianTests {
    private static Librarian b;
    private static User u;

    @BeforeAll
    public static void before() {
        db.Init.setDB("LibraryManagement");
        db.Init.getEntityManager();
        u = Utils.getUser();
    }

    @AfterAll
    public static void after() {
        if (b != null) {
            try {
                EntityManager em = db.Init.getEntityManager();
                em.getTransaction().begin();
                em.remove(em.merge(u));
                em.getTransaction().commit();
            } catch (Exception _) {}
        }
    }

    @Test
    @Order(0)
    public void createTest() {
        List<Librarian> pre = Utils.getAllEntities(Librarian.class);

        b = new Librarian(u, new Date(), "ret");

        List<Librarian> after = Utils.getAllEntities(Librarian.class);

        after.removeAll(pre);

        Assertions.assertEquals(b, after.getFirst());
    }

    @Test
    @Order(1)
    public void readTest() {
        DisplayTable dt = new DisplayTable(Librarian.class);
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

        List<Librarian> query = Utils.getAllEntities(Librarian.class);

        Assertions.assertEquals(objects.stream().toList(), query);
    }

    @Test
    @Order(2)
    public void updateTest() {
        List<Librarian> pre = Utils.getAllEntities(Librarian.class);
        b.setPosition("xd");
        List<Librarian> after = Utils.getAllEntities(Librarian.class);
        after.removeAll(pre);
        Optional<Field> res = Arrays.stream(after.getFirst().getClass().getDeclaredFields())
                .filter(f -> f.getName().equals("position"))
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

        Assertions.assertEquals(name, "xd");
    }


    @Test
    @Order(3)
    public void deleteTest() {
        List<Librarian> before = Utils.getAllEntities(Librarian.class);
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
                        DisplayTable dt = new DisplayTable(Librarian.class);
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

        List<Librarian> after = Utils.getAllEntities(Librarian.class);

        Assertions.assertEquals(before, after);
    }
}

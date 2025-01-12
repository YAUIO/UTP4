import GUI.DisplayTable;
import GUI.LibrarianUI;
import GUI.TableWrapper;
import db.Book;
import db.Librarian;
import db.Borrowing;
import db.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {
    private static User user;

    @BeforeAll
    public static void before() {
        db.Init.getEntityManager();
    }

    @AfterAll
    public static void after() {
        if (user != null) {
            try {
                EntityManager em = db.Init.getEntityManager();
                em.getTransaction().begin();
                em.remove(em.merge(user));
                em.getTransaction().commit();
            } catch (Exception _) {}
        }
    }

    @Test
    @Order(0)
    public void createTest() {
        List<User> pre = Utils.getAllEntities(User.class);

        user = Utils.getUser();

        List<User> after = Utils.getAllEntities(User.class);

        after.removeAll(pre);

        Assertions.assertEquals(user, after.getFirst());
    }

    @Test
    @Order(1)
    public void readTest() {
        DisplayTable dt = new DisplayTable(User.class);
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

        List<User> query = Utils.getAllEntities(User.class);

        Assertions.assertEquals(objects.stream().toList(), query);
    }

    @Test
    @Order(2)
    public void updateTest() {
        List<User> pre = Utils.getAllEntities(User.class);
        user.setName("Hello");
        List<User> after = Utils.getAllEntities(User.class);
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

        Assertions.assertEquals(name, "Hello");
    }

    @Test
    @Order(3)
    public void removeFailTest() {
        List<User> before = Utils.getAllEntities(User.class);

        Book book = Utils.getBook();
        Borrowing b = new Borrowing(user, book, new Date());

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
                                DisplayTable dt = new DisplayTable(User.class);
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
        em.remove(em.merge(b));
        em.remove(em.merge(book));
        em.getTransaction().commit();

        Assertions.assertEquals(before, Utils.getAllEntities(User.class));
    }

    @Test
    @Order(4)
    public void deleteTest() {
        List<User> before = Utils.getAllEntities(User.class);
        before.remove(user);

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
                        DisplayTable dt = new DisplayTable(User.class);
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

        List<User> after = Utils.getAllEntities(User.class);

        Assertions.assertEquals(before, after);
    }
}

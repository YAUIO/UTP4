import GUI.DisplayTable;
import GUI.LibrarianUI;
import GUI.TableWrapper;
import db.Book;
import db.Borrowing;
import db.Copy;
import db.Librarian;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookTests {
    private static Book b;
    private static Copy c;

    @BeforeAll
    public static void before() {
        db.Init.getEntityManager();
    }

    @AfterAll
    public static void after() {
        if (b != null) {
            try {
                EntityManager em = db.Init.getEntityManager();
                em.getTransaction().begin();
                em.remove(em.merge(c));
                em.remove(em.merge(b));
                em.getTransaction().commit();
            } catch (Exception _) {}
        }
    }

    @Test
    @Order(0)
    public void createTest() {
        List<Book> pre = Utils.getAllEntities(Book.class);

        b = Utils.getBook();

        List<Book> after = Utils.getAllEntities(Book.class);

        after.removeAll(pre);

        Assertions.assertEquals(b, after.getFirst());
    }

    @Test
    @Order(1)
    public void readTest() {
        DisplayTable dt = new DisplayTable(Book.class);
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

        List<Book> query = Utils.getAllEntities(Book.class);

        Assertions.assertEquals(objects.stream().toList(), query);
    }

    @Test
    @Order(2)
    public void updateTest() {
        List<Book> pre = Utils.getAllEntities(Book.class);
        b.setAuthor("Hello");
        List<Book> after = Utils.getAllEntities(Book.class);
        after.removeAll(pre);
        Optional<Field> res = Arrays.stream(after.getFirst().getClass().getDeclaredFields())
                .filter(f -> f.getName().equals("author"))
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
        List<Book> before = Utils.getAllEntities(Book.class);
        c = new Copy(b, 228, "asf");

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
                                DisplayTable dt = new DisplayTable(Book.class);
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
        em.remove(em.merge(c));
        em.getTransaction().commit();

        Assertions.assertEquals(before, Utils.getAllEntities(Book.class));
    }

    @Test
    @Order(4)
    public void deleteTest() {
        List<Book> before = Utils.getAllEntities(Book.class);
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
                        DisplayTable dt = new DisplayTable(Book.class);
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

        List<Book> after = Utils.getAllEntities(Book.class);

        Assertions.assertEquals(before, after);
    }
}

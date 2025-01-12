import GUI.DisplayTable;
import GUI.LibrarianUI;
import GUI.TableWrapper;
import db.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BorrowingTests {
    private static Borrowing b;
    private static Book bk;
    private static User u;
    private static Copy c;


    @BeforeAll
    public static void before() {
        db.Init.getEntityManager();
        System.out.println(Init.getEntityManager().createQuery("SELECT u FROM User u", User.class).getResultList());
        u = Utils.getUser();
        bk = Utils.getBook();
        c = new Copy(bk, 0, "xd");
    }

    @AfterAll
    public static void after() {
        try {
            EntityManager em = db.Init.getEntityManager();
            em.getTransaction().begin();
            em.remove(em.merge(c));
            em.remove(em.merge(u));
            em.remove(em.merge(bk));
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(0)
    public void createTest() {
        List<Borrowing> pre = Utils.getAllEntities(Borrowing.class);

        b = new Borrowing(u, c, new Date());

        List<Borrowing> after = Utils.getAllEntities(Borrowing.class);

        after.removeAll(pre);

        Assertions.assertEquals(b, after.getFirst());
    }

    @Test
    @Order(1)
    public void readTest() {
        DisplayTable dt = new DisplayTable(Borrowing.class);
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

        List<Borrowing> query = Utils.getAllEntities(Borrowing.class);

        Assertions.assertEquals(objects.stream().toList(), query);
    }

    @Test
    @Order(2)
    public void updateTest() {
        List<Borrowing> pre = Utils.getAllEntities(Borrowing.class);
        Date d = new Date();
        b.setReturnDate(d);
        List<Borrowing> after = Utils.getAllEntities(Borrowing.class);
        after.removeAll(pre);
        Optional<Field> res = Arrays.stream(after.getFirst().getClass().getDeclaredFields())
                .filter(f -> f.getName().equals("returnDate"))
                .findFirst();

        Date name = null;

        if (res.isPresent()) {
            res.get().setAccessible(true);
            try {
                name = (Date) res.get().get(after.getFirst());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        Assertions.assertEquals(name.hashCode(), d.hashCode());
    }

    @Test
    @Order(3)
    public void deleteTest() {
        List<Borrowing> before = Utils.getAllEntities(Borrowing.class);
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
                        DisplayTable dt = new DisplayTable(Borrowing.class);
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

        List<Borrowing> after = Utils.getAllEntities(Borrowing.class);

        Assertions.assertEquals(before, after);
    }
}

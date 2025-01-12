import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TestInit {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("LibraryManagementTestUnit");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}

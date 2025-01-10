package db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Init {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("LibraryManagement");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}

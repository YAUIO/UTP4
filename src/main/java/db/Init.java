package db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Init {
    private static EntityManagerFactory emf ;

    public static void setDB(String db) {
        emf = Persistence.createEntityManagerFactory(db);
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}

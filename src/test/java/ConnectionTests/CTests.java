package ConnectionTests;

import db.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CTests {
    @BeforeAll
    public static void init() {
        db.Init.setDB("LibraryManagementTestUnit");
        db.Init.getEntityManager();
    }

    @Test
    public void BorrowingUser() {
        User u = ClassTests.Utils.getUser();
    }
}

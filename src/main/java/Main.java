import java.sql.Date;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        if (args.length >= 3 && args.length <= 4) {
            db.User user;
            db.Librarian lb;
            try {
                user = db.Init.getEntityManager().createQuery("SELECT u FROM User u WHERE u.id = :id AND u.name = :name", db.User.class)
                        .setParameter("id", args[0])
                        .setParameter("name", args[1])
                        .getSingleResult();
            } catch (Exception e){return;}

            if (args.length == 4) {
                lb = new db.Librarian(user, Date.valueOf(args[2]), args[3]);
            } else {
                lb = new db.Librarian(user, Date.from(Instant.now()), args[2]);
            }

            System.out.println("Successfully added new Librarian: " + lb);
        } else {
            new GUI.Login();
        }
    }
}

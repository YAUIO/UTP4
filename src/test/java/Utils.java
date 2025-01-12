import db.Book;
import db.User;

import java.util.List;

public class Utils{
    public static<T> List<T> getAllEntities(Class<T> _class) {
        return db.Init.getEntityManager().createQuery("select u from " + _class.getName().substring(_class.getName().indexOf('.') + 1) +" u", _class).getResultList();
    }

    public static User getUser () {
        return new User("Artiom", "nby@gmail.com", "+234242324", "Krakow");
    }

    public static Book getBook () {
        return new Book("a","a","a",2007,"a");
    }
}

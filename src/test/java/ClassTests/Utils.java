package ClassTests;

import db.Book;
import db.Publisher;
import db.User;

import java.util.List;

/**
 * Utils for Entity testing
 */
public class Utils{
    public static<T> List<T> getAllEntities(Class<T> _class) {
        return db.Init.getEntityManager().createQuery("select u from " + _class.getName().substring(_class.getName().indexOf('.') + 1) +" u", _class).getResultList();
    }

    public static User getUser () {
        return new User("Artiom", "nby@gmail.com", "+2342425324", "Krakow");
    }

    public static Book getBook (Publisher p) {
        return new Book("a","a",p,2007,"8573849201");
    }
}

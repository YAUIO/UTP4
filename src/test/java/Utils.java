import java.util.List;

public class Utils{
    public static<T> List<T> getAllEntities(Class<T> _class) {
        return db.Init.getEntityManager().createQuery("select u from " + _class.getName().substring(_class.getName().indexOf('.') + 1) +" u", _class).getResultList();
    }
}

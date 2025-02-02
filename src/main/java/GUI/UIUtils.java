package GUI;

import db.Borrowing;
import db.Copy;
import db.Init;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Collection of repeating fragments of code
 */
public class UIUtils {
    /**
     * Checks if all arguments are acceptable in unique terms
     */
    protected static void checkIfUnique(Object[] args, DisplayTable table) throws ClassNotFoundException {
        ArrayList<Field> fields = new ArrayList<>(List.of(Class.forName(table.getClassName()).getDeclaredFields()).subList(1, args.length + 1));

        Class<?> _class = Class.forName(table.getClassName());

        Arrays.stream(_class.getDeclaredFields())
                .filter(f -> Arrays.stream(f.getDeclaredAnnotations())
                        .filter(a -> a.annotationType() == Column.class || a.annotationType() == JoinColumn.class)
                        .anyMatch(a -> {
                            if (a.annotationType() == Column.class && ((Column) a).unique()) {
                                return true;
                            } else return a.annotationType() == JoinColumn.class && ((JoinColumn) a).unique();}) || f.isAnnotationPresent(OneToOne.class))
                .forEach(f -> {
                    List<?> result = db.Init.getEntityManager().createQuery("SELECT f." + f.getName() + " from " + _class.getSimpleName() + " f", f.getType())
                            .getResultList();

                    if (result.contains(args[fields.indexOf(f)])) {
                        throw new RuntimeException(f.getName() + " must be unique");
                    }
                });
    }

    /**
     * Converts
     * @param arg from string to
     * @param types and outputs to
     * @param output array
     */
    protected static void parseArguments(Object[] output, String[] arg, Class<?>[] types, DisplayTable table) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, ParseException {
        Object[] args = output;
        if (args == null) {
            args = new Object[arg.length];
        }
        for (int i = 0; i < args.length; i++) {
            if (arg[i].equals("set")) continue;
            if (types[i] != String.class && types[i] != java.util.Date.class) {
                Optional<Method> res =
                        Arrays.stream(types[i].getDeclaredMethods())
                                .filter(m -> m.getParameterCount() == 1)
                                .filter(m -> m.getParameterTypes()[0] == String.class)
                                .filter(m -> m.getName().contains("value"))
                                .findFirst();
                if (res.isPresent()) {
                    args[i] = types[i].cast(res.get().invoke(null, arg[i]));
                }
            } else if (types[i] == java.util.Date.class) { //that type is ruining my beautiful code. like why do you need DateFormat.getDateInstance().parse()
                if (Arrays.stream(((Field)
                                Arrays.stream(Class.forName(table.getClassName())
                                        .getDeclaredFields()).toArray()[i + 1])
                                .getDeclaredAnnotations()).filter(a -> a.annotationType() == Column.class)
                        .anyMatch(a -> ((Column) a).nullable())) {
                    if (arg[i].isEmpty()) {
                        args[i] = null;
                        continue;
                    }
                }

                args[i] = DateFormat.getDateInstance(DateFormat.SHORT).parse(arg[i]);
            } else {
                args[i] = arg[i];
            }
        }
    }

    /**
     * Checks if there are any available copies for borrowing
     */
    public static synchronized boolean checkAvailableCopies(Integer bookId, @Nullable Copy copy) {
        List<?> copies =
                Init.getEntityManager()
                        .createQuery("SELECT c FROM Copy c WHERE c.book.id = :id", db.Copy.class)
                        .setParameter("id", bookId)
                        .getResultList();

        List<?> unReturnedBorrowings =
                Init.getEntityManager().createQuery("SELECT o FROM Borrowing o WHERE o.copy.book.id = :id", db.Borrowing.class)
                        .setParameter("id", bookId)
                        .getResultList();

        try {
            Field rDate = Borrowing.class.getDeclaredField("returnDate");
            rDate.setAccessible(true);
            unReturnedBorrowings = unReturnedBorrowings.stream().filter(b -> {
                try {
                    return rDate.get(b) == null || ((Date) rDate.get(b)).after(new Date());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (unReturnedBorrowings.size() >= copies.size()) {
            return false;
        }

        if (copy != null) {
            try {
                Field copyF = Borrowing.class.getDeclaredField("copy");
                copyF.setAccessible(true);
                return unReturnedBorrowings.stream()
                        .anyMatch(r -> {
                            try {
                                return !copyF.get(r).equals(copy);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}

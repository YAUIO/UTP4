package GUI;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UIUtils {
    protected static void checkIfUnique(Object[] args, DisplayTable table) throws ClassNotFoundException {
        ArrayList<Field> fields = new ArrayList<>(List.of(Class.forName(table.getClassName()).getDeclaredFields()).subList(1, args.length + 1));

        Class<?> _class = Class.forName(table.getClassName());

        Arrays.stream(_class.getDeclaredFields())
                .filter(f -> Arrays.stream(f.getDeclaredAnnotations())
                        .filter(a -> a.annotationType() == Column.class)
                        .anyMatch(a -> ((Column) a).unique()) || f.isAnnotationPresent(OneToOne.class))
                .forEach(f -> {
                    List<?> result = db.Init.getEntityManager().createQuery("SELECT f." + f.getName() + " from " + _class.getSimpleName() + " f", f.getType())
                                .getResultList();

                    if (result.contains(args[fields.indexOf(f)])) {
                        throw new RuntimeException(f.getName() + " must be unique");
                    }
                });
    }

    protected static void parseArguments (Object[] output, String[] arg, Class<?>[] types, DisplayTable table) throws InvocationTargetException, IllegalAccessException, ClassNotFoundException, ParseException {
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
}

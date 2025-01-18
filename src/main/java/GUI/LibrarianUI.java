package GUI;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import javax.swing.*;
import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

public class LibrarianUI {
    private final db.Librarian user;
    private DisplayTable table;
    private final TableWrapper frame;

    public LibrarianUI(db.Librarian user) {
        this.user = user;

        frame = new TableWrapper();

        JMenuBar jmb = new JMenuBar();

        jmb.add(getTableChooser(frame));

        jmb.add(getEditChooser());

        frame.setJMenuBar(jmb);
    }

    private JMenu getEditChooser() {
        JMenu tables = new JMenu("Edit");
        JMenuItem create = new JMenuItem("Create");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem duplicateWithNewId = new JMenuItem("Duplicate with new ID");

        tables.add(duplicateWithNewId);
        tables.add(delete);
        tables.add(create);

        duplicateWithNewId.addActionListener(_ -> getCopyLambda().run());

        delete.addActionListener(_ -> getDeleteLambda().run());

        create.addActionListener(_ -> getCreateLambda().run());

        return tables;
    }

    private JMenu getTableChooser(TableWrapper frame) {
        JMenu tables = new JMenu("Data");

        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .forPackage("db")
                        .addScanners(new SubTypesScanner(false)));

        reflections.getSubTypesOf(Object.class).stream()
                .filter(_class -> _class.getName().contains("db.")) //search for subclasses of package db
                .filter(_class -> _class.isAnnotationPresent(Entity.class))
                .forEach(_class -> {
                    JMenuItem jmi = new JMenuItem(_class.getName());
                    jmi.addActionListener(e -> actionTableListener(_class, frame));
                    tables.add(jmi);
                });

        return tables;
    }

    private void actionTableListener(Class<?> ent, TableWrapper tw) {
        table = new DisplayTable(ent);
        tw.changeTable(table);
    }

    private Runnable getDeleteLambda() {
        return () -> {
            Object o = null;
            int[] selectedRows = table.getTable().getSelectedRows();
            int counter = 0;

            for (int row : selectedRows) {
                try {
                    EntityManager em = db.Init.getEntityManager();
                    em.getTransaction().begin();
                    o = em.createQuery("SELECT o FROM " + table.getClassName() + " o WHERE o.id = :id", Class.forName(table.getClassName()))
                            .setParameter("id", Integer.parseInt((String) table.getValue(row - counter, 0)))
                            .getSingleResult();
                    boolean isIndependent = false;
                    if (o.getClass().isAnnotationPresent(Entity.class)) {
                        Reflections reflections = new Reflections(
                                new ConfigurationBuilder()
                                        .forPackage("db")
                                        .addScanners(new SubTypesScanner(false))
                        );
                        Object tempObject = o;
                        long dependentOn =
                                reflections.getSubTypesOf(Object.class).stream()
                                        .filter(_class -> _class.getName().contains("db.")) //search for subclasses of package db
                                        .filter(_class -> _class.isAnnotationPresent(Entity.class))
                                        .filter(_class ->
                                                Arrays.stream(_class.getDeclaredFields())
                                                        .anyMatch(f -> f.getType() == tempObject.getClass())
                                        ).filter(_class -> {
                                                    Optional<Method> getId =
                                                            Arrays.stream(tempObject.getClass().getDeclaredMethods())
                                                                    .filter(m -> m.getName().equalsIgnoreCase("getid"))
                                                                    .findFirst();
                                                    if (getId.isPresent()) {
                                                        try {
                                                            System.out.println(getId.get().invoke(tempObject));
                                                            return em.createQuery("SELECT o from " + _class.getSimpleName() +
                                                                            " o WHERE o." + tempObject.getClass().getSimpleName().toLowerCase() +
                                                                            ".id = :id", _class)
                                                                    .setParameter("id", getId.get().invoke(tempObject))
                                                                    .getResultStream()
                                                                    .findFirst().isPresent();
                                                        } catch (Exception e) {
                                                            System.out.println(e.getMessage() + " " + e.getClass());
                                                        }
                                                    }
                                                    return false;
                                                }
                                        ).count();

                        if (dependentOn == 0) {
                            isIndependent = true;
                        }
                    } else {
                        isIndependent = true;
                    }

                    if (isIndependent) {
                        em.remove(em.merge(o));
                        em.getTransaction().commit();
                    } else {
                        new Error("This record is used in other records, cannot delete it");
                    }
                    table = new DisplayTable(Class.forName(table.getClassName()));
                    frame.changeTable(table);
                } catch (Exception e) {
                    new Error(e);
                }
                counter++;
            }
        };
    }

    private Runnable getCreateLambda() {
        return () -> {
            JDialog dialog = new JDialog(frame, "Create");
            try {
                Optional<Constructor<?>> searchResult =
                        Arrays.stream(Class.forName(table.getClassName()).getDeclaredConstructors())
                                .filter(c -> c.isAnnotationPresent(FullArgsConstructor.class))
                                .findFirst();

                if (searchResult.isPresent()) {
                    Constructor<?> constructor = searchResult.get();

                    Class<?>[] types = constructor.getParameterTypes();

                    String[] typesStr = table.getHeader().split("\\s+");
                    ArrayList<JComponent> entryFields = new ArrayList<>(types.length);

                    for (Class<?> c : types) {
                        if (c.isAnnotationPresent(Entity.class)) {
                            try {
                                Object[] arr = db.Init.getEntityManager().createQuery("SELECT o FROM " + c.getSimpleName() + " o", c)
                                        .getResultList().toArray();

                                ScrollableList<Object> sel = new ScrollableList<>(new JList<>(arr));

                                entryFields.add(sel);
                            } catch (Exception _) {
                            }
                        } else {
                            entryFields.add(new JTextField());
                        }
                    }

                    JPanel selection = new JPanel(new BorderLayout());

                    JPanel fr = new JPanel(new GridLayout(1, types.length));

                    JPanel sr = new JPanel(new GridLayout(1, types.length));

                    for (String s : typesStr) {
                        fr.add(new JLabel(s));
                    }

                    for (JComponent jc : entryFields) {
                        sr.add(jc);
                    }

                    selection.add(fr, BorderLayout.NORTH);
                    selection.add(sr, BorderLayout.CENTER);

                    dialog.setLayout(new BorderLayout());
                    Dimension size = new Dimension(600 + (types.length - 2) * 140, 400);
                    dialog.setSize(size);
                    dialog.setPreferredSize(size);
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.add(new JLabel("Type in arguments to create new record"), BorderLayout.NORTH);
                    dialog.add(selection, BorderLayout.CENTER);
                    JButton submit = new JButton("Submit");
                    dialog.add(submit, BorderLayout.SOUTH);
                    submit.addActionListener(_ -> {
                        try {
                            String[] arg = new String[typesStr.length];
                            Object[] args = new Object[arg.length];
                            int c = 0;

                            for (JComponent j : entryFields) {
                                if (j.getClass() == JTextField.class) {
                                    arg[c] = ((JTextField) j).getText();
                                } else if (j.getClass() == ScrollableList.class) {
                                    arg[c] = "set";
                                    args[c] = ((ScrollableList<?>) j).getList().getSelectedValue();
                                }
                                c++;
                            }

                            UIUtils.parseArguments(args, arg, types, table);

                            UIUtils.checkIfUnique(args, table);

                            constructor.newInstance(args);

                            dialog.dispose();

                            table = new DisplayTable(Class.forName(table.getClassName()));

                            frame.changeTable(table);
                        } catch (Exception exc) {
                            if (exc.getCause() == null) {
                                new Error("Incorrect choice: " + exc.getClass().getName() + ": " + exc.getMessage(), dialog);
                            } else if (exc.getCause().getCause() == null) {
                                new Error("Incorrect choice: " + exc.getCause().getClass().getName() + ": " + exc.getCause().getMessage(), dialog);
                            } else {
                                new Error("Incorrect choice: " + exc.getCause().getCause().getClass().getName() + ": " + exc.getCause().getCause().getMessage(), dialog);
                            }
                        }
                    });

                } else {
                    throw new RuntimeException("No viable constructor found");
                }

                dialog.pack();
                dialog.setVisible(true);

            } catch (Exception er) {
                new Error("Incorrect choice: " + er.getClass().getName() + ": " + er.getMessage(), dialog);
            }
        };
    }

    private Runnable getCopyLambda() {
        return () -> {
            try {
                Object o = db.Init.getEntityManager().createQuery("SELECT o FROM " + table.getClassName() + " o WHERE o.id = :id", Class.forName(table.getClassName()))
                        .setParameter("id", Integer.parseInt((String) table.getValue(table.getTable().getSelectedRow(), 0)))
                        .getSingleResult();

                Object[] uniqueFields =
                        Arrays.stream(o.getClass().getDeclaredFields())
                                .filter(f -> !f.getName().equals("id"))
                                .filter(f -> Arrays.stream(f.getDeclaredAnnotations())
                                        .filter(a -> a.annotationType() == Column.class || a.annotationType() == JoinColumn.class)
                                        .anyMatch(a -> {
                                            if (a.annotationType() == Column.class && ((Column) a).unique()) {
                                                return true;
                                            } else if (a.annotationType() == JoinColumn.class && ((JoinColumn) a).unique()) {
                                                return true;
                                            }
                                            return f.isAnnotationPresent(OneToOne.class);
                                        })
                                ).toArray();

                if (uniqueFields.length == 0) {
                    Arrays.stream(Class.forName(table.getClassName()).getDeclaredConstructors())
                            .filter(c -> c.isAnnotationPresent(CopyConstructor.class))
                            .forEach(c -> {
                                try {
                                    c.newInstance(o);
                                    table.update();
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            });
                } else {
                    try {
                        JDialog jDialog = new JDialog(frame, "Duplicate with id: " + Integer.parseInt((String) table.getValue(table.getTable().getSelectedRow(), 0)));
                        Optional<Constructor<?>> searchResult =
                                Arrays.stream(Class.forName(table.getClassName()).getDeclaredConstructors())
                                        .filter(c -> c.isAnnotationPresent(FullArgsConstructor.class))
                                        .findFirst();

                        if (searchResult.isPresent()) {
                            Field[] fields = new Field[o.getClass().getDeclaredFields().length - 1];

                            for (int i = 0; i < fields.length; i++) {
                                int tempI = i;
                                Optional<Object> search = Arrays.stream(uniqueFields)
                                        .filter(f -> f.equals(o.getClass().getDeclaredFields()[tempI + 1]))
                                        .findFirst();
                                fields[i] = (Field) search.orElse(null);
                            }

                            Constructor<?> constructor = searchResult.get();

                            Class<?>[] types = constructor.getParameterTypes();

                            JComponent[] entryFields = new JComponent[types.length];

                            int listCount = 0;

                            for (int i = 0; i < types.length; i++) {
                                if (fields[i] == null) continue;
                                Class<?> c = types[i];
                                if (c.isAnnotationPresent(Entity.class)) {
                                    try {
                                        Object[] arr = db.Init.getEntityManager().createQuery("SELECT o FROM " + c.getSimpleName() + " o", c)
                                                .getResultList().toArray();

                                        ScrollableList<Object> sel = new ScrollableList<>(new JList<>(arr));

                                        listCount++;

                                        entryFields[i] = sel;
                                    } catch (Exception _) {
                                    }
                                } else {
                                    entryFields[i] = new JTextField();
                                }
                            }

                            JPanel selection = new JPanel(new BorderLayout());

                            JPanel fr = new JPanel(new GridLayout(1, fields.length));

                            JPanel sr = new JPanel(new GridLayout(1, fields.length));

                            for (Field field : fields) {
                                if (field != null) {
                                    fr.add(new JLabel(field.getName()));
                                }
                            }

                            for (JComponent jc : entryFields) {
                                if (jc != null) {
                                    sr.add(jc);
                                }
                            }

                            selection.add(fr, BorderLayout.NORTH);
                            selection.add(sr, BorderLayout.CENTER);

                            jDialog.setLayout(new BorderLayout());
                            Dimension sizeD = new Dimension(600 + (entryFields.length - 2) * 140, 400);
                            if (listCount == 0) {
                                sizeD.setSize(sizeD.getWidth(), 160);
                            }
                            jDialog.setSize(sizeD);
                            jDialog.setPreferredSize(sizeD);
                            jDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            jDialog.add(new JLabel("Type in unique arguments to duplicate a record"), BorderLayout.NORTH);
                            jDialog.add(selection, BorderLayout.CENTER);
                            JButton submitD = new JButton("Submit");
                            jDialog.add(submitD, BorderLayout.SOUTH);
                            submitD.addActionListener(_ -> {
                                try {
                                    String[] arg = new String[fields.length];
                                    Object[] args = new Object[fields.length];
                                    int c = 0;

                                    for (JComponent j : entryFields) {
                                        if (j == null) {
                                            Field f = o.getClass().getDeclaredFields()[c + 1];
                                            f.setAccessible(true);
                                            args[c] = f.get(o);
                                            arg[c] = "set";
                                            c++;
                                            continue;
                                        }
                                        if (j.getClass() == JTextField.class) {
                                            arg[c] = ((JTextField) j).getText();
                                        } else if (j.getClass() == ScrollableList.class) {
                                            arg[c] = "set";
                                            args[c] = ((ScrollableList<?>) j).getList().getSelectedValue();
                                        }
                                        c++;
                                    }

                                    UIUtils.parseArguments(args, arg, types, table);

                                    UIUtils.checkIfUnique(args, table);

                                    constructor.newInstance(args);

                                    jDialog.dispose();

                                    table = new DisplayTable(Class.forName(table.getClassName()));

                                    frame.changeTable(table);
                                } catch (Exception exc) {
                                    if (exc.getCause() == null) {
                                        new Error("Incorrect choice: " + exc.getClass().getName() + ": " + exc.getMessage());
                                    } else if (exc.getCause().getCause() == null) {
                                        new Error("Incorrect choice: " + exc.getCause().getClass().getName() + ": " + exc.getCause().getMessage());
                                    } else {
                                        new Error("Incorrect choice: " + exc.getCause().getCause().getClass().getName() + ": " + exc.getCause().getCause().getMessage());
                                    }
                                }
                            });

                            jDialog.pack();
                            jDialog.setVisible(true);
                        }
                    } catch (Exception excz) {
                        new Error("Incorrect choice: " + excz.getClass().getName() + ": " + excz.getMessage());
                    }
                }
                table = new DisplayTable(Class.forName(table.getClassName()));
                frame.changeTable(table);
            } catch (ArrayIndexOutOfBoundsException _) {
                new Error("Choose a row first");
            } catch (Exception exc) {
                new Error("Incorrect choice: " + exc.getClass().getName() + ": " + exc.getMessage());
            }
        };
    }
}

package GUI;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.Entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.*;

public class LibrarianUI {
    private final db.Librarian user;
    private DisplayTable table;
    private final TableWrapper frame;

    LibrarianUI(db.Librarian user) {
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

        duplicateWithNewId.addActionListener(e -> {
            JDialog dialog = new JDialog(frame, "Duplicate with new ID");
            dialog.setLayout(new BorderLayout(3, 1));
            Dimension size = new Dimension(400, 200);
            dialog.setSize(size);
            dialog.setPreferredSize(size);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.add(new JLabel("Type in ID to duplicate"));
            JTextField jt = new JTextField();
            dialog.add(jt);
            JButton submit = new JButton("Submit");
            dialog.add(submit);
            submit.addActionListener(_ -> {
                try {
                    Object o = db.Init.getEntityManager().createQuery("SELECT o FROM " + table.getClassName() + " o WHERE o.id = :id", Class.forName(table.getClassName()))
                            .setParameter("id", Integer.parseInt(jt.getText()))
                            .getSingleResult();

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
                } catch (Exception exc) {
                    new Error("Incorrect choice: " + exc.getClass().getName() + ": " + exc.getMessage(), dialog);
                }
            });

            dialog.pack();
            dialog.setVisible(true);
        });

        //delete.addActionListener();

        create.addActionListener(e -> {
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
                                Object[] arr = db.Init.getEntityManager().createQuery("SELECT o FROM " + c.getName().substring(c.getName().indexOf('.') + 1) + " o", c)
                                        .getResultList().toArray();

                                ScrollableList<Object> sel = new ScrollableList<>(new JList<>(arr));

                                entryFields.add(sel);
                            } catch (Exception _) {}
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
                    Dimension size = new Dimension(600 + (types.length-2)*140, 400);
                    dialog.setSize(size);
                    dialog.setPreferredSize(size);
                    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dialog.add(new JLabel("Type in arguments to create new record [HINT - DATE FORMAT YYYY-MM-DD]"), BorderLayout.NORTH);
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

                            for (int i = 0; i < args.length; i++) {
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
                                    args[i] = DateFormat.getDateInstance(DateFormat.SHORT).parse(arg[i]);
                                } else {
                                    if (arg[i].equals("set")) continue;
                                    args[i] = arg[i];
                                }
                            }

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
        });

        return tables;
    }

    private JMenu getTableChooser(TableWrapper frame) {
        JMenu tables = new JMenu("Data");
        JMenuItem users = new JMenuItem("Users");
        JMenuItem books = new JMenuItem("Books");
        JMenuItem borrowings = new JMenuItem("Borrowings");

        tables.add(users);
        tables.add(books);
        tables.add(borrowings);

        users.addActionListener(e -> actionTableListener(db.User.class, frame));
        books.addActionListener(e -> actionTableListener(db.Book.class, frame));
        borrowings.addActionListener(e -> actionTableListener(db.Borrowing.class, frame));

        return tables;
    }

    private void actionTableListener(Class<?> ent, TableWrapper tw) {
        table = new DisplayTable(ent);
        tw.changeTable(table);
    }
}

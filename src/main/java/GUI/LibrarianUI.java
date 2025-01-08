package GUI;

import db.CopyConstructor;
import db.FullArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Vector;

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
            dialog.setLayout(new GridLayout(3,1));
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
                            .setParameter("id",Integer.parseInt(jt.getText()))
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
            dialog.setLayout(new GridLayout(3,1));
            Dimension size = new Dimension(600, 200);
            dialog.setSize(size);
            dialog.setPreferredSize(size);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.add(new JLabel("Type in arguments ( " + table.getHeader() + " ) to create new record"));
            JTextField jt = new JTextField();
            dialog.add(jt);
            JButton submit = new JButton("Submit");
            dialog.add(submit);
            submit.addActionListener(_ -> {
                try {
                    String[] arg = jt.getText().split("\\s+");

                    Object[] args = new Object[arg.length];

                    for (int i = 0; i < args.length; i++) {
                        try {
                            args[i] = Integer.parseInt(arg[i]);
                            System.out.println(args[i] + " " + args[i].getClass().getName());
                        } catch (NumberFormatException _) {
                            args[i] = arg[i];
                        }
                    }

                    Arrays.stream(Class.forName(table.getClassName()).getDeclaredConstructors())
                            .filter(c -> c.isAnnotationPresent(FullArgsConstructor.class))
                            .forEach(c -> {
                                try {
                                    c.newInstance(args);
                                    table.update();
                                } catch (Exception ex) {
                                    throw new RuntimeException(ex);
                                }
                            });
                    table.update();
                    dialog.dispose();
                } catch (Exception exc) {
                    String[] arg = jt.getText().split("\\s+");
                    try {
                        Arrays.stream(Class.forName(table.getClassName()).getDeclaredConstructors())
                                .filter(c -> c.isAnnotationPresent(FullArgsConstructor.class))
                                .forEach(c -> {
                                    try {
                                        c.newInstance(arg);
                                        table.update();
                                    } catch (Exception ex) {
                                        throw new RuntimeException(ex);
                                    }
                                });
                        table.update();
                        dialog.dispose();
                    } catch (Exception _) {
                        new Error("Incorrect choice: " + exc.getClass().getName() + ": " + exc.getMessage(), dialog);
                    }
                }
            });

            dialog.pack();
            dialog.setVisible(true);
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
        DisplayTable dt = new DisplayTable(ent);
        tw.changeTable(dt);
        table = dt;
    }
}

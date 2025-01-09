package GUI;

import db.Book;
import db.Borrowing;
import db.Init;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.*;

public class UserUI {
    private final db.User user;
    private DisplayTable table;
    private final TableWrapper frame;
    private boolean isFiltered;

    UserUI(db.User user) {
        this.user = user;

        frame = new TableWrapper();

        frame.setEditable(false);

        JMenuBar jmb = new JMenuBar();

        jmb.add(getTableChooser(frame));

        jmb.add(getFilterChooser());

        frame.setJMenuBar(jmb);
    }

    private JMenu getFilterChooser() {
        JMenu tables = new JMenu("Filter");
        JMenuItem available = new JMenuItem("Available");

        tables.add(available);

        available.addActionListener(e -> {
            if (table.getClassName().equals(Book.class.getName()) && !isFiltered) {
                table = new DisplayTable(Book.class, book -> {
                    Optional<Field> fs =
                            Arrays.stream(book.getClass().getDeclaredFields())
                                    .filter(f -> f.getName().equals("id"))
                                    .findFirst();

                    if (fs.isPresent()) {
                        try {
                            fs.get().setAccessible(true);
                            Integer id = (Integer) fs.get().get(book);

                            List<?> copies =
                                    Init.getEntityManager()
                                            .createQuery("SELECT c FROM Copy c WHERE c.book.id = :id", db.Copy.class)
                                            .setParameter("id", id)
                                            .getResultList();

                            List<?> unReturnedBorrowings =
                                    Init.getEntityManager().createQuery("SELECT o FROM Borrowing o WHERE o.book.id = :id AND (o.returnDate = null OR o.returnDate > :date)", db.Borrowing.class)
                                            .setParameter("id", id)
                                            .setParameter("date", new Date())
                                            .getResultList();

                            return unReturnedBorrowings.size() < copies.size();
                        } catch (Exception ex) {
                            new Error(ex);
                        }
                    }
                    return false;
                });
                frame.changeTable(table);
                isFiltered = !isFiltered;
            } else if (table.getClassName().equals(Book.class.getName())) {
                table = new DisplayTable(Book.class);
                frame.changeTable(table);
                isFiltered = !isFiltered;
            }
        });

        return tables;
    }

    private JMenu getTableChooser(TableWrapper frame) {
        JMenu tables = new JMenu("Data");
        JMenuItem books = new JMenuItem("Books");
        JMenuItem borrowings = new JMenuItem("Borrowings");

        tables.add(books);
        tables.add(borrowings);

        books.addActionListener(e -> actionTableListener(db.Book.class, frame));
        borrowings.addActionListener(e -> {
            table = new DisplayTable(db.Borrowing.class, borrowing -> {
                return
                Init.getEntityManager().createQuery("SELECT o FROM Borrowing o WHERE o.user.id = :id", db.Borrowing.class)
                        .setParameter("id", this.user.getId())
                        .getResultStream()
                        .anyMatch(b -> Objects.equals(b.getId(), ((Borrowing) borrowing).getId()));
            });
            frame.changeTable(table);
        });

        return tables;
    }

    private void actionTableListener(Class<?> ent, TableWrapper tw) {
        table = new DisplayTable(ent);
        tw.changeTable(table);
    }
}

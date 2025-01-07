package GUI;

import javax.swing.*;

public class LibrarianUI {
    private final db.Librarian user;

    LibrarianUI(db.Librarian user) {
        this.user = user;

        TableWrapper frame = new TableWrapper();

        JMenuBar jmb = new JMenuBar();

        jmb.add(getTableChooser(frame));

        jmb.add(getEditChooser(frame));

        frame.setJMenuBar(jmb);
    }

    private JMenu getEditChooser(TableWrapper frame) {
        JMenu tables = new JMenu("Edit");
        JMenuItem create = new JMenuItem("Create");
        JMenuItem delete = new JMenuItem("Delete");
        JMenuItem duplicateWithNewId = new JMenuItem("Duplicate with new ID");

        tables.add(duplicateWithNewId);
        tables.add(delete);
        tables.add(create);

        /*duplicateWithNewId.addActionListener();
        delete.addActionListener();
        create.addActionListener();*/

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

        users.addActionListener(e -> actionTableListener(db.User.class,frame));
        books.addActionListener(e -> actionTableListener(db.Book.class,frame));
        borrowings.addActionListener(e -> actionTableListener(db.Borrowing.class,frame));

        return tables;
    }

    private void actionTableListener(Class<?> ent, TableWrapper tw) {
        DisplayTable dt = new DisplayTable(ent);
        tw.changeTable(dt);
    }
}

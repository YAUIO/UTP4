package GUI;

import javax.swing.*;
import java.awt.*;

public class LibrarianUI {
    private final db.Librarian user;

    LibrarianUI(db.Librarian user) {
        this.user = user;

        JFrame frame = new JFrame("Librarian Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        JButton addUserButton = new JButton("Add User");
        JButton editUserButton = new JButton("Edit User");
        JButton deleteUserButton = new JButton("Delete User");
        JButton viewBorrowingsButton = new JButton("View Borrowings");

        panel.add(addUserButton);
        panel.add(editUserButton);
        panel.add(deleteUserButton);
        panel.add(viewBorrowingsButton);

        frame.add(panel, BorderLayout.CENTER);

        addUserButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Add User clicked"));
        editUserButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Edit User clicked"));
        deleteUserButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Delete User clicked"));
        viewBorrowingsButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "View Borrowings clicked"));

        frame.setVisible(true);
    }
}

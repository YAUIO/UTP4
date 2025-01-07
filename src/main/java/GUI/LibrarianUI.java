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

        JButton addUserButton = new JButton("Users");
        JButton editUserButton = new JButton("Books");
        JButton deleteUserButton = new JButton("Borrowings");

        panel.add(addUserButton);
        panel.add(editUserButton);
        panel.add(deleteUserButton);

        frame.add(panel, BorderLayout.CENTER);

        addUserButton.addActionListener(e -> {
            frame.setVisible(false);
            frame.removeAll();
            frame.dispose();

            JFrame fr = new JFrame();
            fr.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            Dimension size = new Dimension(1280,720);
            fr.setSize(size);
            fr.setPreferredSize(size);
            DisplayTable users = new DisplayTable(db.User.class);
            fr.add(users.get());
            fr.pack();
            fr.setVisible(true);
        });
        editUserButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Edit User clicked"));
        deleteUserButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Delete User clicked"));

        frame.setVisible(true);
    }
}

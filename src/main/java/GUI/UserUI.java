package GUI;

import javax.swing.*;
import java.awt.*;

public class UserUI {
    private final db.User user;

    UserUI(db.User user) {
        this.user = user;

        JFrame frame = new JFrame("User " + user.getId() + "  Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton viewAllBooksButton = new JButton("View All Books");
        JButton viewAvailableBooksButton = new JButton("View Available Books");
        JButton viewBorrowingHistoryButton = new JButton("View Borrowing History");

        panel.add(viewAllBooksButton);
        panel.add(viewAvailableBooksButton);
        panel.add(viewBorrowingHistoryButton);

        frame.add(panel, BorderLayout.CENTER);

        viewAllBooksButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "View All Books clicked"));
        viewAvailableBooksButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "View Available Books clicked"));
        viewBorrowingHistoryButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "View Borrowing History clicked"));

        frame.setVisible(true);
    }
}

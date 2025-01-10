package GUI;

import db.User;
import jakarta.persistence.NoResultException;

import javax.swing.*;
import java.awt.*;

public class Login {
    public Login() {

        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Id:");
        JTextField usernameField = new JTextField();
        frame.add(usernameLabel);
        frame.add(usernameField);

        JLabel passwordLabel = new JLabel("Name:");
        JTextField passwordField = new JTextField();
        frame.add(passwordLabel);
        frame.add(passwordField);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        frame.add(registerButton);
        frame.add(loginButton);

        registerButton.addActionListener(e -> {
            frame.dispose();

            JFrame fframe = new JFrame("Register");
            fframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            fframe.setSize(300, 300);
            fframe.setLayout(new GridLayout(5, 2, 10, 10));

            JLabel fpasswordLabel = new JLabel("Name:");
            JTextField fpasswordField = new JTextField();
            fframe.add(fpasswordLabel);
            fframe.add(fpasswordField);

            JLabel emailLabel = new JLabel("Email:");
            JTextField emailField = new JTextField();
            fframe.add(emailLabel);
            fframe.add(emailField);

            JLabel phoneLabel = new JLabel("Phone:");
            JTextField phoneField = new JTextField();
            fframe.add(phoneLabel);
            fframe.add(phoneField);

            JLabel addressLabel = new JLabel("Address:");
            JTextField addressField = new JTextField();
            fframe.add(addressLabel);
            fframe.add(addressField);

            JButton floginButton = new JButton("Register");
            fframe.add(new JLabel());
            fframe.add(floginButton);

            floginButton.addActionListener(f -> {
                if (fpasswordField.getText() != null && emailField.getText() != null && phoneField.getText() != null && addressField.getText() != null) {
                    try {
                        db.Init.getEntityManager().createQuery("SELECT u FROM User u WHERE u.email = :email", db.User.class)
                                .setParameter("email", emailField.getText())
                                .getSingleResult();
                        JOptionPane.showMessageDialog(frame, "Such email already exists");
                    } catch (NoResultException _) {
                        fframe.dispose();
                        db.User user = new User(fpasswordField.getText(), emailField.getText(), phoneField.getText(), addressField.getText());
                        JOptionPane.showMessageDialog(null, "Welcome, " + fpasswordField.getText() + "! Make sure to write down your id: " + user.getId() + ", it will be required to log in");
                        login(user);
                    } catch (Exception ex) {
                        new Error(ex, frame);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "One of the fields is empty");
                }
            });

            fframe.setVisible(true);
        });

        loginButton.addActionListener(e -> {
            String selectedId = usernameField.getText();
            String selectedName = passwordField.getText();

            try {
                db.User user = db.Init.getEntityManager().createQuery("SELECT u FROM User u WHERE u.id = :id AND u.name = :name", db.User.class)
                        .setParameter("id", selectedId)
                        .setParameter("name", selectedName)
                        .getSingleResult();

                JOptionPane.showMessageDialog(frame, "Welcome, user" + user.getId());

                frame.dispose();
                login(user);
            } catch (NoResultException _) {
                new Error("Invalid username or id", frame);
            } catch (Exception ex) {
                new Error(ex);
            }
        });

        frame.setVisible(true);
    }

    private void login(db.User user) {
        try {
            db.Librarian lib = db.Init.getEntityManager().createQuery("SELECT l FROM Librarian l WHERE l.user = :u", db.Librarian.class)
                    .setParameter("u", user)
                    .getSingleResult();
            new LibrarianUI(lib);
        } catch (Exception _) {
            new UserUI(user);
        }
    }
}

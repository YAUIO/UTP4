package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

/**
 * User entity for JPA db
 */
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format."
    )
    private String email;

    @Column(nullable = false)
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Invalid phone number. It must be 10 to 15 digits and can start with '+'."
    )
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    public User() {
    }

    @CopyConstructor
    public User(User u) {
        this.name = u.name;
        this.email = u.email;
        this.phoneNumber = u.phoneNumber;
        this.address = u.address;
        Tools.checkAndCommit(new Object[]{name,email,phoneNumber,address}, this.getClass().getDeclaredFields(),this);
    }

    @FullArgsConstructor
    public User(String name, String email, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        Tools.checkAndCommit(new Object[]{name,email,phoneNumber,address}, this.getClass().getDeclaredFields(),this);
    }

    public Integer getId() {
        return id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        Tools.checkAndCommit(new Object[]{name,email,phoneNumber,address}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setName(String name) {
        this.name = name;
        Tools.checkAndCommit(new Object[]{name,email,phoneNumber,address}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setEmail(String email) {
        this.email = email;
        Tools.checkAndCommit(new Object[]{name,email,phoneNumber,address}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setAddress(String address) {
        this.address = address;
        Tools.checkAndCommit(new Object[]{name,email,phoneNumber,address}, this.getClass().getDeclaredFields(),this, true);
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            return this.hashCode() == o.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, phoneNumber, address);
    }
}

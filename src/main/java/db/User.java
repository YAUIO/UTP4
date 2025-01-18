package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

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
        throw new RuntimeException("Can't copy a user -> email must be unique");
    }

    @FullArgsConstructor
    public User(String name, String email, String phoneNumber, String address) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public Integer getId() {
        return id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setName(String name) {
        this.name = name;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setEmail(String email) {
        this.email = email;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setAddress(String address) {
        this.address = address;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
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

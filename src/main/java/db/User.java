package db;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    public User() {
    }

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
}

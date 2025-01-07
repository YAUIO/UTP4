package db;

import jakarta.persistence.*;

@Entity
@Table(name = "Publishers")
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    public Publisher() {}

    public Publisher(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public void setAddress(String address) {
        this.address = address;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public void setName(String name) {
        this.name = name;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    @Override
    public String toString() {
        return "Publisher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

package db;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Librarians")
public class Librarian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date employmentDate;

    @Column(nullable = false)
    private String position;

    public Librarian(User user, Date employmentDate, String position) {
        this.user = user;
        this.employmentDate = employmentDate;
        this.position = position;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public Librarian(){}

    public void setUser(User user) {
        this.user = user;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setPosition(String position) {
        this.position = position;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", user=" + user +
                ", employmentDate=" + employmentDate +
                ", position='" + position + '\'';
    }
}

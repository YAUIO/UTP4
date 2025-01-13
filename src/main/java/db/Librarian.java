package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

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

    @CopyConstructor
    public Librarian(Librarian l) {
        user = l.user;
        employmentDate = l.employmentDate;
        position = l.position;

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    @FullArgsConstructor
    public Librarian(User user, Date employmentDate, String position) {
        if (employmentDate.after(new Date())) {
            throw new RuntimeException("Can't be employed in future");
        }
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
        if (employmentDate.after(new Date())) {
            throw new RuntimeException("Can't be employed in future");
        }
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
                ", employmentDate=" + DateFormat.getDateInstance(DateFormat.SHORT).format(employmentDate) +
                ", position='" + position + '\'';
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
        return Objects.hash(id, user, DateFormat.getDateInstance(DateFormat.SHORT).format(employmentDate), position);
    }
}

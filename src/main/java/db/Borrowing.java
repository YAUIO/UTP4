package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "Borrowings")
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "copy_id", nullable = false)
    private Copy copy;

    @Column(nullable = false)
    private Date borrowDate;

    @Column()
    private Date returnDate;

    @FullArgsConstructor
    public Borrowing(User user, Copy book, Date borrowDate, Date returnDate) {
        this.user = user;
        this.copy = book;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public Borrowing(User user, Copy book, Date borrowDate) {
        this.user = user;
        this.copy = book;
        this.borrowDate = borrowDate;
        this.returnDate = null;

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    @CopyConstructor
    public Borrowing(Borrowing b) {
        user = b.user;
        copy = b.copy;
        borrowDate = b.borrowDate;
        returnDate = b.returnDate;

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public Borrowing(){}

    public Integer getId() {
        return id;
    }

    public void setCopy(Copy book) {
        this.copy = book;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setUser(User user) {
        this.user = user;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    @Override
    public String toString() {
        if (returnDate != null) {
            return "id=" + id +
                    ", user=" + user +
                    ", book=" + copy +
                    ", borrowDate=" + DateFormat.getDateInstance(DateFormat.SHORT).format(borrowDate) +
                    ", returnDate=" + DateFormat.getDateInstance(DateFormat.SHORT).format(returnDate);
        } else {
            return "id=" + id +
                    ", user=" + user +
                    ", book=" + copy +
                    ", borrowDate=" + DateFormat.getDateInstance(DateFormat.SHORT).format(borrowDate);
        }
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
        if (returnDate!= null) {
            return Objects.hash(id, user, copy, DateFormat.getDateInstance(DateFormat.SHORT).format(borrowDate), DateFormat.getDateInstance(DateFormat.SHORT).format(returnDate));
        } else {
            return Objects.hash(id, user, copy, DateFormat.getDateInstance(DateFormat.SHORT).format(borrowDate));
        }
    }
}

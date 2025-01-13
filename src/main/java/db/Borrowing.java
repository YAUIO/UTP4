package db;

import GUI.UIUtils;
import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
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
        if (borrowDate.before(new Date())) {
            throw new RuntimeException("BorrowDate should be after today");
        }
        if (returnDate.before(borrowDate)) {
            throw new RuntimeException("ReturnDate should be after BorrowDate");
        }
        if (!UIUtils.checkAvailableCopies(book.book.getId())) {
            throw new RuntimeException("There aren't any free copies");
        }
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
        if (borrowDate.before(new Date())) {
            throw new RuntimeException("BorrowDate should be after today");
        }
        if (!UIUtils.checkAvailableCopies(book.book.getId())) {
            throw new RuntimeException("There aren't any free copies");
        }

        book.setStatus("BORROWED");

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
        if (!UIUtils.checkAvailableCopies(b.copy.book.getId())) {
            throw new RuntimeException("There aren't any free copies");
        }

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
        if (borrowDate.before(returnDate)) {
            throw new RuntimeException("BorrowDate should be before ReturnDate");
        }
        this.borrowDate = borrowDate;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setReturnDate(Date returnDate) {
        if (returnDate.before(borrowDate)) {
            throw new RuntimeException("ReturnDate should be after BorrowDate");
        }
        this.returnDate = returnDate;
        if (returnDate.before(new Date())) {
            copy.setStatus("FREE");
        }
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
        if (returnDate != null) {
            return Objects.hash(id, user, copy, DateFormat.getDateInstance(DateFormat.SHORT).format(borrowDate), DateFormat.getDateInstance(DateFormat.SHORT).format(returnDate));
        } else {
            return Objects.hash(id, user, copy, DateFormat.getDateInstance(DateFormat.SHORT).format(borrowDate));
        }
    }
}

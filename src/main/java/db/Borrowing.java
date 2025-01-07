package db;

import jakarta.persistence.*;

import java.util.Date;

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
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Date borrowDate;

    private Date returnDate;

    public Borrowing(Integer id, User user, Book book, Date borrowDate, Date returnDate) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public Borrowing(Integer id, User user, Book book, Date borrowDate) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.borrowDate = borrowDate;
        this.returnDate = null;

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public Borrowing(){}

    public void setBook(Book book) {
        this.book = book;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public void setUser(User user) {
        this.user = user;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", user=" + user +
                ", book=" + book +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate;
    }
}

package db;

import jakarta.persistence.*;

@Entity
@Table(name = "Copies")
public class Copy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    Book book;

    @Column(nullable = false)
    private Integer copyNumber;

    @Column(nullable = false)
    private String status;

    @FullArgsConstructor
    public Copy(Book book, Integer copyNumber, String status) {
        this.book = book;
        this.copyNumber = copyNumber;
        this.status = status;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    @CopyConstructor
    public Copy(Copy c){
        book = c.book;
        copyNumber = c.copyNumber;
        status = c.status;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public Copy(){}

    public void setBook(Book book) {
        this.book = book;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setCopyNumber(Integer copyNumber) {
        this.copyNumber = copyNumber;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setStatus(String status) {
        this.status = status;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", book=" + book +
                ", copyNumber=" + copyNumber +
                ", status='" + status + '\'';
    }
}

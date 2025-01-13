package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;


import java.util.List;
import java.util.Objects;

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

    private enum states {
        FREE,
        BORROWED
    }

    @FullArgsConstructor
    public Copy(Book book, Integer copyNumber, String status) {
        states s = null;
        try {
            s = states.valueOf(status);
        } catch (Exception _){}
        if (status == null || s == null) {
            status = "FREE";
        }
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

    public Integer getId() {
        return id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            return this.hashCode() == o.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, book, copyNumber, status);
    }
}

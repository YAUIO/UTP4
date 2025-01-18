package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Publisher publisher;

    @Column(nullable = false)
    private Integer publicationYear;

    @Column (unique = true, nullable = false)
    private String isbn;

    public Book(){}

    @FullArgsConstructor
    public Book(String title, String author, Publisher publisher, Integer publicationYear, String isbn) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.isbn = isbn;

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    @CopyConstructor
    public Book(Book b) {
        title = b.title;
        author = b.author;
        publisher = b.publisher;
        publicationYear = b.publicationYear;
        isbn = b.isbn;

        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.persist(this);
        em.getTransaction().commit();
    }

    public Integer getId() {
        return id;
    }

    public void setAuthor(String author) {
        this.author = author;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    public void setTitle(String title) {
        this.title = title;
        EntityManager em = Init.getEntityManager();
        em.getTransaction().begin();
        em.merge(this);
        em.getTransaction().commit();
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publicationYear=" + publicationYear +
                ", isbn='" + isbn + '\'';
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
        return Objects.hash(id, title, author, publisher, publicationYear, isbn);
    }
}

package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

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
    @Pattern(
            regexp = "^(?:\\d{9}[\\dX]|\\d{13})$",
            message = "Invalid ISBN format. It must be a valid ISBN-10 or ISBN-13."
    )
    private String isbn;

    public Book(){}

    @FullArgsConstructor
    public Book(String title, String author, Publisher publisher, Integer publicationYear, String isbn) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.isbn = isbn;

        Tools.checkAndCommit(new Object[]{title,author,publisher,publicationYear,isbn}, this.getClass().getDeclaredFields(),this);
    }

    @CopyConstructor
    public Book(Book b) {
        title = b.title;
        author = b.author;
        publisher = b.publisher;
        publicationYear = b.publicationYear;
        isbn = b.isbn;

        Tools.checkAndCommit(new Object[]{title,author,publisher,publicationYear,isbn}, this.getClass().getDeclaredFields(),this);
    }

    public Integer getId() {
        return id;
    }

    public void setAuthor(String author) {
        this.author = author;
        Tools.checkAndCommit(new Object[]{title,author,publisher,publicationYear,isbn}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
        Tools.checkAndCommit(new Object[]{title,author,publisher,publicationYear,isbn}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
        Tools.checkAndCommit(new Object[]{title,author,publisher,publicationYear,isbn}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
        Tools.checkAndCommit(new Object[]{title,author,publisher,publicationYear,isbn}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setTitle(String title) {
        this.title = title;
        Tools.checkAndCommit(new Object[]{title,author,publisher,publicationYear,isbn}, this.getClass().getDeclaredFields(),this, true);
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

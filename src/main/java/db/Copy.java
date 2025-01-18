package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;


import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "Copies")
public class Copy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
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
        } catch (Exception _){
            System.out.println("Incorrect status, falling back to default {" + states.FREE + "}. List of states: " + Arrays.toString(states.values()));
        }
        if (status == null || s == null) {
            status = "FREE";
        }
        this.book = book;
        this.copyNumber = copyNumber;
        this.status = status;
        Tools.checkAndCommit(new Object[]{book,copyNumber,status}, this.getClass().getDeclaredFields(),this);
    }

    @CopyConstructor
    public Copy(Copy c){
        book = c.book;
        copyNumber = c.copyNumber;
        status = c.status;
        Tools.checkAndCommit(new Object[]{book,copyNumber,status}, this.getClass().getDeclaredFields(),this);
    }

    public Copy(){}

    public Integer getId() {
        return id;
    }

    public void setBook(Book book) {
        this.book = book;
        EntityManager em = Init.getEntityManager();
        Tools.checkAndCommit(new Object[]{book,copyNumber,status}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setCopyNumber(Integer copyNumber) {
        this.copyNumber = copyNumber;
        Tools.checkAndCommit(new Object[]{book,copyNumber,status}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setStatus(String status) {
        this.status = status;
        Tools.checkAndCommit(new Object[]{book,copyNumber,status}, this.getClass().getDeclaredFields(),this, true);
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

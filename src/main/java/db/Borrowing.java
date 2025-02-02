package db;

import GUI.UIUtils;
import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Borrowing entity for JPA db
 */
@Entity
@Table(name = "Borrowings")
public class Borrowing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
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
        if (returnDate != null && returnDate.before(borrowDate)) {
            throw new RuntimeException("ReturnDate should be after BorrowDate");
        }
        if (!UIUtils.checkAvailableCopies(book.book.getId(), copy)) {
            throw new RuntimeException("There aren't any free copies");
        }

        if (returnDate != null && returnDate.before(new Date())) {
            book.setStatus("FREE");
        } else {
            book.setStatus("BORROWED");
        }

        this.user = user;
        this.copy = book;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;

        Tools.checkAndCommit(new Object[]{user,copy,borrowDate,returnDate}, this.getClass().getDeclaredFields(),this);
    }

    public Borrowing(User user, Copy book, Date borrowDate) {
        if (borrowDate.before(new Date())) {
            throw new RuntimeException("BorrowDate should be after today");
        }
        if (!UIUtils.checkAvailableCopies(book.book.getId(), copy)) {
            throw new RuntimeException("There aren't any free copies");
        }

        book.setStatus("BORROWED");

        this.user = user;
        this.copy = book;
        this.borrowDate = borrowDate;
        this.returnDate = null;

        Tools.checkAndCommit(new Object[]{user,copy,borrowDate,returnDate}, this.getClass().getDeclaredFields(),this);
    }

    @CopyConstructor
    public Borrowing(Borrowing b) {
        if (!UIUtils.checkAvailableCopies(b.copy.book.getId(), copy)) {
            throw new RuntimeException("There aren't any free copies or the copy is already borrowed");
        }

        user = b.user;
        copy = b.copy;
        borrowDate = b.borrowDate;
        returnDate = b.returnDate;

        Tools.checkAndCommit(new Object[]{user,copy,borrowDate,returnDate}, this.getClass().getDeclaredFields(),this);
    }

    public Borrowing(){}

    public Integer getId() {
        return id;
    }

    public void setCopy(Copy book) {
        this.copy = book;
        Tools.checkAndCommit(new Object[]{user,copy,borrowDate,returnDate}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setBorrowDate(Date borrowDate) {
        if (borrowDate.before(returnDate)) {
            throw new RuntimeException("BorrowDate should be before ReturnDate");
        }
        this.borrowDate = borrowDate;
        Tools.checkAndCommit(new Object[]{user,copy,borrowDate,returnDate}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setReturnDate(Date returnDate) {
        if (returnDate.before(borrowDate)) {
            throw new RuntimeException("ReturnDate should be after BorrowDate");
        }
        this.returnDate = returnDate;
        if (returnDate.before(new Date())) {
            copy.setStatus("FREE");
        }
        Tools.checkAndCommit(new Object[]{user,copy,borrowDate,returnDate}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setUser(User user) {
        this.user = user;
        Tools.checkAndCommit(new Object[]{user,copy,borrowDate,returnDate}, this.getClass().getDeclaredFields(),this, true);
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

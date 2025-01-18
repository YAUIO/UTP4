package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Librarian entity for JPA db
 */
@Entity
@Table(name = "Librarians")
public class Librarian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(nullable = false)
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

        Tools.checkAndCommit(new Object[]{user,employmentDate,position}, this.getClass().getDeclaredFields(),this);
    }

    @FullArgsConstructor
    public Librarian(User user, Date employmentDate, String position) {
        if (employmentDate.after(new Date())) {
            throw new RuntimeException("Can't be employed in future");
        }
        this.user = user;
        this.employmentDate = employmentDate;
        this.position = position;
        Tools.checkAndCommit(new Object[]{user,employmentDate,position}, this.getClass().getDeclaredFields(),this);
    }

    public Librarian(){}

    public void setUser(User user) {
        this.user = user;
        Tools.checkAndCommit(new Object[]{user,employmentDate,position}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setEmploymentDate(Date employmentDate) {
        if (employmentDate.after(new Date())) {
            throw new RuntimeException("Can't be employed in future");
        }
        this.employmentDate = employmentDate;
        Tools.checkAndCommit(new Object[]{user,employmentDate,position}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setPosition(String position) {
        this.position = position;
        Tools.checkAndCommit(new Object[]{user,employmentDate,position}, this.getClass().getDeclaredFields(),this, true);
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

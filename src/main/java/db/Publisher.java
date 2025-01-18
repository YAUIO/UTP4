package db;

import db.Annotations.CopyConstructor;
import db.Annotations.FullArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.util.Objects;

@Entity
@Table(name = "Publishers")
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Invalid phone number. It must be 10 to 15 digits and can start with '+'."
    )
    private String phone;

    public Publisher() {}

    @CopyConstructor
    public Publisher(Publisher p) {
        name = p.name;
        address = p.address;
        phone = p.phone;
        Tools.checkAndCommit(new Object[]{name,address,phone}, this.getClass().getDeclaredFields(),this);
    }

    @FullArgsConstructor
    public Publisher(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        Tools.checkAndCommit(new Object[]{name,address,phone}, this.getClass().getDeclaredFields(),this);
    }

    public Integer getId() {
        return id;
    }

    public void setAddress(String address) {
        this.address = address;
        EntityManager em = Init.getEntityManager();
        Tools.checkAndCommit(new Object[]{name,address,phone}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setName(String name) {
        this.name = name;
        Tools.checkAndCommit(new Object[]{name,address,phone}, this.getClass().getDeclaredFields(),this, true);
    }

    public void setPhone(String phone) {
        this.phone = phone;
        Tools.checkAndCommit(new Object[]{name,address,phone}, this.getClass().getDeclaredFields(),this, true);
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'';
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
        return Objects.hash(id, name, address, phone);
    }
}

package ru.yandex.practicum.model.shoping;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Table(name = "users")
public class User  implements Persistable<Long> {
    @Id
    @Column("id")
    private Long id;

    @Column("firstname")
    private String firstName = "Jorg";

    @Column("lastname")
    private String lastName = "Born";

    @Column("sub")
    private String sub;

    @Transient
    private boolean isNew = false;

    public User() {}

    public User(Long id, String sub) {
        this.id = id;
        this.sub = sub;
        isNew=true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

}
package com.vincent.arrowsserver.model;


import com.vincent.arrowsserver.persistence.PasswordStorage;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;


    @Column(name = "password", nullable = false)
    private String password;

    public int getNumberUpvotes() {
        return numberUpvotes;
    }

    public void setNumberUpvotes(int numberUpvotes) {
        this.numberUpvotes = numberUpvotes;
    }

    @Column
    private int numberUpvotes = 0;

    public int getNumberDownvotes() {
        return numberDownvotes;
    }

    public void setNumberDownvotes(int numberDownvotes) {
        this.numberDownvotes = numberDownvotes;
    }

    @Column
    private int numberDownvotes = 0;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) throws PasswordStorage.CannotPerformOperationException {
        this.password = PasswordStorage.createHash(pass);
    }


}

package oth.ics.wtp.pollsbackend.entities;

import jakarta.persistence.*;

@Entity
public class AppUser {

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String username;

    private String hashedPassword;

    public AppUser() {
    }

    public AppUser(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
}
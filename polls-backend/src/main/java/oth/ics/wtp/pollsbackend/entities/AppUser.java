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

    private java.time.LocalDate passwordChangedAt;

    public java.time.LocalDate getPasswordChangedAt() { return passwordChangedAt; }
    public void setPasswordChangedAt(java.time.LocalDate date) { this.passwordChangedAt = date; }

    //localhost:8080/swagger-ui/index.html
    //http://localhost:7070/?server=db
    /*
    {
      "title": "Team lunch",
      "description": "Help plan lunch",
      "dueDate": "2026-07-01",
      "questions": [
        { "text": "What did you think?", "type": "PLAIN_TEXT" },
        { "text": "Would you return?", "type": "BOOLEAN" },
        { "text": "Rate the food", "type": "NUMERIC" }
      ]
    }
     */
}
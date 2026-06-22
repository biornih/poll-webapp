package oth.ics.wtp.pollsbackend.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Answer {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Question question;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Invitation invitation;

    private String textValue;
    private Boolean booleanValue;
    private Integer numericValue;

    public Answer() {}

    public Answer(Question question, Invitation invitation,
                  String textValue, Boolean booleanValue,
                  Integer numericValue) {
        this.question = question;
        this.invitation = invitation;
        this.textValue = textValue;
        this.booleanValue = booleanValue;
        this.numericValue = numericValue;
    }

    public long getId() { return id; }
    public Question getQuestion() { return question; }
    public Invitation getInvitation() { return invitation; }
    public String getTextValue() { return textValue; }
    public Boolean getBooleanValue() { return booleanValue; }
    public Integer getNumericValue() { return numericValue; }
}
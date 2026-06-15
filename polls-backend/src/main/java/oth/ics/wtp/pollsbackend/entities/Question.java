package oth.ics.wtp.pollsbackend.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Question {

    @Id
    @GeneratedValue
    private long id;

    private String text;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Poll poll;

    public Question() {}

    public Question(String text, QuestionType type, Poll poll) {
        this.text = text;
        this.type = type;
        this.poll = poll;
    }

    public long getId() { return id; }
    public String getText() { return text; }
    public QuestionType getType() { return type; }
    public Poll getPoll() { return poll; }
    public void setText(String text) { this.text = text; }
    public void setType(QuestionType type) { this.type = type; }
}
package oth.ics.wtp.pollsbackend.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title"})
})
public class Poll {

    @Id
    @GeneratedValue
    private long id;

    private String title;

    private String description;

    private LocalDate dueDate;

    private boolean finished = false;

    @ManyToOne
    private AppUser creator;

    @OneToMany(mappedBy = "poll", fetch = FetchType.EAGER)
    private List<Question> questions;

    public Poll() {}

    public Poll(String title, String description,
                LocalDate dueDate, AppUser creator) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.creator = creator;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isFinished() { return finished; }
    public AppUser getCreator() { return creator; }
    public List<Question> getQuestions() { return questions; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setFinished(boolean finished) { this.finished = finished; }
}
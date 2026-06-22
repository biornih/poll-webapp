package oth.ics.wtp.pollsbackend.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Invitation {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Poll poll;

    @ManyToOne
    private AppUser invitee;

    private boolean hasAnswered = false;

    public Invitation() {}

    public Invitation(Poll poll, AppUser invitee) {
        this.poll = poll;
        this.invitee = invitee;
    }

    public long getId() { return id; }
    public Poll getPoll() { return poll; }
    public AppUser getInvitee() { return invitee; }
    public boolean isHasAnswered() { return hasAnswered; }
    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }
}
package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participation_requests", uniqueConstraints = @UniqueConstraint(name = "uq_requester_event", columnNames = {
"requester_id", "event_id"
}))
public class ParticipationRequestEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private EventEntity event;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private UserEntity requester;
    @Column(nullable = false) private LocalDateTime created;
    @Column(nullable = false, length = 20) private String status;

    protected ParticipationRequestEntity() {}


    public ParticipationRequestEntity(EventEntity event, UserEntity requester) {
this.event = event;this.requester = requester;this.created = LocalDateTime.now();this.status = "PENDING";
}


    public Long getId() {
return id;
}


    public EventEntity getEvent() {
return event;
}


    public UserEntity getRequester() {
return requester;
}


    public LocalDateTime getCreated() {
return created;
}


    public String getStatus() {
return status;
}


    public void setStatus(String status) {
this.status = status;
}

}

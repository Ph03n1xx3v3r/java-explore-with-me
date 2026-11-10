package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", indexes = {
@Index(name = "idx_event_date_state", columnList = "event_date,state"), 
        @Index(name = "idx_event_category", columnList = "category_id")
})
public class EventEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 2000) private String annotation;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private CategoryEntity category;
    @Column(nullable = false, length = 7000) private String description;
    @Column(name = "event_date", nullable = false) private LocalDateTime eventDate;
    @Column(nullable = false) private float lat;
    @Column(nullable = false) private float lon;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private UserEntity initiator;
    @Column(nullable = false) private boolean paid;
    @Column(name = "participant_limit", nullable = false) private int participantLimit;
    @Column(name = "request_moderation", nullable = false) private boolean requestModeration;
    @Column(nullable = false, length = 20) private String state;
    @Column(nullable = false, length = 120) private String title;
    @Column(name = "created_on", nullable = false) private LocalDateTime createdOn;
    @Column(name = "published_on") private LocalDateTime publishedOn;

    protected EventEntity() {}


    public EventEntity(String annotation, CategoryEntity category, String description, LocalDateTime eventDate, 
            float lat, float lon, UserEntity initiator, boolean paid, int participantLimit, 
            boolean requestModeration, String title) {

        this.annotation = annotation; this.category = category; this.description = description; this.eventDate = eventDate;
        this.lat = lat; this.lon = lon; this.initiator = initiator; this.paid = paid; this.participantLimit = participantLimit;
        this.requestModeration = requestModeration; this.title = title; this.state = "PENDING"; this.createdOn = LocalDateTime.now();

}


    public Long getId() {
return id;
}


    public String getAnnotation() {
return annotation;
}


    public CategoryEntity getCategory() {
return category;
}


    public String getDescription() {
return description;
}


    public LocalDateTime getEventDate() {
return eventDate;
}


    public float getLat() {
return lat;
}


    public float getLon() {
return lon;
}


    public UserEntity getInitiator() {
return initiator;
}


    public boolean isPaid() {
return paid;
}


    public int getParticipantLimit() {
return participantLimit;
}


    public boolean isRequestModeration() {
return requestModeration;
}


    public String getState() {
return state;
}


    public String getTitle() {
return title;
}


    public LocalDateTime getCreatedOn() {
return createdOn;
}


    public LocalDateTime getPublishedOn() {
return publishedOn;
}


    public void update(String annotation, CategoryEntity category, String description, LocalDateTime eventDate, 
            Float lat, Float lon, Boolean paid, Integer participantLimit, Boolean requestModeration, String title) {

        if (annotation != null)this.annotation = annotation; if (category != null)this.category = category;
        if (description != null)this.description = description; if (eventDate != null)this.eventDate = eventDate;
        if (lat != null)this.lat = lat; if (lon != null)this.lon = lon; if (paid != null)this.paid = paid;
        if (participantLimit != null)this.participantLimit = participantLimit; if (requestModeration != null)this.requestModeration = requestModeration;
        if (title != null)this.title = title;

}


    public void publish() {
state = "PUBLISHED";publishedOn = LocalDateTime.now();
}


    public void reject() {
state = "CANCELED";
}


    public void cancel() {
state = "CANCELED";
}


    public void sendToReview() {
state = "PENDING";
}

}

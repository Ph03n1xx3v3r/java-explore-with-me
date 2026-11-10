package ru.practicum.ewm.main.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compilations")
public class CompilationEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 120) private String title;
    @Column(nullable = false) private boolean pinned;
    @ManyToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.BatchSize(size = 50) @JoinTable(name = "compilation_events", joinColumns = @JoinColumn(name = "compilation_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<EventEntity>  events = new ArrayList<>();

    protected CompilationEntity() {}


    public CompilationEntity(String title, boolean pinned, List<EventEntity>  events) {
this.title = title;this.pinned = pinned;if (events != null)this.events = new ArrayList<>(events);
}


    public Long getId() {
return id;
}


    public String getTitle() {
return title;
}


    public boolean isPinned() {
return pinned;
}


    public List<EventEntity>  getEvents() {
return events;
}


    public void update(String title, Boolean pinned, List<EventEntity>  events) {
if (title != null)this.title = title;if (pinned != null)this.pinned = pinned;if (events != null) {
this.events.clear();this.events.addAll(events);
}
}

}

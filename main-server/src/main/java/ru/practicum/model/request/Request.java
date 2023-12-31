package ru.practicum.model.request;

import lombok.Data;
import ru.practicum.model.enums.Status;
import ru.practicum.model.event.Event;
import ru.practicum.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    User requester;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    Status status;
    @Column(name = "created")
    LocalDateTime created;
}

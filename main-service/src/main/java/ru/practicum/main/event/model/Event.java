package ru.practicum.main.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity

@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    @Length(min = 2, max = 2000)
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Length(min = 20, max = 7000)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @Column(columnDefinition = "boolean default false")
    private boolean paid;

    @Column(name = "participant_limit", columnDefinition = "integer default 0")
    private Integer participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation", columnDefinition = "boolean default true")
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventState state;

    @Column(nullable = false)
    @Length(min = 3, max = 120)
    private String title;

    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilations;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private Set<Request> requests;

    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;

}

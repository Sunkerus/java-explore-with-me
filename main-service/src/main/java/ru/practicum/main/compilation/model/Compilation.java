package ru.practicum.main.compilation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.event.model.Event;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "compilations")
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean pinned;


    @Column(unique = true)
    private String title;


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "compilation_event",
    joinColumns = @JoinColumn(name = "compilation_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"))
    private List<Event> events;
}

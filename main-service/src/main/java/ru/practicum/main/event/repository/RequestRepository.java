package ru.practicum.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.event.enums.RequestStatus;
import ru.practicum.main.event.model.Request;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("select r from Request r " +
            "left join Event e on e.id = r.event.id " +
            "left join User u on u.id = e.initiator.id " +
            "where u.id = :userId and r.event.id = :eventId ")
    List<Request> findAllByEventAndOwner(@Param("userId") Long userId, @Param("eventId") Long eventId);

    List<Request> findAllByRequesterId(Long requesterId);

    List<Request> findAllByIdIn(List<Long> requestIds);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findByStatusAndEvent_IdIn(RequestStatus status, Collection<Long> eventId);

}

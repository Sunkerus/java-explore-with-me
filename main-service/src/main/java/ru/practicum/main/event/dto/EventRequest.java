package ru.practicum.main.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.event.enums.EventSortType;
import ru.practicum.main.event.enums.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EventRequest {


    private HttpServletRequest request;
    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private boolean onlyAvailable;
    private EventSortType sort;
    private Integer from;
    private Integer size;

    public static EventRequest of(
            HttpServletRequest request,
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            boolean onlyAvailable,
            EventSortType sort,
            Integer from,
            Integer size) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setRequest(request);
        eventRequest.setFrom(from);
        eventRequest.setPaid(paid);
        eventRequest.setSize(size);
        eventRequest.setOnlyAvailable(onlyAvailable);
        eventRequest.setText(text);
        eventRequest.setRangeEnd(rangeEnd);
        eventRequest.setRangeStart(rangeStart);
        eventRequest.setCategories(categories);
        eventRequest.setSort(sort);

        return eventRequest;
    }

    public static AdminRequest ofAdmin(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setUsers(users);
        adminRequest.setStates(states);
        adminRequest.setCategories(categories);
        adminRequest.setRangeStart(rangeStart);
        adminRequest.setRangeEnd(rangeEnd);
        adminRequest.setFrom(from);
        adminRequest.setSize(size);

        return adminRequest;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class AdminRequest {

        private List<Long> users;
        private List<EventState> states;
        private List<Long> categories;
        private LocalDateTime rangeStart;
        private LocalDateTime rangeEnd;
        private Integer from;
        private Integer size;
    }
}

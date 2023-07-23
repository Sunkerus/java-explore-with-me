package ru.practicum.main.event.dto;

import lombok.*;
import ru.practicum.main.event.enums.RequestStatus;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatus {

    private List<Long> requestIds;

    private RequestStatus status;
}

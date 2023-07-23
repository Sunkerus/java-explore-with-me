package ru.practicum.main.user.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserShortDto {

    private Long id;

    private String name;
}

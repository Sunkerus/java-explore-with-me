package ru.practicum.common.structures;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class HitDto {

    @NotBlank(message = "App cannot be blank")
    private String app;

    @NotBlank(message = "URI cannot be blank")
    private String uri;

    @NotBlank(message = "ip cannot be blank")
    private String ip;

    @NotBlank(message = "Timestamp cannot be blank")
    private String timestamp;
}

package ru.practicum.common.structures;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TransferStats {

    private String app;

    private String uri;

    private Long hits;
}

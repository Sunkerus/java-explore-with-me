package ru.practicum.main.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    private Set<Long> events = new HashSet<>();

    private boolean pinned;


    @Size(min = 1, max = 50, message = "Title length must be between 1 to 50")
    @NotBlank(message = "Title cannot be blank")
    private String title;
}

package ru.practicum.main.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentIncomeDto {

    @Size(min = 3, max = 2047, message = "Size of comment must be between 3 and 2047 symbols")
    @NotBlank(message = "Comment cannot be blank")
    private String text;
}

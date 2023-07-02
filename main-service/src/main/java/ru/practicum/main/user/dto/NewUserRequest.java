package ru.practicum.main.user.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@Getter
public class NewUserRequest {

    @Size(min = 6, max = 254, message = "The email must be at least 2 characters long and no more than 250 characters.")
    @Email(message = "Invalid email.")
    @NotBlank
    private String email;

    @Size(min = 2, max = 250, message = "The name must be at least 6 characters long and no more than 250 characters.")
    @NotBlank(message = "The name cannot be empty")
    private String name;
}

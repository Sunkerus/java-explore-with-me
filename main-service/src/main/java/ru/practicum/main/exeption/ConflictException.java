package ru.practicum.main.exeption;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}

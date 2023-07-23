package ru.practicum.main.exeption;

public class IssueException extends RuntimeException {
    public IssueException(String message) {
        super(message);
    }
}
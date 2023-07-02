package ru.practicum.main.helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class FurtherPageRequest extends PageRequest {

    private final int from;

    public FurtherPageRequest(int from, int size) {
        super(from, size, Sort.unsorted());
        this.from = from;
    }

    public FurtherPageRequest(int from, int size, Sort sort) {
        super(from, size, sort);
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }
}

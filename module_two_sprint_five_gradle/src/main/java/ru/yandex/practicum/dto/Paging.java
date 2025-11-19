package ru.yandex.practicum.dto;

public record Paging (
        int pageSize,
        int pageNumber,
        boolean hasPrevious,
        boolean hasNext) {

    public Paging(int pageSize, int pageNumber, boolean hasPrevious, boolean hasNext) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
    }

    @Override
    public int pageSize() {
        return pageSize;
    }

    @Override
    public int pageNumber() {
        return pageNumber;
    }

    @Override
    public boolean hasPrevious() {
        return hasPrevious;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }
}

package ru.yandex.practicum.dto.payment;

public record StatusDto (
    Long orderId,
    Long total,
    ResultStatus status)
{
}

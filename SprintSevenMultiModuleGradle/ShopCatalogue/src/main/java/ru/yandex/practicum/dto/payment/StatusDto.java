package ru.yandex.practicum.dto.payment;

public record StatusDto (
    Long orderId,
    ResultStatus status)
{
}

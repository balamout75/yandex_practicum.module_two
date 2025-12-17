package ru.yandex.practicum.dto.payment;

public record BalanceDto (
    Long userId,
    Long balance,
    BalanceStatus status)
{
}

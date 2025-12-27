package ru.yandex.practicum.dto.shoping;

import java.io.Serializable;

public record UserDto (
        long id,
        String firstName,
        String lastName,
        String sub) implements Serializable {
    public UserDto(long id, String firstName, String lastName, String sub) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sub = sub;
    }
}


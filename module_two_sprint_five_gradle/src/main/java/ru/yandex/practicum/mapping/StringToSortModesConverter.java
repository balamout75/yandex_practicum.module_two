package ru.yandex.practicum.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.convert.converter.Converter;
//import com.fasterxml.jackson.databind.util.Converter;

public class StringToSortModesConverter implements Converter<String, SortModes> {
    @Override
    public SortModes convert(String source) {
        try {
            return SortModes.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return SortModes.NO;
        }
    }

    public StringToSortModesConverter() {
    }
}
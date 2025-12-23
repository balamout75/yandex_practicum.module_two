package ru.yandex.practicum.mapping;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

public class ConverterRegistrator {

    FormatterRegistry registry;

    public ConverterRegistrator(FormatterRegistry registry) {
        this.registry = registry;
        registry.addConverter(new StringToActionModesConverter());
        registry.addConverter(new StringToSortModesConverter());
    }


}

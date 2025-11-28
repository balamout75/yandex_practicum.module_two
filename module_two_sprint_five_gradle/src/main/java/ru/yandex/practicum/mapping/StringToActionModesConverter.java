package ru.yandex.practicum.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.convert.converter.Converter;


import javax.swing.*;

public class StringToActionModesConverter implements Converter<String, ActionModes> {
    @Override
    public ActionModes convert(String source) {
        try {
            return ActionModes.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ActionModes.NOTHING;
        }
    }

    public StringToActionModesConverter() {
    }

    /*@Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(ActionModes.class);
    }*/
}
package com.donglaistd.jinli.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.concurrent.atomic.AtomicInteger;

@Converter
public class AtomicIntConverter implements AttributeConverter<AtomicInteger, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AtomicInteger attribute) {
        return attribute.get();
    }

    @Override
    public AtomicInteger convertToEntityAttribute(Integer dbData) {
        return new AtomicInteger(dbData);
    }

}
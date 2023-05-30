package com.kolip.findiksepeti.converters;

public interface Converter<T, R> {
    R convert(T beConverted);

    R convert(T beConverted, R beMerged);
}

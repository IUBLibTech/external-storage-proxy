package org.fcrepo.camel.external.storage.common;

import java.io.IOException;
import java.util.Map;

import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringToMapTypeConverter implements TypeConverters {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static JavaType mapType;

    static {
        mapType = mapper.getTypeFactory().constructMapType(Map.class,
                String.class, Object.class);
    }

    @Converter
    public Map<String, Object> toMap(String map) throws IOException {
        return mapper.readValue(map, mapType);
    }
}

package com.kolip.findiksepeti.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Convert Filter Json in string to List of Filter.
 */
@Component
public class FilterConverter {
    private Logger logger = LoggerFactory.getLogger(FilterConverter.class);

    private ObjectMapper mapper;

    public FilterConverter() {
        mapper = new ObjectMapper();
    }

    public List<Filter> convert(String filtersJson) {
        Filter[] filters = new Filter[0];
        try {
            filters = StringUtils.hasText(filtersJson) ? mapper.readValue(filtersJson, Filter[].class) : new Filter[0];
        } catch (UnrecognizedPropertyException ex) {
            logger.warn("Called with unregnized paramaters {}", filtersJson);
            ex.printStackTrace();
        } catch (JsonProcessingException e) {
            logger.warn("Exception occured while mapping filter json {}", filtersJson);
            e.printStackTrace();
        }
        return Arrays.asList(filters);
    }
}

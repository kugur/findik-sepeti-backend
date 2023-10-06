package com.kolip.findiksepeti.pagination;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Component
public class PageRequestConverter {
    private Logger logger = LoggerFactory.getLogger(PageRequestConverter.class);

    private ObjectMapper mapper;
    private final int defaultPage = 0;
    private final int defaultPageSize = 10;
    private final Sort defaultSort = Sort.by(Sort.Direction.ASC, "Id");

    public PageRequestConverter() {
        mapper = new ObjectMapper();
    }

    public PageRequest convert(String pageInfoJson) {
        PageRequestModel pageInfo;
        try {
            pageInfo = mapper.readValue(pageInfoJson, PageRequestModel.class);
        } catch (JsonProcessingException e) {
            return PageRequest.of(defaultPage, defaultPageSize, defaultSort);
        }
        return convert(pageInfo);
    }

    public PageRequest convert(PageRequestModel pageInfo) {

        return PageRequest.of(pageInfo.getPage(), pageInfo.getSize(), convertSort(pageInfo.getSort()));
    }

    private Sort convertSort(String sortRequestParam) {
        if (!StringUtils.hasText(sortRequestParam)) {
            return defaultSort;
        }

        String[] sortValues = sortRequestParam.split(",");
        if (sortValues.length >= 1 &&
                (sortValues[0].toUpperCase().equals(Sort.Direction.ASC.name()) ||
                        sortValues[0].toUpperCase().equals(Sort.Direction.DESC.name()))) {
            return Sort.by(Sort.Direction.valueOf(sortValues[0]),
                    Arrays.copyOfRange(sortValues, 1, sortValues.length));
        }
        return defaultSort;
    }
}

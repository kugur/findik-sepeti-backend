package com.kolip.findiksepeti.pagination;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class PageRequestConverterTest {

    private PageRequestConverter converter;

    @BeforeEach
    public void setup() {
        converter = new PageRequestConverter();
    }

    @Test
    public void convert_WithEmptyString_ReturnZeroPageTenSizeOrderById() {
        //Initialize
        String pageInfoJson = "";

        //Run test
        PageRequest pageRequest = converter.convert(pageInfoJson);

        //Verify
        assertNotNull(pageRequest);
        assertEquals(pageRequest.getPageNumber(), 0, "If request param page info empty, pageNumber should return 0");
        assertEquals(pageRequest.getPageSize(), 10, "If requestParam pageInfo is empty, it should return 10 for pageSize");
        assertEquals(Sort.unsorted(), pageRequest.getSort());
    }

    @Test
    public void convert_WithAttributes_ReturnPageRequest() {
        //Initialize
        String pageInfoJson = "{\"page\":1,\"size\":5, \"sort\":\"ASC,id\"}";

        //Run test
        PageRequest pageRequest = converter.convert(pageInfoJson);

        //Verify
        assertNotNull(pageRequest);
        assertEquals(1, pageRequest.getPageNumber(), "pageRequest pageNumber should equal page value.");
        assertEquals(5, pageRequest.getPageSize());
        assertEquals(Sort.by(Sort.Direction.ASC, "id"), pageRequest.getSort());
    }

    @Test
    public void convert_WithInvalidPageCount_ReturnDefaultRequest() {
        //Initialize
        String pageInfoJson = "{\"invalidPage\":1,\"size\":5, \"sort\":\"ASC,id\"}";

        //Run test
        PageRequest pageRequest = converter.convert(pageInfoJson);

        //Verify
        assertNotNull(pageRequest);
        assertEquals(pageRequest.getPageNumber(), 0, "If request param page info empty, pageNumber should return 0");
        assertEquals(pageRequest.getPageSize(), 10, "If requestParam pageInfo is empty, it should return 10 for pageSize");
        assertEquals(pageRequest.getSort(), Sort.unsorted());
    }


    @Test
    public void convert_WithInvalidSort_ReturnDefaultSort() {
        //Initialize
        String pageInfoJson = "{\"page\":1,\"size\":5, \"sort\":\"InvalidASC,id\"}";

        //Run test
        PageRequest pageRequest = converter.convert(pageInfoJson);

        //Verify
        assertNotNull(pageRequest);
        assertEquals(pageRequest.getPageNumber(), 1);
        assertEquals(pageRequest.getPageSize(), 5);
        assertEquals(pageRequest.getSort(), Sort.unsorted());
    }
}
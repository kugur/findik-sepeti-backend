package com.kolip.findiksepeti.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilterConverterTest {

    private FilterConverter filterConverter;

    @BeforeEach
    public void setUp() {
        filterConverter = new FilterConverter();
    }

    @Test
    public void convert_WithoutFilters_ReturnEmptyList() {
        //initialize Test
        String filterJson = "";

        //Run test
        List<Filter> filters = filterConverter.convert(filterJson);
        //verify results
        assertNotNull(filters, "If filter string is empty, should return empty list");
        assertEquals(0, filters.size(), "If filter string empty, should return empty list");
    }

    @Test
    public void converter_WithValidFilter_ReturnFilterListWithOneElement() throws Exception {
        //Setup mock items
        String filterJson = "[{\"name\":\"category\",\"operation\":\"" +
                FilterOperations.EQUAL.name() +
                "\",\"value\":\"22\"}]";

        // Run test
        List<Filter> filterList = filterConverter.convert(filterJson);

        // Verify results
        assertNotNull(filterList);
        assertNotNull(filterList, "If filter string is empty, should return empty list");
        assertEquals(1, filterList.size(), "If filter string empty, should return empty list");
        assertEquals(filterList.get(0), new Filter("category", FilterOperations.EQUAL, "22"));
    }

    @Test
    public void covert_WithTwoArrayFilterList_ReturnTwoFilters() {
        //initialize Test
        String filterJson = "[{\"name\":\"category\", \"operation\":\"EQUAL\" , \"value\":\"raw\"}," +
                "{\"name\":\"price\", \"operation\":\"GREATER\" , \"value\":\"22\"}]";

        //run test
        List<Filter> filters = filterConverter.convert(filterJson);

        //Verify
        assertEquals(2, filters.size());
        assertEquals(filters.get(0), new Filter("category", FilterOperations.EQUAL, "raw"));
        assertEquals(filters.get(1), new Filter("price", FilterOperations.GREATER, "22"));
    }

    @Test
    public void convert_WithInvalidFilterAttribute_ReturnEmptyFilter() {
        //initialize Test
        String filterJson = "[{\"InvalidName\":\"category\", \"operation\":\"EQUAL\" , \"value\":\"raw\"}," +
                "{\"name\":\"price\", \"operation\":\"GREATER\" , \"value\":\"22\"}]";

        //run test
        List<Filter> filters = filterConverter.convert(filterJson);

        //Verify
        assertEquals(0, filters.size(), "If There is invalid attribute , should return empty list.");
    }

    @Test
    public void convert_WithInvalidOperationValue_ReturnEmptyFilter() {
        //initialize Test
        String filterJson = "[{\"name\":\"category\", \"operation\":\"INVALIDEQUAL\" , \"value\":\"raw\"}," +
                "{\"name\":\"price\", \"operation\":\"GREATER\" , \"value\":\"22\"}]";

        //run test
        List<Filter> filters = filterConverter.convert(filterJson);

        //Verify
        assertEquals(0, filters.size(), "If There is invalid attribute , should return empty list.");

    }

    @Test
    public void convert_WithNull_ReturnEmptyList() {
        //Initialize Test
        String filterJson = null;

        //Run test
        List<Filter> filters = filterConverter.convert(filterJson);

        //Verify
        assertEquals(0, filters.size(), "If FilterJson is null, it should return empty list");
    }
}
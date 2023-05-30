package com.kolip.findiksepeti.filters;

import java.util.Objects;

/**
 * Store attributes that received from http request to use in querying data on database.
 */
public class Filter {
    String name;
    FilterOperations operation;
    String value;

    public Filter() {
    }

    public Filter(String name, FilterOperations operation, String value) {
        System.out.println("Filter created");
        this.name = name;
        this.operation = operation;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOperation(FilterOperations operation) {
        this.operation = operation;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public FilterOperations getOperation() {
        return operation;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "name='" + name + '\'' +
                ", operation='" + operation + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return Objects.equals(name, filter.name) && Objects.equals(operation, filter.operation) && Objects.equals(value, filter.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, operation, value);
    }
}

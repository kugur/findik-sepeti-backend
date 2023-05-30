package com.kolip.findiksepeti.pagination;

import java.io.Serializable;

public class PageRequestModel implements Serializable {

    int page;
    int size;
    String sort;

    public PageRequestModel() {
    }

    public PageRequestModel(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public PageRequestModel(int page, int size, String sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSort() {
        return sort;
    }
}

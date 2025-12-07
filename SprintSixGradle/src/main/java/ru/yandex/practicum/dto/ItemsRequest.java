package ru.yandex.practicum.dto;

import ru.yandex.practicum.mapper.ActionModes;
import ru.yandex.practicum.mapper.SortModes;

public class ItemsRequest{
        private Long id=0L;
        private String search="";
        private SortModes sort=SortModes.NO;
        private Integer pageSize = 5;
        private Integer pageNumber =1;
        private ActionModes action = ActionModes.NOTHING;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public SortModes getSort() {
        return sort;
    }

    public void setSort(SortModes sort) {
        this.sort = sort;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public ActionModes getAction() {
        return action;
    }

    public void setAction(ActionModes action) {
        this.action = action;
    }
}
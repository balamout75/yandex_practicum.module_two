package ru.yandex.practicum.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PageDto implements Serializable {

        String name="1";
        List<ItemDto> items;

        public PageDto() {
        }

        public PageDto(List<ItemDto> items) {
                this.items = items;
        }

        @Override
        public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                PageDto pageDto = (PageDto) o;
                return Objects.equals(name, pageDto.name) && Objects.equals(items, pageDto.items);
        }

        @Override
        public int hashCode() {
                return Objects.hash(name, items);
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public List<ItemDto> getItems() {
                System.out.println("ItemGet "+items.size());
                return items;
        }

        public void setItems(List<ItemDto> items) {
                System.out.println("ItemSet "+items.size());
                this.items = items;
        }

}


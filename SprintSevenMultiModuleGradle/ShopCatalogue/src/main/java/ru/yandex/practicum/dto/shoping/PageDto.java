package ru.yandex.practicum.dto.shoping;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PageDto implements Serializable {

        Long userId;

        List<ItemDto> items;

        public PageDto() {
        }

        public PageDto(Long userId, List<ItemDto> items) {
                this.userId = userId;
                this.items = items;
        }

        @Override
        public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                PageDto pageDto = (PageDto) o;
                return Objects.equals(userId, pageDto.userId) && Objects.equals(items, pageDto.items);
        }

        @Override
        public int hashCode() {
                return Objects.hash(userId, items);
        }

        public Long getUserId() {
                return userId;
        }

        public void setUserId(Long userId) {
                this.userId = userId;
        }

        public List<ItemDto> getItems() {
                return items;
        }

        public void setItems(List<ItemDto> items) {
                this.items = items;
        }

}


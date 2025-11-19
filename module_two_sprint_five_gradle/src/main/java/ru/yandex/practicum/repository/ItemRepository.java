package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.dto.ItemDto;
import ru.yandex.practicum.model.Item;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    //@Query("select distinct p from Post p join p.tags t where p.title ilike :searchSubString and lower(t.tag) in (:tags) group by p.id having count(p.id) = :tagCount")
    @NativeQuery(value = "select i.*, coalesce(ic.count, 0) as count from items i left join in_card ic on i.id = ic.item_id")
    Page<ItemDto> findMyItems(Pageable pageable);





}

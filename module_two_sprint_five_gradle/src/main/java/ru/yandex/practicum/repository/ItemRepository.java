package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.model.Item;

import java.util.List;


@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {




}

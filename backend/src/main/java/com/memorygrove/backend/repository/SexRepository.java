package com.memorygrove.backend.repository;

import com.memorygrove.backend.dictionary.Sex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы со справочником полов пользователя
 */
@Repository
public interface SexRepository extends JpaRepository<Sex, UUID> {

    /**
     * Поиск пола по наименованию
     * @param name наименование пола
     * @return Optional с найденным полом, либо пустой Optional
     */
    Optional<Sex> findByName(String name);

    /**
     * Проверка сущестовавния пола по наименованию
     * @param name наименование пола
     * @return true существует, false - не существует
     */
    boolean existsByName(String name);
}

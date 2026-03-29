package com.memorygrove.backend.repository;

import com.memorygrove.backend.dictionary.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы со справочником ролей пользователя
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    /**
     * Поиск роли по наименованию
     * @param name наименование роли
     * @return Optional с найденной ролью, либо пустой Optional
     */
    Optional<Role> findByName(String name);

    /**
     * Проверка существования роли по наименованию
     * @param name наименование роли
     * @return true - существует, false - не существует
     */
    boolean existsByName(String name);
}

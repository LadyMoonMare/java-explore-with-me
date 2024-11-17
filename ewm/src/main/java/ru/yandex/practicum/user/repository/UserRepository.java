package ru.yandex.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "select * from app_users limit ?2 offset ?1",nativeQuery = true)
    List<User> findAllButLimit(Integer from, Integer size);
}

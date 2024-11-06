package ru.yandex.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "select * from app_users limit ?1",nativeQuery = true)
    List<User> findAllButSize(Integer size);

    @Query(value = "select * from app_users limit ?1,?2",nativeQuery = true)
    List<User> findAllButLimit(Integer from, Integer size);
}

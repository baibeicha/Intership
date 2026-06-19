package com.intership.userservice.repository;

import com.intership.userservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    void deleteById(Long id);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAllByIdIn(List<Long> ids);

    @Modifying
    @Query(value = "UPDATE users SET name = :name, surname = :surname, birth_date = :birth_date WHERE id = :id",
            nativeQuery = true)
    void updateById(@Param("id") Long id,
                    @Param("name") String name,
                    @Param("surname") String surname,
                    @Param("birth_date") LocalDate birth_date);
}

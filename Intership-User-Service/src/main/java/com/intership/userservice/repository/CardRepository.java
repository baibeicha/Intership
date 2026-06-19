package com.intership.userservice.repository;

import com.intership.userservice.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Card save(Card card);

    Optional<Card> findById(Long id);

    void deleteById(Long id);

    List<Card> findAllByIdIn(List<Long> ids);

    List<Card> findAllByUserId(Long userId);

    @Modifying
    @Query(value = "UPDATE card_info SET number = :number, holder = :holder, " +
            "expiration_date = :expirationDate WHERE id = :id", nativeQuery = true)
    void updateById(@Param("id") Long id,
                    @Param("number") String number,
                    @Param("holder") String holder,
                    @Param("expirationDate") String expirationDate);

    long findUserIdById(Long id);
}

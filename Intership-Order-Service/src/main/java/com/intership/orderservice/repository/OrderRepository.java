package com.intership.orderservice.repository;

import com.intership.orderservice.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByIdIn(List<Long> ids);
    List<Order> findByStatusIn(List<Order.Status> statuses);

    @Modifying
    @Query("update Order o set o.userEmail =:userEmail, o.status = :status where o.id = :id")
    int updateById(@Param("id") Long id, @Param("userEmail") String userEmail, @Param("status") Order.Status status);

    @Modifying
    @Query("update Order o set o.status = :status where o.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") Order.Status status);
}

package com.zalando.ecommerce.repository;

import com.zalando.ecommerce.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {

}

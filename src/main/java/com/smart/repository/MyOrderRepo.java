package com.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.model.MyOrder;

public interface MyOrderRepo extends JpaRepository<MyOrder, Long>{
	
	public MyOrder findByOrderId(String orderId);

}

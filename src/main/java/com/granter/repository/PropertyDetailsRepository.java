package com.granter.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.granter.entity.PropertyDetails;

public interface PropertyDetailsRepository extends JpaRepository<PropertyDetails, Long> {

	PropertyDetails findByGranterApplicationId(Long granterId);
}
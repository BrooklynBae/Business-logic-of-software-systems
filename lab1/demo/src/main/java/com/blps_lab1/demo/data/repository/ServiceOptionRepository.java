package com.blps_lab1.demo.data.repository;

import com.blps_lab1.demo.data.tables.ServiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOptionRepository extends JpaRepository<ServiceOption, Long> {
}

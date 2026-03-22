package com.blps_lab1.demo.data.repository;

import com.blps_lab1.demo.data.tables.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {
}

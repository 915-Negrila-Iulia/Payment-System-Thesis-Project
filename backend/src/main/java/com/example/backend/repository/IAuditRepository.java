package com.example.backend.repository;

import com.example.backend.model.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuditRepository extends JpaRepository<Audit,Long> {
}

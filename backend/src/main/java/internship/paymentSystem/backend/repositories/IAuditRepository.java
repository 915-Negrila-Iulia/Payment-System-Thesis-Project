package com.example.backend.repositories;

import com.example.backend.models.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAuditRepository extends JpaRepository<Audit,Long> {
}

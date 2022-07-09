package com.example.backend.repository;

import com.example.backend.model.PersonHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonHistoryRepository extends JpaRepository<PersonHistory,Long> {
}

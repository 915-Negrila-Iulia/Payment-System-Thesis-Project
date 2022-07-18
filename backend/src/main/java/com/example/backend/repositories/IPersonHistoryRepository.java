package com.example.backend.repositories;

import com.example.backend.models.PersonHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPersonHistoryRepository extends JpaRepository<PersonHistory,Long> {
}

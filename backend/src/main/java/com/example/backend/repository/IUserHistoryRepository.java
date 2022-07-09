package com.example.backend.repository;

import com.example.backend.model.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserHistoryRepository extends JpaRepository<UserHistory,Long> {
}

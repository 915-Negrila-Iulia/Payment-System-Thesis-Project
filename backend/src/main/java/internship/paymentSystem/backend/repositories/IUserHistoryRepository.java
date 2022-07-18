package com.example.backend.repositories;

import com.example.backend.models.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserHistoryRepository extends JpaRepository<UserHistory,Long> {
}

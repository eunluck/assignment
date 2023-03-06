package com.szs.assignment.repository;

import com.szs.assignment.model.refund.ScrapHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapHistoryRepository extends JpaRepository<ScrapHistory,Long>{



}

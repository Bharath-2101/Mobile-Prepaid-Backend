package com.aits.mobileprepaid.Repositories;

import com.aits.mobileprepaid.Entities.RechargeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RechargeHistoryRepository extends JpaRepository<RechargeHistory,Long> {

    List<RechargeHistory> findByUserId(Long id);

    List<RechargeHistory> findByExpirationDateBetween(LocalDateTime start, LocalDateTime end);


}

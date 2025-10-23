package com.aits.mobileprepaid.Services;

import com.aits.mobileprepaid.DTOs.HistoryResponseDTO;
import com.aits.mobileprepaid.Entities.RechargeHistory;
import com.aits.mobileprepaid.Repositories.RechargeHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RechargeHistoryService {

    private final RechargeHistoryRepository rechargeHistoryRepository;

    public List<HistoryResponseDTO> getAllRechargeHistories(){
        return rechargeHistoryRepository.findAll()
                                        .stream()
                                        .map(this::historyToDTO)
                                        .toList();
    }

    public List<HistoryResponseDTO> getAllRechargeHistoriesByUserId(Long id){
        return rechargeHistoryRepository.findByUserId(id)
                                        .stream()
                                        .map(this::historyToDTO)
                                        .toList();
    }

    public List<HistoryResponseDTO> getAllRechargeHistoriesExpiresInThreeDays()
    {
        return rechargeHistoryRepository.findByExpirationDateBetween(LocalDateTime.now(),LocalDateTime.now().plusDays(3))
                                        .stream()
                                        .map(this::historyToDTO)
                                        .toList();
    }

    //Utils
    public HistoryResponseDTO historyToDTO(RechargeHistory history){
        return new HistoryResponseDTO(history.getId(),
                                        history.getRechargeDate(),
                                        history.getExpirationDate(),
                                        history.getPaidAmount(),
                                        history.getPaymentMethod(),
                                        history.getPaymentDetails(),
                                        history.getUser().getId(),
                                        history.getUser().getName(),
                                        history.getRechargePlan().getCategory());
    }
}

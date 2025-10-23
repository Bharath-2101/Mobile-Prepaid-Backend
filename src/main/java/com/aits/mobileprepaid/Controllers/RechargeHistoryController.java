package com.aits.mobileprepaid.Controllers;

import com.aits.mobileprepaid.DTOs.HistoryResponseDTO;
import com.aits.mobileprepaid.Services.RechargeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class RechargeHistoryController {

    private final RechargeHistoryService rechargeHistoryService;

    @GetMapping("/all")
    public ResponseEntity<List<HistoryResponseDTO>> getAllHistories()
    {
        return ResponseEntity.ok(rechargeHistoryService.getAllRechargeHistories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<HistoryResponseDTO>> getAllHistoriesByUserId(@PathVariable Long id)
    {
        return ResponseEntity.ok(rechargeHistoryService.getAllRechargeHistoriesByUserId(id));
    }
}

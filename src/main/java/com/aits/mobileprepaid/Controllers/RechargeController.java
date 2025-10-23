package com.aits.mobileprepaid.Controllers;

import com.aits.mobileprepaid.DTOs.HistoryResponseDTO;
import com.aits.mobileprepaid.DTOs.RechargeRequestDTO;
import com.aits.mobileprepaid.Services.RechargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recharge")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;

    @PostMapping("")
    public ResponseEntity<HistoryResponseDTO> recharge(@Valid @RequestBody RechargeRequestDTO dto) {
        return   ResponseEntity.ok(rechargeService.recharge(dto));
    }
}

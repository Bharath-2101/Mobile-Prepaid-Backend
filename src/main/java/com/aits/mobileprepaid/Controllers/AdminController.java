package com.aits.mobileprepaid.Controllers;

import com.aits.mobileprepaid.DTOs.*;
import com.aits.mobileprepaid.Services.RechargeHistoryService;
import com.aits.mobileprepaid.Services.RechargePlanService;
import com.aits.mobileprepaid.Services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final RechargePlanService rechargePlanService;
    private final RechargeHistoryService rechargeHistoryService;
    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<RegisterResponseDTO> addUserWithAdminRole(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {

        return ResponseEntity.ok(userService.addAdmin(registerRequestDTO));

    }

    @PutMapping("/user/{id}")
    public ResponseEntity<String> updateUserToAdmin(@PathVariable Long id)
    {
        return ResponseEntity.ok(userService.updateUserIntoAdmin(id));
    }


    //Recharge Plan
    @PostMapping("/addPlan")
    public ResponseEntity<PlanResponseDTO> addPlan(@Valid @RequestBody PlanRequestDTO planRequestDTO) {
        return ResponseEntity.ok(rechargePlanService.addPlan(planRequestDTO));
    }

    @PutMapping("/updatePlan/{id}")
    public ResponseEntity<PlanResponseDTO> updatePlanToAdmin(@PathVariable Long id, @Valid @RequestBody PlanRequestDTO planRequestDTO)
    {
        return  ResponseEntity.ok(rechargePlanService.updatePlan(id, planRequestDTO));
    }

    @DeleteMapping("/deletePlan/{id}")
    public ResponseEntity<String> deletePlanToAdmin(@PathVariable Long id)
    {
        return ResponseEntity.ok(rechargePlanService.deletePlan(id));
    }

    //Recharge History
    @GetMapping("/expiringPlans")
    public ResponseEntity<List<HistoryResponseDTO>> getExpiringPlansInThreeDays()
    {
        return ResponseEntity.ok(rechargeHistoryService.getAllRechargeHistoriesExpiresInThreeDays());
    }


}

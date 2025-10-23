package com.aits.mobileprepaid.Controllers;

import com.aits.mobileprepaid.DTOs.PlanResponseDTO;
import com.aits.mobileprepaid.Entities.RechargePlan.*;
import com.aits.mobileprepaid.Services.RechargePlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class RechargePlanController {

    private final RechargePlanService rechargePlanService;

    @GetMapping("/all")
    public ResponseEntity<List<PlanResponseDTO>> getAllRechargePlan(){
        return ResponseEntity.ok(rechargePlanService.getAllPlans());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<PlanResponseDTO>> getRechargePlanByCategory(@PathVariable Category category){
            return ResponseEntity.ok(rechargePlanService.getPlansByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponseDTO> getRechargePlanById(@PathVariable Long id){
        return ResponseEntity.ok(rechargePlanService.getPlanById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<PlanResponseDTO>> getRechargePlanByName(@PathVariable String name){
        return ResponseEntity.ok(rechargePlanService.getPlanByName(name));
    }

}

package com.aits.mobileprepaid.DTOs;

import com.aits.mobileprepaid.Entities.RechargePlan.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private Long validity;
    private Category category;

}

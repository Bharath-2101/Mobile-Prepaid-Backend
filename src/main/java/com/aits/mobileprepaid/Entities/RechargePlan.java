package com.aits.mobileprepaid.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RechargePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Plan name is required.")
    @Size(max = 50, message = "Plan name must not exceed 50 characters.")
    private String name;

    @NotNull(message = "Price is required.")
    @Min(value = 1, message = "Price must be greater than 0.")
    private Long price;

    @NotNull(message = "Validity is required.")
    @Min(value = 1, message = "Validity must be at least 1 day.")
    private Long validity;

    @Size(max = 255, message = "Description must not exceed 255 characters.")
    private String description;

    @NotNull(message = "Category is required.")
    @Enumerated(EnumType.STRING)
    private Category category;

    public enum Category {
        DATA,
        POPULAR,
        UNLIMITED
    }
}

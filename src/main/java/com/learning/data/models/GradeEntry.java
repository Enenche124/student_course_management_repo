package com.learning.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeEntry {
    @NotBlank
    private String email;

    @NotBlank
    private String grade;
}
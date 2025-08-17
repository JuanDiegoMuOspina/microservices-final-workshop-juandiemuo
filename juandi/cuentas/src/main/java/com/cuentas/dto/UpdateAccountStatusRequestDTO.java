package com.cuentas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAccountStatusRequestDTO {

    @NotBlank(message = "El estado no puede estar vacío")
    private String status;
}

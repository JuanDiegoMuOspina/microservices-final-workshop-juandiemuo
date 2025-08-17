package com.cuentas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAccountRequestDTO {

    @NotBlank
    @Size(min = 5, max = 20)
    @Pattern(regexp = "^[0-9]+$", message = "El número de cuenta solo debe contener dígitos")
    private String accountNumber;

    @NotBlank(message = "El tipo de cuenta no puede estar vacío")
    private String accountType;

    @NotNull(message = "El ID del banco es obligatorio")
    @Positive(message = "El ID del banco debe ser un número positivo")
    private Long bankId;
}

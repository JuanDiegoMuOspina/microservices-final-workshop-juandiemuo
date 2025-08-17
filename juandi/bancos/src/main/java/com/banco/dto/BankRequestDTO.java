package com.banco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class BankRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String name;

    @NotBlank(message = "El código no puede estar vacío")
    @Size(max = 50, message = "El código no puede tener más de 50 caracteres")
    private String code;
}

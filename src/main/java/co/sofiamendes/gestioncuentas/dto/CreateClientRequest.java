package co.sofiamendes.gestioncuentas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateClientRequest(
        @NotBlank String identificationType,
        @NotBlank String identificationNumber,
        @NotBlank @Size(min = 2) String firstName,
        @NotBlank @Size(min = 2) String lastName,
        @NotBlank @Email String email,
        @NotNull @Past LocalDate birthDate
) {
}
package co.sofiamendes.gestioncuentas.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UpdateClientRequest(
        @NotBlank @Size(min = 2) String firstName,
        @NotBlank @Size(min = 2) String lastName,
        @NotBlank @Email String email,
        @NotNull @Past LocalDate birthDate
) {
}
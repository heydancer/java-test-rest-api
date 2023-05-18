package com.heydancer.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortUserDTO {
    @NotNull(message = "Firstname cannot be null")
    @NotBlank(message = "Firstname cannot be empty")
    private String firstname;

    @NotNull(message = "Lastname cannot be null")
    @NotBlank(message = "Lastname cannot be empty")
    private String lastname;

    @NotNull(message = "Surname cannot be null")
    @NotBlank(message = "Surname cannot be empty")
    private String surname;

    @NotNull(message = "Birthday cannot be null")
    @PastOrPresent(message = "Birthday cannot be in future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}

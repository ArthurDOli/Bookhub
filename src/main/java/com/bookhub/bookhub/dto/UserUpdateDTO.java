package com.bookhub.bookhub.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @Size(min = 2, max = 100)
    private String name;

    @Size(min = 6)
    private String password;
}

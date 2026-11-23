
package com.supporthub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateTicketStatusRequest(

    @NotBlank(message = "Status is required")
    @Pattern(
        regexp = "OPEN|IN_PROGRESS|RESOLVED|CLOSED",
        message = "Invalid ticket status"
    )
    String status

) {}

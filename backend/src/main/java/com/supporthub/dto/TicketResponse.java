package com.supporthub.dto;

import java.time.Instant;
import java.util.UUID;

public record TicketResponse(
    UUID id,
    Long ticketNumber,
    String title,
    String description,
    String status,
    String priority,
    String category,
    UUID createdBy,
    String createdByName,
    UUID assignedTo,
    Instant createdAt,
    Instant updatedAt
) {}

package com.supporthub.repositories;

import com.supporthub.entities.Ticket;
import com.supporthub.enums.TicketStatus;
import com.supporthub.enums.TicketPriority;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByPriority(TicketPriority priority);

    List<Ticket> findByCreatedById(UUID userId);

    List<Ticket> findByAssignedToId(UUID technicianId);

    Optional<Ticket> findByIdAndCreatedById(UUID ticketId, UUID userId);

}

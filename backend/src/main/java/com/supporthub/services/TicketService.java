
package com.supporthub.services;

import com.supporthub.dto.CreateTicketRequest;
import com.supporthub.dto.TicketResponse;
import com.supporthub.entities.AppUser;
import com.supporthub.entities.Ticket;
import com.supporthub.enums.TicketPriority;
import com.supporthub.enums.TicketStatus;
import com.supporthub.repositories.AppUserRepository;
import com.supporthub.repositories.TicketRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final AppUserRepository appUserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public TicketService(
            TicketRepository ticketRepository,
            AppUserRepository appUserRepository) {
        this.ticketRepository = ticketRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        AppUser user = appUserRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.title().trim());
        ticket.setDescription(request.description().trim());
        ticket.setCategory(request.category().trim());
        ticket.setCreatedBy(user);

        if (request.priority() != null && !request.priority().isBlank()) {
            try {
                ticket.setPriority(TicketPriority.valueOf(
                    request.priority().trim().toUpperCase(Locale.ROOT)
                ));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid ticket priority");
            }
        } else {
            ticket.setPriority(TicketPriority.MEDIUM);
        }

        Ticket saved = ticketRepository.saveAndFlush(ticket);
        entityManager.refresh(saved);

        return toResponse(saved, user);
    }

    @Transactional
    public TicketResponse updateTicketStatus(
            UUID ticketId, String status) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

        Ticket ticket = ticketRepository
                .findByIdAndCreatedById(ticketId, user.getId())
                .orElseThrow(() ->
                    new IllegalArgumentException("Ticket not found"));

        TicketStatus newStatus;

        try {
            newStatus = TicketStatus.valueOf(
                status.trim().toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid ticket status");
        }

        ticket.setStatus(newStatus);

        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(java.time.Instant.now());
        } else {
            ticket.setResolvedAt(null);
        }

        Ticket updated = ticketRepository.save(ticket);

        return toResponse(updated, user);
    }

    @Transactional
    public void deleteTicket(UUID ticketId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

        Ticket ticket = ticketRepository
                .findByIdAndCreatedById(ticketId, user.getId())
                .orElseThrow(() ->
                    new IllegalArgumentException("Ticket not found"));

        ticketRepository.delete(ticket);
    }

    private TicketResponse toResponse(Ticket ticket, AppUser user) {
        return new TicketResponse(
            ticket.getId(),
            ticket.getTicketNumber(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getStatus().name(),
            ticket.getPriority().name(),
            ticket.getCategory(),
            user.getId(),
            user.getFullName(),
            ticket.getAssignedTo() != null
                ? ticket.getAssignedTo().getId() : null,
            ticket.getCreatedAt(),
            ticket.getUpdatedAt()
        );
    }
}


package com.supporthub.controllers;

import com.supporthub.dto.CreateTicketRequest;
import com.supporthub.dto.TicketResponse;
import com.supporthub.dto.UpdateTicketStatusRequest;
import com.supporthub.entities.AppUser;
import com.supporthub.entities.Ticket;
import com.supporthub.repositories.AppUserRepository;
import com.supporthub.repositories.TicketRepository;
import com.supporthub.services.TicketService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketRepository ticketRepository;
    private final AppUserRepository appUserRepository;

    public TicketController(
            TicketService ticketService,
            TicketRepository ticketRepository,
            AppUserRepository appUserRepository) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.appUserRepository = appUserRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createTicket(
            @Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @GetMapping
    public List<TicketResponse> getMyTickets() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

        List<Ticket> tickets =
                ticketRepository.findByCreatedById(user.getId());

        return tickets.stream()
                .map(ticket -> new TicketResponse(
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
                ))
                .toList();
    }

    @GetMapping("/{ticketId}")
    public TicketResponse getTicketById(
            @PathVariable UUID ticketId) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        AppUser user = appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

        Ticket ticket = ticketRepository
                .findByIdAndCreatedById(ticketId, user.getId())
                .orElseThrow(() ->
                    new IllegalArgumentException("Ticket not found"));

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

    @PatchMapping("/{ticketId}/status")
    public TicketResponse updateTicketStatus(
            @PathVariable UUID ticketId,
            @Valid @RequestBody UpdateTicketStatusRequest request) {

        return ticketService.updateTicketStatus(
            ticketId,
            request.status()
        );
    }

    @DeleteMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(@PathVariable UUID ticketId) {
        ticketService.deleteTicket(ticketId);
    }
}

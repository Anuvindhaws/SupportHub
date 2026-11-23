
package com.supporthub;

import com.supporthub.dto.CreateTicketRequest;
import com.supporthub.dto.TicketResponse;
import com.supporthub.entities.AppUser;
import com.supporthub.entities.Ticket;
import com.supporthub.enums.TicketStatus;
import com.supporthub.repositories.AppUserRepository;
import com.supporthub.repositories.TicketRepository;
import com.supporthub.services.TicketService;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TicketService ticketService;

    private AppUser testUser;

    @BeforeEach
    void setUp() {

        // Inject EntityManager because TicketService uses @PersistenceContext.
        ReflectionTestUtils.setField(
            ticketService, "entityManager", entityManager
        );

        testUser = new AppUser();

        ReflectionTestUtils.setField(
            testUser, "id", UUID.randomUUID()
        );

        testUser.setEmail("employee1@supporthub.com");
        testUser.setFullName("Test Employee");

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "employee1@supporthub.com",
                null
            )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTicketShouldSaveTicket() {

        when(appUserRepository.findByEmail("employee1@supporthub.com"))
            .thenReturn(Optional.of(testUser));

        when(ticketRepository.saveAndFlush(any(Ticket.class)))
            .thenAnswer(invocation -> {
                Ticket ticket = invocation.getArgument(0);

                ReflectionTestUtils.setField(
                    ticket, "id", UUID.randomUUID()
                );

                ReflectionTestUtils.setField(
                    ticket, "ticketNumber", 100L
                );

                ticket.setStatus(TicketStatus.OPEN);

                return ticket;
            });

        CreateTicketRequest request = new CreateTicketRequest(
            "Laptop issue",
            "Laptop cannot connect to WiFi",
            "Network",
            "HIGH"
        );

        TicketResponse response = ticketService.createTicket(request);

        assertNotNull(response);
        assertEquals(100L, response.ticketNumber());
        assertEquals("Laptop issue", response.title());
        assertEquals("OPEN", response.status());
        assertEquals("HIGH", response.priority());
        assertEquals("Network", response.category());

        verify(ticketRepository).saveAndFlush(any(Ticket.class));
        verify(entityManager).refresh(any(Ticket.class));
    }
}

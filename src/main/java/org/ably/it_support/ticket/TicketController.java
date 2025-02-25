package org.ably.it_support.ticket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ably.it_support.user.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket Controller", description = "Ticket Management APIs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Create a new ticket")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketRequest ticketRequest) {
        TicketResponse response = ticketService.create(ticketRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a ticket by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        TicketResponse response = ticketService.findById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(summary = "Update the status of a ticket")
     @PreAuthorize("hasRole('IT_SUPPORT')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Boolean> updateStatus(@PathVariable Long id, @RequestParam TicketStatus status) {
        boolean updated = ticketService.updateStatus(id, status);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }


    @Operation(summary = "Recherche")
    @GetMapping("/search")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('IT_SUPPORT')")
    public ResponseEntity<Page<TicketResponse>> searchTickets(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String ticketPriority,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "category") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDirection,
        @AuthenticationPrincipal AppUser user) {

    TicketSearchCriteria criteria = TicketSearchCriteria.builder()
            .category(category)
            .ticketPriority(ticketPriority)
            .status(status)
            .build();

    Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
    Pageable pageable = PageRequest.of(page, size, sort);


    Page<TicketResponse> tickets = ticketService.findAll(criteria, pageable, user);
    return ResponseEntity.ok(tickets);
    }


}
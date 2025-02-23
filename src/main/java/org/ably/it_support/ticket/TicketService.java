package org.ably.it_support.ticket;


import org.ably.it_support.user.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface   TicketService {
    TicketResponse save(Ticket ticket);
    TicketResponse create(TicketRequest request);
    TicketResponse findById(Long id);
    Page<TicketResponse> findAll(TicketSearchCriteria criteria, Pageable pageable, AppUser user);
    boolean existsById(Long id);
    boolean updateStatus(Long id, TicketStatus status);

}

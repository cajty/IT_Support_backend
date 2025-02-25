package org.ably.it_support.ticket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ably.it_support.core.exception.BusinessException;
import org.ably.it_support.core.exception.NotFoundException;

import org.ably.it_support.core.security.SecurityUtil;
import org.ably.it_support.user.AppUser;
import org.ably.it_support.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final SecurityUtil securityUtil;
    private final TicketSpecificationBuilder ticketSpecificationBuilder;


    private Map<String, String> validateError(TicketRequest request) {
        Map<String, String> errors = new HashMap<>();

        return errors;
    }

    private void validateTicketRequest(TicketRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Ticket request cannot be null");
        }
        Map<String, String> errors = validateError(request);
        if (!errors.isEmpty()) {
            throw new BusinessException(errors.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public TicketResponse save(Ticket ticket) {
        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("New ticket created: {}", savedTicket.getId());
        return ticketMapper.toResponse(savedTicket);
    }

    @Override
    @Transactional
    public TicketResponse create(TicketRequest request) {

        Ticket ticket = ticketMapper.toEntity(request);
        AppUser loggedInUser = securityUtil.getLoggedInUser();
        ticket.setCreatedBy(loggedInUser);
        ticket.setStatus(TicketStatus.NEW);


        return save(ticket);
    }





     private Ticket findEntityById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ticket", id));
    }

    @Override
    @Transactional(readOnly = true)
    public TicketResponse findById(Long id) {
        Ticket ticket = findEntityById(id);
        return ticketMapper.toResponse(ticket);
    }
    @Override
   public Page<TicketResponse> findAll(TicketSearchCriteria criteria, Pageable pageable, AppUser user) {
    boolean isEmployee = user.getRole() == Role.EMPLOYEE;
    Specification<Ticket> spec = ticketSpecificationBuilder.build(criteria, user.getId(), isEmployee);
    return ticketRepository.findAll(spec, pageable).map(ticketMapper::toResponse);
}




    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return ticketRepository.existsById(id);
    }

    @Override
    public boolean updateStatus(Long id, TicketStatus status) {
        Ticket ticket = findEntityById(id);
        TicketStatus oldStatus = ticket.getStatus();

        int updatedRows = ticketRepository.updateTicketStatus(id, status);
        if (updatedRows > 0) {

            log.info("Ticket {} status changed from {} to {}", id, oldStatus, status);
            return true;
        }
        return false;
    }


}

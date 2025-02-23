package org.ably.it_support.ticket;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class TicketSpecificationBuilder {

    public Specification<Ticket> build(TicketSearchCriteria criteria, UUID userId, boolean isEmployee) {
        Specification<Ticket> spec = Specification.where(hasCategory(criteria.getCategory()))
                .and(hasTicketPriority(criteria.getTicketPriority()))
                .and(hasStatus(criteria.getStatus()));


        if (isEmployee) {
            spec = spec.and(createdBy(userId));
        }

        return spec;
    }

    private Specification<Ticket> hasCategory(String category) {
        return (root, query, cb) -> {
            if (category == null) return null;
            return cb.equal(root.get("category").get("name"), category);
        };
    }

    private Specification<Ticket> hasTicketPriority(String ticketPriority) {
        return (root, query, cb) -> {
            if (ticketPriority == null) return null;
            return cb.equal(root.get("ticketPriority"), TicketPriority.valueOf(ticketPriority.toUpperCase()));
        };
    }

    private Specification<Ticket> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), TicketStatus.valueOf(status.toUpperCase()));
        };
    }

    private Specification<Ticket> createdBy(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("createdBy").get("id"), userId);
    }
}

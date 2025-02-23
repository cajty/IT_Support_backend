package org.ably.it_support.ticket;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TicketSearchCriteria {
    private String category;
    private String ticketPriority;
    private String status;
}

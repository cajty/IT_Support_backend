package org.ably.it_support.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@Builder
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private TicketPriority ticketPriority;
    private LocalDateTime createdAt;
    private TicketStatus status;
    private String  categoryName;
   private String creatorName;
}

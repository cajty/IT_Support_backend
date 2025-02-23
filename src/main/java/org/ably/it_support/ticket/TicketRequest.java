package org.ably.it_support.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.ably.it_support.common.validation.EnumValue;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

  @NotNull(message = "Ticket priority is required")
private TicketPriority ticketPriority;



    @NotNull(message = "Category ID is required")
    private Long categoryId;


}
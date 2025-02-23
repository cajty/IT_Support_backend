package org.ably.it_support.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket,Long>, JpaSpecificationExecutor<Ticket> {

    @Modifying
    @Query("UPDATE Ticket t SET t.status = :status WHERE t.id = :id")
    int updateTicketStatus(@Param("id") Long id, @Param("status") TicketStatus status);
}

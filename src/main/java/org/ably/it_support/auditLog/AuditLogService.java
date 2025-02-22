package org.ably.it_support.auditLog;

import lombok.RequiredArgsConstructor;
import org.ably.it_support.ticket.Ticket;
import org.ably.it_support.ticket.TicketStatus;
import org.ably.it_support.user.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {


        public static AuditLog createLog(Ticket ticket, AppUser appUser, AuditAction action, String details) {
        return AuditLog.builder()
                .ticket(ticket)
                .changedBy(appUser)
                .action(action)
                .details(details)
                .build();
    }


    public static AuditLog statusChange(Ticket ticket, AppUser appUser, TicketStatus oldStatus, TicketStatus newStatus) {
        return createLog(ticket, appUser, AuditAction.STATUS_CHANGE, "Status changed: " + oldStatus + " → " + newStatus);
    }

    public static AuditLog commentLog(Ticket ticket, AppUser appUser, String comment) {
        return createLog(ticket, appUser, AuditAction.COMMENT, "Comment: " + comment);
    }
}

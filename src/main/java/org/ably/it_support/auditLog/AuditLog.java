package org.ably.it_support.auditLog;

import jakarta.persistence.*;
import lombok.*;
import org.ably.it_support.ticket.Ticket;
import org.ably.it_support.user.AppUser;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_log", indexes = {
    @Index(name = "idx_audit_ticket", columnList = "ticket_id"),
    @Index(name = "idx_audit_user", columnList = "changed_by"),
    @Index(name = "idx_audit_action", columnList = "action")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private AppUser changedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Column(nullable = false)
    private String details;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


}

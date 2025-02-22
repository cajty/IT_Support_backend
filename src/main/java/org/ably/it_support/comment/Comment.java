package org.ably.it_support.comment;

import jakarta.persistence.*;
import lombok.*;
import org.ably.it_support.ticket.Ticket;
import org.ably.it_support.user.AppUser;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "commented_by", nullable = false)
    private AppUser commentedBy;

    @Column(nullable = false)
    private String commentText;

}
package com.telemed.domain.agent;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Mensaje individual dentro de una conversación. */
@Entity
@Table(name="messages")
@Getter @Setter @NoArgsConstructor
public class Message {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="conversation_id", nullable=false) private Conversation conversation;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private Sender sender;
    @Column(nullable=false, columnDefinition="TEXT") private String content;
    @Column(name="sent_at", nullable=false) private OffsetDateTime sentAt;

    public Message(Conversation conversation, Sender sender, String content) {
        this.conversation = conversation;
        this.sender = sender;
        this.content = content;
        this.sentAt = OffsetDateTime.now();
    }
}

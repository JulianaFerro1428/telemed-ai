package com.telemed.domain.agent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telemed.domain.patient.Patient;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/** Agregado de una preconsulta conversacional. */
@Entity
@Table(name="conversations")
@Getter @Setter @NoArgsConstructor
public class Conversation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id", nullable=false) private Patient patient;
    @JsonIgnore
    @Column(name="start_date", nullable=false) private OffsetDateTime startDate;
    @Column(name="end_date") private OffsetDateTime endDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20) private ConversationStatus status;

    @OneToMany(mappedBy="conversation", cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    public Conversation(Patient patient) {
        this.patient = patient;
        this.startDate = OffsetDateTime.now();
        this.status = ConversationStatus.ACTIVA;
    }

    /** Agrega un mensaje al agregado. */
    public void addMessage(Message message) { messages.add(message); }

    /** Finaliza la conversación. */
    public void finish() { status = ConversationStatus.FINALIZADA; endDate = OffsetDateTime.now(); }
}

package com.telemed.interfaces.agent;

import com.telemed.application.agent.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** API del agente de preconsulta. */
@RestController
@RequestMapping("/api/agent")
@PreAuthorize("hasAnyRole('PACIENTE','ADMIN')")
public class AgentController {
    private final AgentService service;
    public AgentController(AgentService service) { this.service=service; }

    public record StartRequest(@NotNull Long patientId) {}
    public record MessageRequest(@NotBlank String content) {}

    @PostMapping("/conversations")
    @Operation(summary="Iniciar preconsulta")
    public Object start(@Valid @RequestBody StartRequest r) { return service.start(r.patientId()); }

    @PostMapping("/conversations/{id}/messages")
    public Object message(@PathVariable Long id,@Valid @RequestBody MessageRequest r) { return service.respond(id,r.content()); }

    @PostMapping("/conversations/{id}/finish")
    public Object finish(@PathVariable Long id) { return service.finish(id); }

    @GetMapping("/conversations/{id}")
    public Object get(@PathVariable Long id) { return service.get(id); }

    @GetMapping("/patients/{id}/conversations")
    public Object list(@PathVariable Long id) { return service.byPatient(id); }
}

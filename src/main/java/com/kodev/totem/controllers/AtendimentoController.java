package com.kodev.totem.controllers;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Medico;
import com.kodev.totem.models.Paciente;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.repositories.MedicoRepository;
import com.kodev.totem.repositories.PacienteRepository;
import com.kodev.totem.repositories.UsuarioRepository;
import com.kodev.totem.services.AtendimentoService;
import jakarta.persistence.EntityNotFoundException;
import org.antlr.v4.runtime.atn.ATN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/atendimento")
public class AtendimentoController {

    private final AtendimentoService atendimentoService;
    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    @Autowired
    public AtendimentoController(AtendimentoService atendimentoService,
                                 UsuarioRepository usuarioRepository,
                                 PacienteRepository pacienteRepository,
                                 MedicoRepository medicoRepository) {
        this.atendimentoService = atendimentoService;
        this.usuarioRepository = usuarioRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @PutMapping("/chegou/{id}")
    public ResponseEntity<String> markPatientHasArrived(@PathVariable Long id, @RequestParam MultipartFile fotoPaciente) {
        return ResponseEntity.ok(atendimentoService.markPatientArrived(id, fotoPaciente));
    }

    @GetMapping("/paciente/{letra}")
    public ResponseEntity<List<Atendimento>> getPacientesWithLetterToday(@PathVariable String letra) {
        return ResponseEntity.ok(atendimentoService.getAllAtendimentosPacienteStartsWith(letra));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Atendimento>> getAtendimentosForMedico(@PathVariable Long userId) {
        Usuario usuario = usuarioRepository.findUsuarioByMedico_MedicoId(userId);

        List<Atendimento> atendimentos = atendimentoService.getAtendimentosByMedicoIdToday(usuario.getMedico().getMedicoId());

        List<Atendimento> sortedAtendimentos = atendimentos.stream()
                .sorted(Comparator.comparing(Atendimento::getDataAtendimento))
                .toList();

        return ResponseEntity.ok(sortedAtendimentos);
    }

    public LocalDateTime formataHora(String dataHora) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime timestamp = null;

        return LocalDateTime.parse(dataHora, dateFormat);
    }

    @PostMapping("/app/form")
    public ResponseEntity<Atendimento> criaAtendimento(@RequestParam Long userId, @RequestParam String nomePaciente, @RequestParam String dataHora) {
        Optional<Usuario> userOp = usuarioRepository.findById(userId);
        Optional<Paciente> paciente = pacienteRepository.findPacienteByNomeCompletoIgnoreCase(nomePaciente);

        if(userOp.isEmpty()) return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Médico não encontrado")).build();
        if(paciente.isEmpty()) return ResponseEntity.of(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Paciente não encontrado")).build();

        Atendimento atendimento = new Atendimento();
        atendimento.setMedico(userOp.get().getMedico());
        atendimento.setPaciente(paciente.get());
        atendimento.setDataAtendimento(formataHora(dataHora));
        atendimento.setChegou(false);

        atendimentoService.criaAtendimento(atendimento);

        return ResponseEntity.ok(atendimento);
    }

    @PostMapping
    public ResponseEntity<Atendimento> criaAtendimento(@RequestParam String nomeMedico, @RequestParam String nomePaciente, @RequestParam String dataHora){
        Optional<Paciente> paciente = pacienteRepository.findPacienteByNomeCompletoIgnoreCase(nomePaciente);
        Optional<Medico> medico = medicoRepository.findMedicoByNomeCompletoIgnoreCase(nomeMedico);

        LocalDateTime timestamp = formataHora(dataHora);

        if(paciente.isPresent() && medico.isPresent()) {
            Atendimento atendimento = new Atendimento();
            atendimento.setMedico(medico.get());
            atendimento.setPaciente(paciente.get());
            atendimento.setDataAtendimento(timestamp);
            atendimento.setChegou(false);


            Atendimento atendimento1 = atendimentoService.criaAtendimento(atendimento);

            Usuario user = usuarioRepository.findUsuarioByMedico_MedicoId(medico.get().getMedicoId());

            atendimentoService.sendMessage(user);

            return ResponseEntity.ok(atendimento1);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<Atendimento>> getAtendimentos() {
        return ResponseEntity.ok(atendimentoService.getAtendimentos());
    }

    @GetMapping("/today")
    public ResponseEntity<List<Atendimento>> getAtendimentosToday() {

        List<Atendimento> todayAtendimentos = atendimentoService.getTodayAtendimentos();

        return ResponseEntity.ok(todayAtendimentos);
    }

    @PutMapping("/desmarcar")
    public ResponseEntity<Object> desmarcaAtendimento(@RequestParam Long idAtendimento) {
        atendimentoService.desmarcaAtendimento(idAtendimento);
        return ResponseEntity.ok().build();
    }
}

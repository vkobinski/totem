package com.kodev.totem.controllers;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Medico;
import com.kodev.totem.models.Paciente;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.push.ExpoPushNotification;
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

import javax.print.attribute.standard.JobKOctets;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public ResponseEntity<String> markPatientHasArrived(@PathVariable Long id, @RequestParam MultipartFile fotoPaciente) throws IOException {
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

    @PostMapping("/medico/today-and-forth")
    public ResponseEntity<List<Atendimento>> getAtendimentosForMedicoTodayAndForth(@RequestParam String nomeMedico) {
        List<Atendimento> atendimentos = atendimentoService.getAtendimentosByMedicoIdToday(nomeMedico);

        List<Atendimento> sortedAtendimentos = atendimentos.stream()
                .sorted(Comparator.comparing(Atendimento::getDataAtendimento))
                .toList();

        return ResponseEntity.ok(sortedAtendimentos);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Atendimento>> getAtendimentosForPacienteAndMedico(@RequestParam String nome) {
        return ResponseEntity.ok(atendimentoService.searchByNomeCompleto(nome));
    }

    public LocalDateTime formataHora(String dataHora) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
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
    public ResponseEntity<Atendimento> criaAtendimento(@RequestParam String nomeMedico, @RequestParam String nomePaciente,@RequestParam String dataNascimento, @RequestParam String dataHora) throws ParseException {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date parse = dateTimeFormatter.parse(dataNascimento);
        java.sql.Date dataSql = new Date(parse.getTime());

        Optional<Paciente> paciente = pacienteRepository.findPacienteByNomeCompletoIgnoreCaseAndDataNascimento(nomePaciente, dataSql);
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

            return ResponseEntity.ok(atendimento1);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<Atendimento>> getAtendimentos() {
        return ResponseEntity.ok(atendimentoService.getAtendimentos());
    }


    @PostMapping("/search-by-day")
    public ResponseEntity<List<Atendimento>> getAtendimentosForMedicoByDay(@RequestParam Long userId, @RequestParam String day) throws ParseException {

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date parse = dateTimeFormatter.parse(day + "/2023");
        java.sql.Date dataSql = new Date(parse.getTime());
        LocalDateTime localDateTime = dataSql.toLocalDate().atStartOfDay();
        Usuario usuario = usuarioRepository.findUsuarioByMedico_MedicoId(userId);

        List<Atendimento> atendimentos = atendimentoService.getAtendimentosByMedicoIdByDay(usuario.getMedico().getMedicoId(), localDateTime);

        atendimentos.forEach((atendimento) -> {
            atendimento.getMedico().setFoto(null);
        });

        if(atendimentos.size() > 2) {
            if(atendimentos.get(0).getFotoPaciente() == atendimentos.get(1).getFotoPaciente()) {
                System.out.println("1");
            }
        }


        List<Atendimento> sortedAtendimentos = atendimentos.stream()
                .sorted(Comparator.comparing(Atendimento::getDataAtendimento))
                .toList();

        return ResponseEntity.ok(sortedAtendimentos);
    }

    @PostMapping("/not-notified")
    public ResponseEntity<String> checkIfNewNotified(@RequestParam Long userId) {

        Usuario usuario = usuarioRepository.findUsuarioByMedico_MedicoId(userId);
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        List<Atendimento> atendimentos = atendimentoService.getAtendimentosByMedicoIdByDay(usuario.getMedico().getMedicoId(), localDateTime);

        boolean notify = false;

        for(Atendimento atendimento: atendimentos) {
           if(!atendimento.isNotified() && atendimento.isChegou()) {
               notify = true;
               atendimento.setNotified(true);
               atendimentoService.criaAtendimento(atendimento);
           }
        }

        if(notify) return ResponseEntity.ok("S");

        return ResponseEntity.ok("N");
    }


    @GetMapping("/today-and-forth")
    public ResponseEntity<List<Atendimento>> getAtendimentosTodayAndForth() {
        return ResponseEntity.ok(atendimentoService.getAtendimentosAndForth());
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

    @PutMapping("/desmarcar-buscando")
    public ResponseEntity<Object> desmarcaAtendimento(@RequestParam String nomePaciente, @RequestParam String dataNascimento, @RequestParam String dataAtendimento) throws ParseException {
        atendimentoService.desmarcaAtendimentoBuscando(nomePaciente, dataNascimento, dataAtendimento);
        return ResponseEntity.ok().build();
    }
}

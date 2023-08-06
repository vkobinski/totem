package com.kodev.totem.services;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Medico;
import com.kodev.totem.models.Paciente;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.repositories.AtendimentoRepository;
import com.kodev.totem.repositories.MedicoRepository;
import com.kodev.totem.repositories.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Transient;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    private final UsuarioService usuarioService;


    @Autowired
    public AtendimentoService(AtendimentoRepository atendimentoRepository, PacienteRepository pacienteRepository, MedicoRepository medicoRepository, UsuarioService usuarioService) {
        this.atendimentoRepository = atendimentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.usuarioService = usuarioService;
    }

    public List<Atendimento> searchByNomeCompleto(String nomeCompleto){
        List<Atendimento> result = new ArrayList<>();

        result.addAll(atendimentoRepository.findByPacienteNomeCompletoIgnoreCase(nomeCompleto));
        result.addAll(atendimentoRepository.findByMedicoNomeCompletoIgnoreCase(nomeCompleto));

        return result;
    }
    public Atendimento criaAtendimento(Atendimento atendimento) {
        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> getAtendimentosByMedicoId(Long id) {
        return atendimentoRepository.getAtendimentosByMedico_MedicoId(id);
    }

    public List<Atendimento> getAtendimentosByMedicoIdToday(Long id) {
        return atendimentoRepository.getAtendimentosByMedico_MedicoId_Today(id);
    }

    public List<Atendimento> getAtendimentosByMedicoIdByDay(Long id, LocalDateTime date)  {
       return  atendimentoRepository.getAtendimentosByMedico_MedicoId_OnDate(id, date);
    }

    public List<Atendimento> getAtendimentosByMedicoIdToday(String nomeMedico) {
        Optional<Medico> medico = medicoRepository.findMedicoByNomeCompletoIgnoreCase(nomeMedico);
        if(medico.isPresent()) {
            return atendimentoRepository.getAtendimentosByMedico_MedicoId(medico.get().getMedicoId());
        }
        throw new EntityNotFoundException();
    }


    public void desmarcaAtendimento(Long idAtendimento) {
        atendimentoRepository.deleteById(idAtendimento);
    }

    public void desmarcaAtendimentoBuscando(String nomePaciente, String dataNascimento, String dataAtendimento) throws ParseException {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd/MM/yyyy");
        java.sql.Date parse = new java.sql.Date(dateTimeFormatter.parse(dataNascimento).getTime());

        Paciente paciente = pacienteRepository.getPacienteByNomeCompletoContainingIgnoreCaseAndDataNascimento(nomePaciente, parse);

        parse = new java.sql.Date(dateTimeFormatter.parse(dataAtendimento).getTime());
        Atendimento atendimento = atendimentoRepository.findByPacienteAndDataAtendimento(paciente, parse);
        atendimentoRepository.deleteById(atendimento.getAtendimentoId());
    }

    @Transient
    public String markPatientArrived(Long idPaciente, MultipartFile fotoPaciente) {

        Optional<Paciente> byId = pacienteRepository.findById(idPaciente);

        if(byId.isEmpty()) throw new EntityNotFoundException();

        sendMessage(byId.get(), fotoPaciente);

        return "Appointment marked as patient arrived.";
    }

    public void sendMessage(Usuario usuario) {
        WebSocketSession socket = usuarioService.getSocketForUser(usuario);

        try{
            socket.sendMessage(new TextMessage("S"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessage(Paciente paciente, MultipartFile fotoPaciente) {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay= today.atStartOfDay();
        LocalDateTime endOfDay= today.atTime(LocalTime.MAX);

        List<Atendimento> atendimentos = atendimentoRepository.findByPacienteAndDataAtendimentoBetween(paciente, startOfDay, endOfDay);

        for (Atendimento aT : atendimentos) {
            aT.setChegou(true);

            try {
                aT.setFotoPaciente(fotoPaciente.getBytes());
            } catch (Exception e ) {
                System.out.println(e.getMessage());
            }

            atendimentoRepository.save(aT);

            Usuario userMedico = usuarioService.getUsuarioByMedicoId(aT.getMedico().getMedicoId());
            try {
                WebSocketSession socketForUser = usuarioService.getSocketForUser(userMedico);
                socketForUser.sendMessage(new TextMessage("S"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Atendimento> getAtendimentosByDay(java.sql.Date date) {
        return atendimentoRepository.findByDataAtendimento(date);
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentoRepository.findAll();
    }

    public List<Atendimento> getTodayAtendimentos() {
        return atendimentoRepository.findAllByDataAtendimentoToday();
    }

    public List<Atendimento> getAllAtendimentosPacienteStartsWith(String letter) {
        return atendimentoRepository.findAllByPaciente_NomeCompletoStartingWithAndDataAtendimentoToday(letter);
    }

    public List<Atendimento> getAtendimentosAndForth() {
        return atendimentoRepository.findAllAtendimentosFromTodayAndOnwards();
    }
}

package com.kodev.totem.services;

import com.kodev.totem.models.Atendimento;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public Atendimento criaAtendimento(Atendimento atendimento) {
        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> getAtendimentosByMedicoId(Long id) {
        return atendimentoRepository.getAtendimentosByMedico_MedicoId(id);
    }

    public List<Atendimento> getAtendimentosByMedicoIdToday(Long id) {
        return atendimentoRepository.getAtendimentosByMedico_MedicoId_Today(id);
    }

    public void desmarcaAtendimento(Long idAtendimento) {
        atendimentoRepository.deleteById(idAtendimento);
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
            WebSocketSession socketForUser = usuarioService.getSocketForUser(userMedico);
            try {
                socketForUser.sendMessage(new TextMessage("S"));
            } catch (IOException e) {

            }
        }
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentoRepository.findAll();
    }

    public List<Atendimento> getTodayAtendimentos() {
        return atendimentoRepository.findAllByDataAtendimentoToday();
    }
}

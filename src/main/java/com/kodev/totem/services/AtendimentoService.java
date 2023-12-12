package com.kodev.totem.services;

import com.kodev.totem.models.Atendimento;
import com.kodev.totem.models.Medico;
import com.kodev.totem.models.Paciente;
import com.kodev.totem.models.Usuario;
import com.kodev.totem.push.ExpoPushNotification;
import com.kodev.totem.repositories.AtendimentoRepository;
import com.kodev.totem.repositories.MedicoRepository;
import com.kodev.totem.repositories.PacienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Transient;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
    public Atendimento criaAtendimento(Atendimento atendimento) throws EntityNotFoundException {

        String nomePacienteToken = atendimento.getPaciente().getNomeCompleto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM às HH:mm");
        String horarioToken = atendimento.getDataAtendimento().format(formatter);

        if(!atendimento.getPaciente().isAtivo()) {
            throw new EntityNotFoundException();
        }

        try {
            ExpoPushNotification.sendPush(atendimento.getMedico().getToken(), "Novo atendimento marcado para " + horarioToken, "Novo Atendimento Marcado!");
        } catch (Exception e) {
            log.error("Não foi possível enviar push notification!");
        }
        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> getAtendimentosByMedicoId(Long id) {
        return atendimentoRepository.getAtendimentosByMedico_MedicoId(id);
    }

    public List<Atendimento> getAtendimentosByMedicoIdToday(Long id) {
        return atendimentoRepository.getAtendimentosByMedico_MedicoId_Today(id);
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

        LocalDateTime today = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        LocalDateTime startOfDay= today.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay= today.toLocalDate().atTime(LocalTime.MAX);

        List<Atendimento> atendimentos = atendimentoRepository.findByPacienteAndDataAtendimentoBetween(paciente, startOfDay, endOfDay);

        for (Atendimento aT : atendimentos) {
            try {
                aT.setFotoPaciente(fotoPaciente.getBytes());
            } catch (Exception e ) {
                System.out.println(e.getMessage());
            }

            if(aT.isChegou()) continue;

            aT.setChegou(true);

            atendimentoRepository.save(aT);

            Medico medico = aT.getMedico();

            String nomePacienteToken = aT.getPaciente().getNomeCompleto();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String horarioToken = aT.getDataAtendimento().format(formatter);

            String mensagem = "Paciente " + nomePacienteToken + " chegou para consulta de " + horarioToken;
            try {
                ExpoPushNotification.sendPush(medico.getToken(), mensagem, "Paciente chegou!");
            } catch (Exception e) {
                log.atError().log("Could not send message to Medico");
                log.atError().log(e.getMessage());
            }
        }
    }


    public List<Atendimento> getAtendimentosByMedicoIdByDay(Long id, LocalDateTime date)  {
        return  atendimentoRepository.getAtendimentosByMedico_MedicoId_OnDate(id, date);
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

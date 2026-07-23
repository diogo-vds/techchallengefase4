package com.postech.techchallenge.fase3.hospital.notificacao.service;

import com.postech.techchallenge.fase3.hospital.notificacao.dto.Notificacaorecord;
import com.postech.techchallenge.fase3.hospital.notificacao.enums.TipoNotificacaoEnum;
import com.postech.techchallenge.fase3.hospital.notificacao.envio.IEnvioNotificacaoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoService {

    private final List<IEnvioNotificacaoService> envioNotificacaoServices;

    public NotificacaoService(List<IEnvioNotificacaoService> envioNotificacaoServices) {
        this.envioNotificacaoServices = envioNotificacaoServices;
    }

    public void enviarLembrete(Notificacaorecord notificacaorecord) {
        String assunto = "Envio de notificacao para: " + notificacaorecord.destinatario();
        
        String corpo = String.format(
            "Olá %s,\n\nEsta é uma notificação referente à sua consulta agendada para %s.\n\nStatus: %s",
            notificacaorecord.destinatario(),
            notificacaorecord.assunto(),
            notificacaorecord.corpo()
        );

        envioNotificacaoServices.stream().filter(service -> service.getTipoNotificacao().equals(TipoNotificacaoEnum.EMAIL.ordinal()))
                .findFirst()
                .ifPresent(service -> service.enviarNotificacao(new Notificacaorecord(
                        notificacaorecord.destinatario(),
                        assunto,
                        corpo
                )));
    }
}

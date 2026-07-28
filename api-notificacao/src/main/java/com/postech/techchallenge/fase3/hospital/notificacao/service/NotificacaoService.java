package com.postech.techchallenge.fase3.hospital.notificacao.service;

import com.postech.techchallenge.fase3.hospital.notificacao.dto.Notificacaorecord;
import com.postech.techchallenge.fase3.hospital.notificacao.enums.TipoNotificacaoEnum;
import com.postech.techchallenge.fase3.hospital.notificacao.envio.IEnvioNotificacaoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoService {

    private static final String TITULO = "Nova avaliação crítica recebida";

    private final List<IEnvioNotificacaoService> envioNotificacaoServices;

    public NotificacaoService(List<IEnvioNotificacaoService> envioNotificacaoServices) {
        this.envioNotificacaoServices = envioNotificacaoServices;
    }

    public void enviarLembrete(Notificacaorecord notificacaorecord) {
        String assunto = "Envio de notificacao para: admin@fiap.com.br";
        
        String corpo = String.format(
                """
                     %s\s
                     Descrição:
                     %s \
                     Nota: %s
                     Urgência: Alta
                     Data: %S
                    """,
            TITULO,
            notificacaorecord.descricao(),
            notificacaorecord.nota(),
            notificacaorecord.dataCadastro()
        );

        envioNotificacaoServices.stream().filter(service -> service.getTipoNotificacao().equals(TipoNotificacaoEnum.EMAIL.ordinal()))
                .findFirst()
                .ifPresent(service ->
                        service.enviarNotificacao(notificacaorecord));
    }
}

package com.postech.techchallenge.fase3.hospital.notificacao.envio.impl;

import com.postech.techchallenge.fase3.hospital.notificacao.dto.Notificacaorecord;
import com.postech.techchallenge.fase3.hospital.notificacao.enums.TipoNotificacaoEnum;
import com.postech.techchallenge.fase3.hospital.notificacao.envio.IEnvioNotificacaoService;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EnvioEmailService implements IEnvioNotificacaoService {

    private static final Logger logger = Logger.getLogger(EnvioEmailService.class.getName());
    private static final String TITULO = "Nova avaliação crítica recebida";
    private static final String DESTINATARIO = "admin@fiap.com.br";

    @Override
    public Integer getTipoNotificacao() {
        return TipoNotificacaoEnum.EMAIL.ordinal();
    }

    @Override
    public void enviarNotificacao(Notificacaorecord notificacaorecord) {
        logger.info("**************************************************");
        logger.info("ENVIANDO EMAIL PARA: " + DESTINATARIO);
        logger.info("ASSUNTO: " + TITULO);
        logger.info("Descrição:: " + notificacaorecord.descricao());
        logger.info("Nota: " + notificacaorecord.nota());
        logger.info("Urgência: Alta");
        logger.info("Data: " + notificacaorecord.dataCadastro());
        logger.info("**************************************************");
    }
}

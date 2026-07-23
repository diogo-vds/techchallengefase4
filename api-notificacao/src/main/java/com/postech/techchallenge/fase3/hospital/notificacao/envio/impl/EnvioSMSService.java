package com.postech.techchallenge.fase3.hospital.notificacao.envio.impl;

import com.postech.techchallenge.fase3.hospital.notificacao.dto.Notificacaorecord;
import com.postech.techchallenge.fase3.hospital.notificacao.enums.TipoNotificacaoEnum;
import com.postech.techchallenge.fase3.hospital.notificacao.envio.IEnvioNotificacaoService;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EnvioSMSService implements IEnvioNotificacaoService {

    private static final Logger logger = Logger.getLogger(EnvioSMSService.class.getName());

    @Override
    public Integer getTipoNotificacao() {
        return TipoNotificacaoEnum.SMS.ordinal();
    }

    @Override
    public void enviarNotificacao(Notificacaorecord notificacaorecord) {
        logger.info("**************************************************");
        logger.info("ENVIANDO SMS PARA: " + notificacaorecord.destinatario());
        logger.info("ASSUNTO: " + notificacaorecord.assunto());
        logger.info("CORPO: " + notificacaorecord.corpo());
        logger.info("**************************************************");
    }

}


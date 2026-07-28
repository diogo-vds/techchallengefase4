package com.postech.techchallenge.fase3.hospital.notificacao.envio.impl;

import com.postech.techchallenge.fase3.hospital.notificacao.dto.Notificacaorecord;
import com.postech.techchallenge.fase3.hospital.notificacao.enums.TipoNotificacaoEnum;
import com.postech.techchallenge.fase3.hospital.notificacao.envio.IEnvioNotificacaoService;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EnvioWhatsAppService implements IEnvioNotificacaoService {
    private static final Logger logger = Logger.getLogger(EnvioWhatsAppService.class.getName());
    private static final String ID_USUARIO = "123456789";
    private static final String TITULO = "Nova avaliação crítica recebida";

    @Override
    public Integer getTipoNotificacao() {
        return TipoNotificacaoEnum.WHATSAPP.ordinal();
    }

    @Override
    public void enviarNotificacao(Notificacaorecord notificacaorecord) {
        logger.info("**************************************************");
        logger.info("ENVIANDO WHATSAPP PARA: " + ID_USUARIO);
        logger.info("ASSUNTO: " + TITULO);
        logger.info("Descrição:: " + notificacaorecord.descricao());
        logger.info("Nota: " + notificacaorecord.nota());
        logger.info("Urgência: Alta");
        logger.info("Data: " + notificacaorecord.dataCadastro());
        logger.info("**************************************************");
    }
}

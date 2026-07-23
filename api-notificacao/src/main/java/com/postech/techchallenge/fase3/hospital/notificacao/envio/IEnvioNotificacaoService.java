package com.postech.techchallenge.fase3.hospital.notificacao.envio;

import com.postech.techchallenge.fase3.hospital.notificacao.dto.Notificacaorecord;

public interface IEnvioNotificacaoService {
    Integer getTipoNotificacao();
    void enviarNotificacao(Notificacaorecord notificacaorecord);
}

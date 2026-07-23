package com.postech.techchallenge.fase3.hospital.notificacao.enums;

public enum TipoNotificacaoEnum {
    EMAIL("E", "Email"),
    SMS("S", "SMS"),
    WHATSAPP("W", "WhatsApp");

    private final String codigo;
    private final String descricao;

    TipoNotificacaoEnum(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * Converte um código para o enum correspondente
     * @param codigo O código do status
     * @return O enum correspondente ao código
     */
    public static TipoNotificacaoEnum fromCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        for (TipoNotificacaoEnum perfil : TipoNotificacaoEnum.values()) {
            if (perfil.codigo.equals(codigo)) {
                return perfil;
            }
        }
        throw new IllegalArgumentException("Sigla de notificação inválida: " + codigo);
    }

    /**
     * Converte uma descrição para o enum correspondente
     * @param descricao A descricao do status
     * @return O enum correspondente à descrição
     */
    public static TipoNotificacaoEnum fromDescricao(String descricao) {
        if (descricao == null || descricao.isEmpty()) {
            return null;
        }
        for (TipoNotificacaoEnum perfil : TipoNotificacaoEnum.values()) {
            if (perfil.descricao.equalsIgnoreCase(descricao)) {
                return perfil;
            }
        }
        throw new IllegalArgumentException("Descrição de notificação inválida: " + descricao);
    }
}

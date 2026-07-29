package com.postech.techchallenge.fase4.avaliacao.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AvaliacaoRequestRecord(
        @NotBlank
        String descricao,
        @Min(0)
        @Max(10)
        Integer nota
) {}

package model;

import java.io.Serializable;
import java.util.List;

/**
 * Representa uma despesa com valor global fixo (ex: Transporte, Aluguel de espaço),
 * cujo montante total é dividido igualmente entre os participantes selecionados.
 */
public class DespesaFixa extends Despesa implements Serializable {
    private static final long serialVersionUID = 1L;

    public DespesaFixa(String descricao, double valorInformado, List<Participante> participantesEnvolvidos) {
        super(descricao, valorInformado, participantesEnvolvidos);
    }

    @Override
    public double calcularValorPara(Participante participante) {
        // Se o participante não faz parte desta despesa, o custo para ele é zero
        if (!getParticipantesEnvolvidos().contains(participante)) {
            return 0.0;
        }
        
        int totalParticipantes = getParticipantesEnvolvidos().size();
        if (totalParticipantes == 0) return 0.0;

        // Rateio igualitário do valor global entre os envolvidos
        double valorDividido = getValorInformado() / totalParticipantes;
        
        // Arredondamento matemático rigoroso para duas casas decimais (centavos)
        return Math.round(valorDividido * 100.0) / 100.0;
    }
}
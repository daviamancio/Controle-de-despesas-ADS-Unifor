package model;

import java.io.Serializable;
import java.util.List;

/**
 * Representa uma despesa cobrada de forma unitária por pessoa
 * (ex: Ingressos individuais, refeições por cabeça).
 */
public class DespesaPorPessoa extends Despesa implements Serializable {
    private static final long serialVersionUID = 1L;

    public DespesaPorPessoa(String descricao, double valorUnitario, List<Participante> participantesEnvolvidos) {
        super(descricao, valorUnitario, participantesEnvolvidos);
    }

    @Override
    public double calcularValorPara(Participante participante) {
        // Verifica se o participante está incluído no escopo da despesa
        if (!getParticipantesEnvolvidos().contains(participante)) {
            return 0.0;
        }
        
        // O valor informado atua como o custo unitário aplicado a cada participante envolvido
        double valorUnitario = getValorInformado();
        
        // Garante a precisão decimal correta
        return Math.round(valorUnitario * 100.0) / 100.0;
    }
}
package model;

import java.util.List;

public class DespesaPorPessoa extends Despesa {
    private static final long serialVersionUID = 1L;

    public DespesaPorPessoa(String descricao, double valorUnitario, List<Participante> participantesEnvolvidos) {
        super(descricao, valorUnitario, participantesEnvolvidos);
    }

    @Override
    public String getTipoDescricao() {
        return "Por pessoa";
    }

    @Override
    public double calcularValorPara(Participante participante) {
        if (!envolve(participante)) {
            return 0.0;
        }
        return Math.round(getValorInformado() * 100.0) / 100.0;
    }
}
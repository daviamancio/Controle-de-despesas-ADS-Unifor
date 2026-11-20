package model;

import java.util.List;

public class DespesaFixa extends Despesa {
    private static final long serialVersionUID = 1L;

    public DespesaFixa(String descricao, double valorInformado, List<Participante> participantesEnvolvidos) {
        super(descricao, valorInformado, participantesEnvolvidos);
    }

    @Override
    public String getTipoDescricao() {
        return "Fixa";
    }

    @Override
    public double calcularValorPara(Participante participante) {
        if (!envolve(participante)) {
            return 0.0;
        }
        List<Participante> envolvidos = getParticipantesEnvolvidos();
        int qtd = envolvidos.size();
        if (qtd == 0) {
            return 0.0;
        }

        long totalCentavos = Math.round(getValorInformado() * 100.0);
        long baseCentavos = totalCentavos / qtd;
        long restoCentavos = totalCentavos % qtd;

        int indice = envolvidos.indexOf(participante);
        long centavosParticipante = baseCentavos + (indice < restoCentavos ? 1L : 0L);
        return centavosParticipante / 100.0;
    }
}
package model;

import java.io.Serializable;
import java.util.List;

/**
 * Classe abstrata que serve de base para os diferentes tipos de despesa.
 * Aplica o conceito de polimorfismo, onde cada subclasse define sua própria regra de rateio.
 */
public abstract class Despesa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String descricao;
    private double valorInformado;
    private List<Participante> participantesEnvolvidos;

    public Despesa(String descricao, double valorInformado, List<Participante> participantesEnvolvidos) {
        setDescricao(descricao);
        setValorInformado(valorInformado);
        setParticipantesEnvolvidos(participantesEnvolvidos);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição da despesa não pode ser vazia.");
        }
        this.descricao = descricao.trim();
    }

    public double getValorInformado() {
        return valorInformado;
    }

    public void setValorInformado(double valorInformado) {
        // Regra do edital: despesa deve ser estritamente maior que zero
        if (valorInformado <= 0) {
            throw new IllegalArgumentException("O valor da despesa deve ser maior que zero.");
        }
        this.valorInformado = valorInformado;
    }

    public List<Participante> getParticipantesEnvolvidos() {
        return participantesEnvolvidos;
    }

    public void setParticipantesEnvolvidos(List<Participante> participantesEnvolvidos) {
        if (participantesEnvolvidos == null || participantesEnvolvidos.isEmpty()) {
            throw new IllegalArgumentException("A despesa deve estar vinculada a pelo menos um participante.");
        }
        this.participantesEnvolvidos = participantesEnvolvidos;
    }

    /**
     * Método abstrato polimórfico. Cada tipo de despesa implementa 
     * a sua própria lógica de cálculo individual para um dado participante.
     */
    public abstract double calcularValorPara(Participante participante);
}
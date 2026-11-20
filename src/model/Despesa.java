package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public double getValor() {
        return valorInformado;
    }

    public void setValorInformado(double valorInformado) {
        if (valorInformado <= 0 || Double.isNaN(valorInformado) || Double.isInfinite(valorInformado)) {
            throw new IllegalArgumentException("O valor da despesa deve ser maior que zero.");
        }
        this.valorInformado = Math.round(valorInformado * 100.0) / 100.0;
    }

    public List<Participante> getParticipantesEnvolvidos() {
        return Collections.unmodifiableList(participantesEnvolvidos);
    }

    public void setParticipantesEnvolvidos(List<Participante> participantes) {
        if (participantes == null || participantes.isEmpty()) {
            throw new IllegalArgumentException("A despesa deve envolver ao menos um participante.");
        }
        List<Participante> copia = new ArrayList<>();
        for (Participante p : participantes) {
            if (p == null) {
                throw new IllegalArgumentException("Participante inválido na lista da despesa.");
            }
            if (!copia.contains(p)) {
                copia.add(p);
            }
        }
        if (copia.isEmpty()) {
            throw new IllegalArgumentException("A despesa deve envolver ao menos um participante.");
        }
        this.participantesEnvolvidos = copia;
    }

    public boolean envolve(Participante participante) {
        if (participante == null) {
            return false;
        }
        return participantesEnvolvidos.contains(participante);
    }

    public abstract String getTipoDescricao();

    public abstract double calcularValorPara(Participante participante);
}
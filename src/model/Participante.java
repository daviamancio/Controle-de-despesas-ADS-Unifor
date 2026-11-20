package model;

import java.io.Serializable;
import java.util.Objects;

public class Participante implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigo;
    private String nome;
    private double orcamento;

    public Participante(int codigo, String nome, double orcamento) {
        setCodigo(codigo);
        setNome(nome);
        setOrcamento(orcamento);
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("O código do participante deve ser maior que zero.");
        }
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do participante não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public double getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(double orcamento) {
        if (orcamento < 0 || Double.isNaN(orcamento) || Double.isInfinite(orcamento)) {
            throw new IllegalArgumentException("O orçamento disponível não pode ser negativo.");
        }
        this.orcamento = Math.round(orcamento * 100.0) / 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participante)) return false;
        Participante that = (Participante) o;
        return codigo == that.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + nome;
    }
}
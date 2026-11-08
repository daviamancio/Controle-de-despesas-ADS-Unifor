package model;

import java.io.Serializable;

/**
 * Representa um participante da atividade/passeio.
 * Implementa Serializable para permitir o armazenamento binário completo do estado.
 */
public class Participante implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nome;
    private double orcamentoDisponivel;

    public Participante(String codigo, String nome, double orcamentoDisponivel) {
        setCodigo(codigo);
        setNome(nome);
        setOrcamentoDisponivel(orcamentoDisponivel);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código do participante não pode ser vazio.");
        }
        this.codigo = codigo.trim();
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

    public double getOrcamentoDisponivel() {
        return orcamentoDisponivel;
    }

    public void setOrcamentoDisponivel(double orcamentoDisponivel) {
        // Regra de negócio do edital: orçamento deve ser maior ou igual a zero
        if (orcamentoDisponivel < 0) {
            throw new IllegalArgumentException("O orçamento disponível não pode ser negativo.");
        }
        this.orcamentoDisponivel = orcamentoDisponivel;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Participante that = (Participante) obj;
        return codigo.equalsIgnoreCase(that.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.toLowerCase().hashCode();
    }
}
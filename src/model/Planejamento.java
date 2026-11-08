package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe central do modelo (MVC). Gerencia a lista de participantes e despesas,
 * aplicando as regras de negócio, validações de integridade e cálculos de custos.
 */
public class Planejamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Participante> participantes;
    private List<Despesa> despesas;

    public Planejamento() {
        // Utilização de ArrayList para garantir eficiência e ordem de inserção
        this.participantes = new ArrayList<>();
        this.despesas = new ArrayList<>();
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    public List<Despesa> getDespesas() {
        return despesas;
    }

    public void adicionarParticipante(Participante p) {
        if (p == null) {
            throw new IllegalArgumentException("Participante não pode ser nulo.");
        }
        // Validação de código duplicado exigida pelo edital
        if (buscarParticipantePorCodigo(p.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe um participante com o código: " + p.getCodigo());
        }
        participantes.add(p);
    }

    public void removerParticipante(String codigo) {
        Participante p = buscarParticipantePorCodigo(codigo);
        if (p == null) {
            throw new IllegalArgumentException("Participante não encontrado.");
        }
        // Regra de negócio: impede a exclusão se o participante estiver vinculado a despesas
        for (Despesa d : despesas) {
            if (d.getParticipantesEnvolvidos().contains(p)) {
                throw new IllegalArgumentException("Não é possível remover o participante pois ele está vinculado a despesas ativas.");
            }
        }
        participantes.remove(p);
    }

    public Participante buscarParticipantePorCodigo(String codigo) {
        for (Participante p : participantes) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }

    public void adicionarDespesa(Despesa d) {
        if (d == null) {
            throw new IllegalArgumentException("Despesa não pode ser nula.");
        }
        despesas.add(d);
    }

    public void removerDespesa(Despesa d) {
        despesas.remove(d);
    }

    public double calcularTotalIndividual(Participante p) {
        double total = 0.0;
        // Percorre todas as despesas aplicando o polimorfismo da classe abstrata Despesa
        for (Despesa d : despesas) {
            total += d.calcularValorPara(p);
        }
        // Arredondamento preciso para duas casas decimais (evitando problemas de ponto flutuante)
        return Math.round(total * 100.0) / 100.0;
    }

    public double calcularCustoTotal() {
        double total = 0.0;
        for (Participante p : participantes) {
            total += calcularTotalIndividual(p);
        }
        return Math.round(total * 100.0) / 100.0;
    }
}
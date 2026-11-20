package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Planejamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Participante> participantes;
    private List<Despesa> despesas;

    public Planejamento() {
        this.participantes = new ArrayList<>();
        this.despesas = new ArrayList<>();
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(participantes);
    }

    public List<Despesa> getDespesas() {
        return Collections.unmodifiableList(despesas);
    }

    public void adicionarParticipante(Participante p) {
        if (p == null) {
            throw new IllegalArgumentException("Participante não pode ser nulo.");
        }
        if (buscarParticipantePorCodigo(p.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe participante cadastrado com o código " + p.getCodigo() + ".");
        }
        participantes.add(p);
    }

    public void atualizarParticipante(int codigo, String novoNome, double novoOrcamento) {
        Participante existente = buscarParticipantePorCodigo(codigo);
        if (existente == null) {
            throw new IllegalArgumentException("Participante com código " + codigo + " não encontrado.");
        }
        existente.setNome(novoNome);
        existente.setOrcamento(novoOrcamento);
    }

    public Participante buscarParticipantePorCodigo(int codigo) {
        for (Participante p : participantes) {
            if (p.getCodigo() == codigo) {
                return p;
            }
        }
        return null;
    }

    public Participante buscarParticipantePorNome(String nome) {
        if (nome == null) {
            return null;
        }
        String alvo = nome.trim();
        for (Participante p : participantes) {
            if (p.getNome().equalsIgnoreCase(alvo)) {
                return p;
            }
        }
        return null;
    }

    public boolean removerParticipantePorCodigo(int codigo) {
        Participante alvo = buscarParticipantePorCodigo(codigo);
        if (alvo == null) {
            return false;
        }
        for (Despesa d : despesas) {
            if (d.envolve(alvo) && d.getParticipantesEnvolvidos().size() <= 1) {
                throw new IllegalArgumentException(
                    "Não é possível remover " + alvo.getNome() + " pois a despesa \"" + d.getDescricao() + "\" ficaria sem participantes."
                );
            }
        }
        for (Despesa d : despesas) {
            if (d.envolve(alvo)) {
                List<Participante> restantes = new ArrayList<>(d.getParticipantesEnvolvidos());
                restantes.remove(alvo);
                d.setParticipantesEnvolvidos(restantes);
            }
        }
        return participantes.remove(alvo);
    }

    public boolean removerParticipante(String nome) {
        Participante alvo = buscarParticipantePorNome(nome);
        if (alvo == null) {
            return false;
        }
        return removerParticipantePorCodigo(alvo.getCodigo());
    }

    public void adicionarDespesa(Despesa d) {
        if (d == null) {
            throw new IllegalArgumentException("Despesa não pode ser nula.");
        }
        validarEnvolvidosCadastrados(d);
        despesas.add(d);
    }

    public void atualizarDespesa(int indice, Despesa novaDespesa) {
        if (indice < 0 || indice >= despesas.size()) {
            throw new IllegalArgumentException("Índice de despesa inválido.");
        }
        if (novaDespesa == null) {
            throw new IllegalArgumentException("Nova despesa não pode ser nula.");
        }
        validarEnvolvidosCadastrados(novaDespesa);
        despesas.set(indice, novaDespesa);
    }

    public boolean removerDespesa(int indice) {
        if (indice < 0 || indice >= despesas.size()) {
            return false;
        }
        despesas.remove(indice);
        return true;
    }

    public boolean removerDespesa(Despesa d) {
        return despesas.remove(d);
    }

    public double calcularTotalIndividual(Participante p) {
        if (p == null) {
            return 0.0;
        }
        long totalCentavos = 0L;
        for (Despesa d : despesas) {
            totalCentavos += Math.round(d.calcularValorPara(p) * 100.0);
        }
        return totalCentavos / 100.0;
    }

    public double calcularCustoTotal() {
        long totalCentavos = 0L;
        for (Participante p : participantes) {
            totalCentavos += Math.round(calcularTotalIndividual(p) * 100.0);
        }
        return totalCentavos / 100.0;
    }

    private void validarEnvolvidosCadastrados(Despesa d) {
        if (d.getParticipantesEnvolvidos().isEmpty()) {
            throw new IllegalArgumentException("A despesa deve ter ao menos um participante.");
        }
        for (Participante p : d.getParticipantesEnvolvidos()) {
            if (buscarParticipantePorCodigo(p.getCodigo()) == null) {
                throw new IllegalArgumentException("Participante " + p.getNome() + " não está cadastrado no planejamento.");
            }
        }
    }
}
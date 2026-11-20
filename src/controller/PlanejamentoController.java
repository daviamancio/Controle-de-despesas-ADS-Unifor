package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import model.Despesa;
import model.DespesaFixa;
import model.DespesaPorPessoa;
import model.Participante;
import model.Planejamento;
import persistence.PersistenciaPlanejamento;

public class PlanejamentoController {

    public static class ResultadoCarga {
        private final boolean sucesso;
        private final String mensagem;

        public ResultadoCarga(boolean sucesso, String mensagem) {
            this.sucesso = sucesso;
            this.mensagem = mensagem;
        }

        public boolean isSucesso() {
            return sucesso;
        }

        public String getMensagem() {
            return mensagem;
        }
    }

    private Planejamento planejamento;
    private final PersistenciaPlanejamento persistencia;
    private final String caminhoArquivo;

    public PlanejamentoController(PersistenciaPlanejamento persistencia, String caminhoArquivo) {
        if (persistencia == null) {
            throw new IllegalArgumentException("Mecanismo de persistência não pode ser nulo.");
        }
        this.persistencia = persistencia;
        this.caminhoArquivo = (caminhoArquivo == null || caminhoArquivo.trim().isEmpty())
                ? "dados_planejamento.dat"
                : caminhoArquivo.trim();
        this.planejamento = new Planejamento();
    }

    public Planejamento getPlanejamento() {
        return planejamento;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public ResultadoCarga carregarInicial() {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            this.planejamento = new Planejamento();
            return new ResultadoCarga(true, "Arquivo " + caminhoArquivo + " não encontrado. Iniciado planejamento vazio.");
        }
        try {
            Planejamento carregado = persistencia.carregar(caminhoArquivo);
            this.planejamento = carregado;
            return new ResultadoCarga(true, "Planejamento carregado de " + caminhoArquivo + ".");
        } catch (IOException | ClassNotFoundException | RuntimeException ex) {
            return new ResultadoCarga(
                false,
                "Não foi possível carregar " + caminhoArquivo + " (" + ex.getMessage() + "). O planejamento em uso foi preservado."
            );
        }
    }

    public void salvar() throws IOException {
        persistencia.salvar(caminhoArquivo, planejamento);
    }

    public void adicionarParticipante(String strCodigo, String nome, String strOrcamento) {
        int codigo = parseInteiroPositivo(strCodigo, "código");
        double orcamento = parseDoubleNaoNegativo(strOrcamento, "orçamento");
        Participante novo = new Participante(codigo, nome, orcamento);
        planejamento.adicionarParticipante(novo);
    }

    public void editarParticipante(int codigo, String novoNome, String strNovoOrcamento) {
        double novoOrcamento = parseDoubleNaoNegativo(strNovoOrcamento, "orçamento");
        planejamento.atualizarParticipante(codigo, novoNome, novoOrcamento);
    }

    public boolean removerParticipantePorCodigo(int codigo) {
        return planejamento.removerParticipantePorCodigo(codigo);
    }

    public void adicionarDespesa(String descricao, String strValor, boolean fixa, List<Participante> envolvidos) {
        double valor = parseDoublePositivo(strValor, "valor");
        Despesa nova = criarDespesa(descricao, valor, fixa, envolvidos);
        planejamento.adicionarDespesa(nova);
    }

    public void editarDespesa(int indice, String descricao, String strValor, boolean fixa, List<Participante> envolvidos) {
        double valor = parseDoublePositivo(strValor, "valor");
        Despesa atualizada = criarDespesa(descricao, valor, fixa, envolvidos);
        planejamento.atualizarDespesa(indice, atualizada);
    }

    public boolean removerDespesa(int indice) {
        return planejamento.removerDespesa(indice);
    }

    private Despesa criarDespesa(String descricao, double valor, boolean fixa, List<Participante> envolvidos) {
        if (fixa) {
            return new DespesaFixa(descricao, valor, envolvidos);
        }
        return new DespesaPorPessoa(descricao, valor, envolvidos);
    }

    private int parseInteiroPositivo(String texto, String rotulo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo " + rotulo + " não pode ser vazio.");
        }
        try {
            int valor = Integer.parseInt(texto.trim());
            if (valor <= 0) {
                throw new IllegalArgumentException("O campo " + rotulo + " deve ser maior que zero.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um número inteiro válido para " + rotulo + ".");
        }
    }

    private double parseDoublePositivo(String texto, String rotulo) {
        double valor = parseNumero(texto, rotulo);
        if (valor <= 0) {
            throw new IllegalArgumentException("O campo " + rotulo + " deve ser maior que zero.");
        }
        return valor;
    }

    private double parseDoubleNaoNegativo(String texto, String rotulo) {
        double valor = parseNumero(texto, rotulo);
        if (valor < 0) {
            throw new IllegalArgumentException("O campo " + rotulo + " não pode ser negativo.");
        }
        return valor;
    }

    private double parseNumero(String texto, String rotulo) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O campo " + rotulo + " não pode ser vazio.");
        }
        String normalizado = texto.trim().replace("R$", "").replace(" ", "").replace(",", ".");
        try {
            double valor = Double.parseDouble(normalizado);
            if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                throw new IllegalArgumentException("Informe um valor numérico válido para " + rotulo + ".");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Informe um valor numérico válido para " + rotulo + ".");
        }
    }
}
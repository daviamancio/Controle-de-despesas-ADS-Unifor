package view;

import model.*;
import persistence.GerenciadorArquivo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Visão (View) e Controlo (Controller) da interface gráfica.
 * Implementada em Java Swing com arquitetura MVC e design modernizado via Look and Feel.
 */
public class TelaPrincipal extends JFrame {
    private Planejamento planejamento;
    private final String CAMINHO_ARQUIVO = "dados_planejamento.dat";

    private JTabbedPane tabbedPane;
    private JTable tabelaParticipantes;
    private DefaultTableModel modeloTabelaParticipantes;
    private JTable tabelaDespesas;
    private DefaultTableModel modeloTabelaDespesas;
    private JTable tabelaTotais;
    private DefaultTableModel modeloTabelaTotais;

    public TelaPrincipal() {
        planejamento = new Planejamento();
        carregarDadosAutomaticamente();

        // Título alinhado com a identidade do projeto académico (ADS / Unifor)
        setTitle("SplitCore: Sistema Inteligente de Despesas Compartilhadas — ADS/Unifor");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuração dos separadores (Abas) da aplicação
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Participantes", criarPainelParticipantes());
        tabbedPane.addTab("Despesas", criarPainelDespesas());
        tabbedPane.addTab("Resumo e Totais", criarPainelTotais());

        add(tabbedPane);
        atualizarTodasTabelas();
    }

    private void carregarDadosAutomaticamente() {
        File f = new File(CAMINHO_ARQUIVO);
        if (f.exists()) {
            try {
                planejamento = GerenciadorArquivo.carregar(CAMINHO_ARQUIVO);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Aviso: Não foi possível recuperar os dados anteriores. Iniciando novo planejamento.", "Recuperação", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void salvarDadosAutomaticamente() {
        try {
            GerenciadorArquivo.salvar(CAMINHO_ARQUIVO, planejamento);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro crítico ao gravar dados no arquivo: " + e.getMessage(), "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel criarPainelParticipantes() {
        JPanel painel = new JPanel(new BorderLayout());

        modeloTabelaParticipantes = new DefaultTableModel(new String[]{"Código", "Nome do Participante", "Orçamento Disponível (R$)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaParticipantes = new JTable(modeloTabelaParticipantes);
        painel.add(new JScrollPane(tabelaParticipantes), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnAdicionar = new JButton("Adicionar Participante");
        JButton btnRemover = new JButton("Remover Selecionado");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnRemover);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> {
            String codigo = JOptionPane.showInputDialog(this, "Informe o código único do participante:");
            if (codigo == null || codigo.trim().isEmpty()) return;
            String nome = JOptionPane.showInputDialog(this, "Informe o nome do participante:");
            if (nome == null || nome.trim().isEmpty()) return;
            String strOrcamento = JOptionPane.showInputDialog(this, "Informe o orçamento disponível (R$):");
            if (strOrcamento == null) return;

            try {
                double orcamento = Double.parseDouble(strOrcamento.replace(",", "."));
                Participante p = new Participante(codigo, nome, orcamento);
                planejamento.adicionarParticipante(p);
                salvarDadosAutomaticamente();
                atualizarTodasTabelas();
                JOptionPane.showMessageDialog(this, "Participante cadastrado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro nos dados informados: " + ex.getMessage(), "Validação", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRemover.addActionListener(e -> {
            int linha = tabelaParticipantes.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, selecione um participante na tabela.");
                return;
            }
            String codigo = (String) modeloTabelaParticipantes.getValueAt(linha, 0);
            try {
                planejamento.removerParticipante(codigo);
                salvarDadosAutomaticamente();
                atualizarTodasTabelas();
                JOptionPane.showMessageDialog(this, "Participante removido com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Não foi possível remover: " + ex.getMessage(), "Regra de Negócio", JOptionPane.ERROR_MESSAGE);
            }
        });

        return painel;
    }

    private JPanel criarPainelDespesas() {
        JPanel painel = new JPanel(new BorderLayout());

        modeloTabelaDespesas = new DefaultTableModel(new String[]{"Descrição da Despesa", "Tipo", "Valor (R$)", "Envolvidos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaDespesas = new JTable(modeloTabelaDespesas);
        painel.add(new JScrollPane(tabelaDespesas), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnAdicionar = new JButton("Adicionar Despesa");
        JButton btnRemover = new JButton("Remover Despesa");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnRemover);
        painel.add(painelBotoes, BorderLayout.SOUTH);

        btnAdicionar.addActionListener(e -> {
            if (planejamento.getParticipantes().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cadastre pelo menos um participante antes de lançar despesas.");
                return;
            }

            String descricao = JOptionPane.showInputDialog(this, "Descrição da despesa:");
            if (descricao == null || descricao.trim().isEmpty()) return;

            String strValor = JOptionPane.showInputDialog(this, "Valor total ou unitário (R$):");
            if (strValor == null) return;

            String[] tipos = {"Fixa (Dividida igualmente)", "Por Pessoa (Custo unitário)"};
            String tipoEscolhido = (String) JOptionPane.showInputDialog(this, "Selecione a forma de rateio:", "Tipo de Despesa", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
            if (tipoEscolhido == null) return;

            List<Participante> envolvidos = new ArrayList<>();
            for (Participante p : planejamento.getParticipantes()) {
                int resp = JOptionPane.showConfirmDialog(this, "Incluir " + p.getNome() + " nesta despesa?", "Seleção de Participantes", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    envolvidos.add(p);
                }
            }

            if (envolvidos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "A despesa precisa estar associada a pelo menos um participante.");
                return;
            }

            try {
                double valor = Double.parseDouble(strValor.replace(",", "."));
                Despesa d;
                if (tipoEscolhido.startsWith("Fixa")) {
                    d = new DespesaFixa(descricao, valor, envolvidos);
                } else {
                    d = new DespesaPorPessoa(descricao, valor, envolvidos);
                }

                planejamento.adicionarDespesa(d);
                salvarDadosAutomaticamente();
                atualizarTodasTabelas();
                JOptionPane.showMessageDialog(this, "Despesa registada com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao registar despesa: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRemover.addActionListener(e -> {
            int linha = tabelaDespesas.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(this, "Selecione uma despesa na tabela para remover.");
                return;
            }
            Despesa d = planejamento.getDespesas().get(linha);
            planejamento.removerDespesa(d);
            salvarDadosAutomaticamente();
            atualizarTodasTabelas();
            JOptionPane.showMessageDialog(this, "Despesa removida com sucesso!");
        });

        return painel;
    }

    private JPanel criarPainelTotais() {
        JPanel painel = new JPanel(new BorderLayout());

        modeloTabelaTotais = new DefaultTableModel(new String[]{"Participante", "Total a Pagar (R$)", "Orçamento (R$)", "Situação / Saldo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaTotais = new JTable(modeloTabelaTotais);
        painel.add(new JScrollPane(tabelaTotais), BorderLayout.CENTER);

        return painel;
    }

    private void atualizarTodasTabelas() {
        modeloTabelaParticipantes.setRowCount(0);
        for (Participante p : planejamento.getParticipantes()) {
            modeloTabelaParticipantes.addRow(new Object[]{p.getCodigo(), p.getNome(), p.getOrcamentoDisponivel()});
        }

        modeloTabelaDespesas.setRowCount(0);
        for (Despesa d : planejamento.getDespesas()) {
            String tipo = (d instanceof DespesaFixa) ? "Fixa" : "Por Pessoa";
            modeloTabelaDespesas.addRow(new Object[]{d.getDescricao(), tipo, d.getValorInformado(), d.getParticipantesEnvolvidos().size() + " part."});
        }

        modeloTabelaTotais.setRowCount(0);
        for (Participante p : planejamento.getParticipantes()) {
            double totalPagar = planejamento.calcularTotalIndividual(p);
            double saldo = p.getOrcamentoDisponivel() - totalPagar;
            String status = (saldo >= 0) ? "Dentro do orçamento (Saldo: R$ " + String.format("%.2f", saldo) + ")" : "Excedido em R$ " + String.format("%.2f", Math.abs(saldo));
            modeloTabelaTotais.addRow(new Object[]{p.getNome(), totalPagar, p.getOrcamentoDisponivel(), status});
        }
    }

    public static void main(String[] args) {
        // Aplicação do Look and Feel Nimbus para conferir um aspeto moderno e polido à interface
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Caso o Nimbus não esteja disponível, recorre ao padrão do sistema
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }
}
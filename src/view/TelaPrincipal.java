package view;

import controller.PlanejamentoController;
import controller.PlanejamentoController.ResultadoCarga;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Despesa;
import model.Participante;
import persistence.GerenciadorArquivo;

public class TelaPrincipal extends JFrame {

    private static final String CAMINHO_ARQUIVO = "dados_planejamento.dat";
    private static final Locale LOCALE_PT_BR = new Locale("pt", "BR");

    private final PlanejamentoController controller;
    private final NumberFormat moedaBR = NumberFormat.getCurrencyInstance(LOCALE_PT_BR);

    private JTabbedPane tabbedPane;

    // Aba Participantes
    private JTextField txtCodigo;
    private JTextField txtNome;
    private JTextField txtOrcamento;
    private DefaultTableModel modeloParticipantes;
    private JTable tabelaParticipantes;

    // Aba Despesas
    private JTextField txtDescricao;
    private JTextField txtValor;
    private JRadioButton rbFixa;
    private JRadioButton rbPorPessoa;
    private JPanel painelCheckboxes;
    private List<JCheckBox> checkBoxesParticipantes;
    private DefaultTableModel modeloDespesas;
    private JTable tabelaDespesas;

    // Aba Resumo e Totais
    private DefaultTableModel modeloTotais;
    private JTable tabelaTotais;
    private JLabel lblCustoTotal;
    private JLabel lblStatusArquivo;

    public TelaPrincipal() {
        super("SplitCore — Planejador de Despesas Compartilhadas");
        this.controller = new PlanejamentoController(new GerenciadorArquivo(), CAMINHO_ARQUIVO);
        this.checkBoxesParticipantes = new ArrayList<>();

        configurarJanela();
        inicializarComponentes();
        carregarDadosIniciais();
        atualizarTodasTabelas();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
    }

    private void inicializarComponentes() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Participantes", criarPainelParticipantes());
        tabbedPane.addTab("Despesas", criarPainelDespesas());
        tabbedPane.addTab("Resumo e Totais", criarPainelResumo());

        add(tabbedPane, BorderLayout.CENTER);
        add(criarBarraInferior(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelParticipantes() {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Cadastro / Edição de Participante"));

        txtCodigo = new JTextField();
        txtNome = new JTextField();
        txtOrcamento = new JTextField();

        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnEditar = new JButton("Salvar Edição");
        JButton btnRemover = new JButton("Remover Selecionado");
        JButton btnLimpar = new JButton("Limpar Campos");

        form.add(new JLabel("Código:"));
        form.add(new JLabel("Nome:"));
        form.add(new JLabel("Orçamento (R$):"));
        form.add(new JLabel(""));

        form.add(txtCodigo);
        form.add(txtNome);
        form.add(txtOrcamento);

        JPanel acoesForm = new JPanel(new GridLayout(1, 2, 4, 4));
        acoesForm.add(btnAdicionar);
        acoesForm.add(btnEditar);
        form.add(acoesForm);

        modeloParticipantes = new DefaultTableModel(new Object[]{"Código", "Nome", "Orçamento Disponível"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaParticipantes = new JTable(modeloParticipantes);
        tabelaParticipantes.getSelectionModel().addListSelectionListener(e -> preencherCamposParticipanteSelecionado());

        JPanel rodapeTabela = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodapeTabela.add(btnLimpar);
        rodapeTabela.add(btnRemover);

        btnAdicionar.addActionListener(e -> acaoAdicionarParticipante());
        btnEditar.addActionListener(e -> acaoEditarParticipante());
        btnRemover.addActionListener(e -> acaoRemoverParticipante());
        btnLimpar.addActionListener(e -> limparCamposParticipante());

        painel.add(form, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaParticipantes), BorderLayout.CENTER);
        painel.add(rodapeTabela, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelDespesas() {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topo = new JPanel(new BorderLayout(8, 8));

        JPanel campos = new JPanel(new GridLayout(2, 2, 8, 8));
        campos.setBorder(BorderFactory.createTitledBorder("Dados da Despesa"));

        txtDescricao = new JTextField();
        txtValor = new JTextField();

        rbFixa = new JRadioButton("Despesa Fixa (rateio entre envolvidos)", true);
        rbPorPessoa = new JRadioButton("Despesa Por Pessoa (valor por envolvido)");
        ButtonGroup grupoTipo = new ButtonGroup();
        grupoTipo.add(rbFixa);
        grupoTipo.add(rbPorPessoa);

        JPanel painelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelTipo.add(rbFixa);
        painelTipo.add(rbPorPessoa);

        campos.add(criarCampoRotulado("Descrição:", txtDescricao));
        campos.add(criarCampoRotulado("Valor (R$):", txtValor));
        campos.add(new JLabel("Tipo de Rateio:"));
        campos.add(painelTipo);

        painelCheckboxes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelCheckboxes.setBorder(BorderFactory.createTitledBorder("Participantes Envolvidos"));

        topo.add(campos, BorderLayout.NORTH);
        topo.add(painelCheckboxes, BorderLayout.CENTER);

        JPanel botoesDespesa = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdicionarDespesa = new JButton("Adicionar Despesa");
        JButton btnAtualizarDespesa = new JButton("Atualizar Selecionada");
        JButton btnRemoverDespesa = new JButton("Excluir Selecionada");
        botoesDespesa.add(btnAdicionarDespesa);
        botoesDespesa.add(btnAtualizarDespesa);
        botoesDespesa.add(btnRemoverDespesa);
        topo.add(botoesDespesa, BorderLayout.SOUTH);

        modeloDespesas = new DefaultTableModel(
                new Object[]{"#", "Descrição", "Tipo", "Valor Informado", "Envolvidos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaDespesas = new JTable(modeloDespesas);
        tabelaDespesas.getSelectionModel().addListSelectionListener(e -> preencherCamposDespesaSelecionada());

        btnAdicionarDespesa.addActionListener(e -> acaoAdicionarDespesa());
        btnAtualizarDespesa.addActionListener(e -> acaoAtualizarDespesa());
        btnRemoverDespesa.addActionListener(e -> acaoRemoverDespesa());

        painel.add(topo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabelaDespesas), BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelResumo() {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTotais = new DefaultTableModel(
                new Object[]{"Código", "Participante", "Orçamento", "Total a Pagar", "Saldo / Diferença", "Situação"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaTotais = new JTable(modeloTotais);

        JPanel rodapeResumo = new JPanel(new BorderLayout());
        lblCustoTotal = new JLabel("Custo Total do Planejamento: R$ 0,00", SwingConstants.LEFT);
        lblCustoTotal.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        JButton btnAtualizarResumo = new JButton("Recalcular / Atualizar");
        btnAtualizarResumo.addActionListener(e -> atualizarTodasTabelas());

        rodapeResumo.add(lblCustoTotal, BorderLayout.WEST);
        rodapeResumo.add(btnAtualizarResumo, BorderLayout.EAST);

        painel.add(new JScrollPane(tabelaTotais), BorderLayout.CENTER);
        painel.add(rodapeResumo, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarBarraInferior() {
        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        lblStatusArquivo = new JLabel("Arquivo: " + CAMINHO_ARQUIVO);
        JButton btnSalvar = new JButton("Salvar Planejamento");
        btnSalvar.addActionListener(e -> acaoSalvar());

        barra.add(lblStatusArquivo, BorderLayout.WEST);
        barra.add(btnSalvar, BorderLayout.EAST);
        return barra;
    }

    private JPanel criarCampoRotulado(String rotulo, JTextField campo) {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.add(new JLabel(rotulo), BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private void carregarDadosIniciais() {
        ResultadoCarga resultado = controller.carregarInicial();
        lblStatusArquivo.setText(resultado.getMensagem());
        if (!resultado.isSucesso()) {
            JOptionPane.showMessageDialog(this, resultado.getMensagem(), "Aviso de Persistência", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void acaoAdicionarParticipante() {
        try {
            controller.adicionarParticipante(txtCodigo.getText(), txtNome.getText(), txtOrcamento.getText());
            limparCamposParticipante();
            atualizarTodasTabelas();
        } catch (IllegalArgumentException ex) {
            exibirErro(ex.getMessage());
        }
    }

    private void acaoEditarParticipante() {
        int linha = tabelaParticipantes.getSelectedRow();
        if (linha < 0) {
            exibirErro("Selecione um participante na tabela para editar.");
            return;
        }
        try {
            int codigo = (int) modeloParticipantes.getValueAt(linha, 0);
            controller.editarParticipante(codigo, txtNome.getText(), txtOrcamento.getText());
            limparCamposParticipante();
            atualizarTodasTabelas();
        } catch (IllegalArgumentException ex) {
            exibirErro(ex.getMessage());
        }
    }

    private void acaoRemoverParticipante() {
        int linha = tabelaParticipantes.getSelectedRow();
        if (linha < 0) {
            exibirErro("Selecione um participante na tabela para remover.");
            return;
        }
        try {
            int codigo = (int) modeloParticipantes.getValueAt(linha, 0);
            if (controller.removerParticipantePorCodigo(codigo)) {
                limparCamposParticipante();
                atualizarTodasTabelas();
            }
        } catch (IllegalArgumentException ex) {
            exibirErro(ex.getMessage());
        }
    }

    private void preencherCamposParticipanteSelecionado() {
        int linha = tabelaParticipantes.getSelectedRow();
        if (linha >= 0) {
            int codigo = (int) modeloParticipantes.getValueAt(linha, 0);
            Participante p = controller.getPlanejamento().buscarParticipantePorCodigo(codigo);
            if (p != null) {
                txtCodigo.setText(String.valueOf(p.getCodigo()));
                txtNome.setText(p.getNome());
                txtOrcamento.setText(String.format(LOCALE_PT_BR, "%.2f", p.getOrcamento()));
            }
        }
    }

    private void limparCamposParticipante() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtOrcamento.setText("");
        tabelaParticipantes.clearSelection();
    }

    private void acaoAdicionarDespesa() {
        try {
            List<Participante> envolvidos = obterParticipantesSelecionados();
            controller.adicionarDespesa(txtDescricao.getText(), txtValor.getText(), rbFixa.isSelected(), envolvidos);
            limparCamposDespesa();
            atualizarTodasTabelas();
        } catch (IllegalArgumentException ex) {
            exibirErro(ex.getMessage());
        }
    }

    private void acaoAtualizarDespesa() {
        int linha = tabelaDespesas.getSelectedRow();
        if (linha < 0) {
            exibirErro("Selecione uma despesa na tabela para atualizar.");
            return;
        }
        try {
            List<Participante> envolvidos = obterParticipantesSelecionados();
            controller.editarDespesa(linha, txtDescricao.getText(), txtValor.getText(), rbFixa.isSelected(), envolvidos);
            limparCamposDespesa();
            atualizarTodasTabelas();
        } catch (IllegalArgumentException ex) {
            exibirErro(ex.getMessage());
        }
    }

    private void acaoRemoverDespesa() {
        int linha = tabelaDespesas.getSelectedRow();
        if (linha < 0) {
            exibirErro("Selecione uma despesa na tabela para excluir.");
            return;
        }
        if (controller.removerDespesa(linha)) {
            limparCamposDespesa();
            atualizarTodasTabelas();
        }
    }

    private void preencherCamposDespesaSelecionada() {
        int linha = tabelaDespesas.getSelectedRow();
        List<Despesa> despesas = controller.getPlanejamento().getDespesas();
        if (linha >= 0 && linha < despesas.size()) {
            Despesa d = despesas.get(linha);
            txtDescricao.setText(d.getDescricao());
            txtValor.setText(String.format(LOCALE_PT_BR, "%.2f", d.getValorInformado()));
            if ("Fixa".equalsIgnoreCase(d.getTipoDescricao())) {
                rbFixa.setSelected(true);
            } else {
                rbPorPessoa.setSelected(true);
            }
            for (JCheckBox cb : checkBoxesParticipantes) {
                Participante p = (Participante) cb.getClientProperty("participante");
                cb.setSelected(p != null && d.envolve(p));
            }
        }
    }

    private void limparCamposDespesa() {
        txtDescricao.setText("");
        txtValor.setText("");
        rbFixa.setSelected(true);
        for (JCheckBox cb : checkBoxesParticipantes) {
            cb.setSelected(false);
        }
        tabelaDespesas.clearSelection();
    }

    private List<Participante> obterParticipantesSelecionados() {
        List<Participante> selecionados = new ArrayList<>();
        for (JCheckBox cb : checkBoxesParticipantes) {
            if (cb.isSelected()) {
                Participante p = (Participante) cb.getClientProperty("participante");
                if (p != null) {
                    selecionados.add(p);
                }
            }
        }
        return selecionados;
    }

    private void acaoSalvar() {
        try {
            controller.salvar();
            lblStatusArquivo.setText("Salvo com sucesso em " + CAMINHO_ARQUIVO + ".");
            JOptionPane.showMessageDialog(this, "Planejamento salvo em " + CAMINHO_ARQUIVO + ".", "Salvar", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            exibirErro("Erro ao salvar planejamento: " + ex.getMessage());
        }
    }

    private void atualizarTodasTabelas() {
        atualizarTabelaParticipantes();
        atualizarCheckboxesEnvolvidos();
        atualizarTabelaDespesas();
        atualizarTabelaTotais();
    }

    private void atualizarTabelaParticipantes() {
        modeloParticipantes.setRowCount(0);
        for (Participante p : controller.getPlanejamento().getParticipantes()) {
            modeloParticipantes.addRow(new Object[]{
                p.getCodigo(),
                p.getNome(),
                moedaBR.format(p.getOrcamento())
            });
        }
    }

    private void atualizarCheckboxesEnvolvidos() {
        List<Participante> marcadosAntes = obterParticipantesSelecionados();
        painelCheckboxes.removeAll();
        checkBoxesParticipantes.clear();

        for (Participante p : controller.getPlanejamento().getParticipantes()) {
            JCheckBox cb = new JCheckBox(p.getNome() + " (Cód. " + p.getCodigo() + ")");
            cb.putClientProperty("participante", p);
            cb.setSelected(marcadosAntes.contains(p));
            checkBoxesParticipantes.add(cb);
            painelCheckboxes.add(cb);
        }

        painelCheckboxes.revalidate();
        painelCheckboxes.repaint();
    }

    private void atualizarTabelaDespesas() {
        modeloDespesas.setRowCount(0);
        List<Despesa> despesas = controller.getPlanejamento().getDespesas();
        for (int i = 0; i < despesas.size(); i++) {
            Despesa d = despesas.get(i);
            StringBuilder nomes = new StringBuilder();
            List<Participante> envolvidos = d.getParticipantesEnvolvidos();
            for (int j = 0; j < envolvidos.size(); j++) {
                if (j > 0) nomes.append(", ");
                nomes.append(envolvidos.get(j).getNome());
            }
            modeloDespesas.addRow(new Object[]{
                i + 1,
                d.getDescricao(),
                d.getTipoDescricao(),
                moedaBR.format(d.getValorInformado()),
                nomes.toString()
            });
        }
    }

    private void atualizarTabelaTotais() {
        modeloTotais.setRowCount(0);
        for (Participante p : controller.getPlanejamento().getParticipantes()) {
            double total = controller.getPlanejamento().calcularTotalIndividual(p);
            double diferenca = Math.round((p.getOrcamento() - total) * 100.0) / 100.0;
            String situacao;
            if (diferenca < -0.005) {
                situacao = "Excedido em " + moedaBR.format(Math.abs(diferenca));
            } else {
                situacao = "Dentro do orçamento";
            }
            modeloTotais.addRow(new Object[]{
                p.getCodigo(),
                p.getNome(),
                moedaBR.format(p.getOrcamento()),
                moedaBR.format(total),
                moedaBR.format(diferenca),
                situacao
            });
        }

        double custoTotal = controller.getPlanejamento().calcularCustoTotal();
        lblCustoTotal.setText("Custo Total do Planejamento: " + moedaBR.format(custoTotal));
    }

    private void exibirErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}
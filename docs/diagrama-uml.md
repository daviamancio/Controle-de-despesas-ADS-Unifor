# Diagrama de Classes UML — SplitCore

```mermaid
classDiagram
    class PersistenciaPlanejamento {
        <<interface>>
        +salvar(caminhoArquivo: String, planejamento: Planejamento) void
        +carregar(caminhoArquivo: String) Planejamento
    }

    class GerenciadorArquivo {
        +salvar(caminhoArquivo: String, planejamento: Planejamento) void
        +carregar(caminhoArquivo: String) Planejamento
    }

    class Participante {
        -serialVersionUID: long = 1L
        -codigo: int
        -nome: String
        -orcamento: double
        +Participante(codigo: int, nome: String, orcamento: double)
        +getCodigo() int
        +setCodigo(codigo: int) void
        +getNome() String
        +setNome(nome: String) void
        +getOrcamento() double
        +setOrcamento(orcamento: double) void
        +equals(o: Object) boolean
        +hashCode() int
    }

    class Despesa {
        <<abstract>>
        -serialVersionUID: long = 1L
        -descricao: String
        -valorInformado: double
        -participantesEnvolvidos: List~Participante~
        +Despesa(descricao: String, valorInformado: double, participantesEnvolvidos: List~Participante~)
        +getDescricao() String
        +setDescricao(descricao: String) void
        +getValorInformado() double
        +getValor() double
        +setValorInformado(valorInformado: double) void
        +getParticipantesEnvolvidos() List~Participante~
        +setParticipantesEnvolvidos(participantes: List~Participante~) void
        +calcularValorPara(participante: Participante)* double
    }

    class DespesaFixa {
        -serialVersionUID: long = 1L
        +DespesaFixa(descricao: String, valorInformado: double, participantesEnvolvidos: List~Participante~)
        +calcularValorPara(participante: Participante) double
    }

    class DespesaPorPessoa {
        -serialVersionUID: long = 1L
        +DespesaPorPessoa(descricao: String, valorUnitario: double, participantesEnvolvidos: List~Participante~)
        +calcularValorPara(participante: Participante) double
    }

    class Planejamento {
        -serialVersionUID: long = 1L
        -participantes: List~Participante~
        -despesas: List~Despesa~
        +Planejamento()
        +getParticipantes() List~Participante~
        +getDespesas() List~Despesa~
        +adicionarParticipante(p: Participante) void
        +atualizarParticipante(codigo: int, novoNome: String, novoOrcamento: double) void
        +buscarParticipantePorCodigo(codigo: int) Participante
        +buscarParticipantePorNome(nome: String) Participante
        +removerParticipantePorCodigo(codigo: int) boolean
        +removerParticipante(nome: String) boolean
        +adicionarDespesa(d: Despesa) void
        +atualizarDespesa(indice: int, novaDespesa: Despesa) void
        +removerDespesa(indice: int) boolean
        +removerDespesa(d: Despesa) boolean
        +calcularTotalIndividual(p: Participante) double
        +calcularCustoTotal() double
    }

    class PlanejamentoController {
        -planejamento: Planejamento
        -persistencia: PersistenciaPlanejamento
        -caminhoArquivo: String
        +PlanejamentoController(persistencia: PersistenciaPlanejamento, caminhoArquivo: String)
        +getPlanejamento() Planejamento
        +carregarInicial() ResultadoCarga
        +salvar() void
        +adicionarParticipante(strCodigo: String, nome: String, strOrcamento: String) void
        +editarParticipante(codigo: int, novoNome: String, strNovoOrcamento: String) void
        +removerParticipantePorCodigo(codigo: int) boolean
        +adicionarDespesa(descricao: String, strValor: String, fixa: boolean, envolvidos: List~Participante~) void
        +editarDespesa(indice: int, descricao: String, strValor: String, fixa: boolean, envolvidos: List~Participante~) void
        +removerDespesa(indice: int) boolean
    }

    class TelaPrincipal {
        -CAMINHO_ARQUIVO: String
        -controller: PlanejamentoController
        -tabbedPane: JTabbedPane
        -tabelaParticipantes: JTable
        -tabelaDespesas: JTable
        -tabelaTotais: JTable
        +TelaPrincipal()
        -atualizarTodasTabelas() void
        +main(args: String[]) void
    }

    PersistenciaPlanejamento <|.. GerenciadorArquivo : implements
    Despesa <|-- DespesaFixa : extends
    Despesa <|-- DespesaPorPessoa : extends
    Planejamento "1" o-- "0..*" Participante : gerencia
    Planejamento "1" o-- "0..*" Despesa : contém
    Despesa "0..*" --> "1..*" Participante : envolve
    PlanejamentoController --> Planejamento : coordena
    PlanejamentoController --> PersistenciaPlanejamento : usa
    TelaPrincipal --> PlanejamentoController : aciona
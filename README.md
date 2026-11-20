# SplitCore — Planejador de Despesas Compartilhadas ("Cabe no Bolso")

Aplicação desktop desenvolvida em **Java Swing** seguindo a arquitetura **MVC (Model-View-Controller)** e os pilares da **Programação Orientada a Objetos (POO)** para a disciplina de Programação Orientada a Objetos (ADS — Unifor).

O sistema permite organizar despesas compartilhadas de passeios ou atividades em grupo, diferenciar despesas fixas (com rateio igualitário e distribuição exata de centavos) de despesas por pessoa (custo unitário), verificar limites individuais de orçamento e persistir o planejamento em arquivo local.

---

## Requisitos e Ambiente

* **JDK:** Java Development Kit **17** ou superior (compatível com JDK 11+).
* **Dependências:** Nenhuma dependência externa; utiliza exclusivamente a API padrão do Java (`javax.swing`, `java.awt`, `java.io`, `java.util`).
* **IDE recomendada:** Visual Studio Code (Extension Pack for Java), IntelliJ IDEA ou Eclipse.

---

## Estrutura de Pastas

```text
PlanejamentoDespesas/
├── README.md                              # Documentação principal e instruções de execução
├── docs/
│   ├── diagrama-uml.md                    # Diagrama de classes UML e justificativas de POO
│   └── roteiro-testes.md                  # Caso de referência e cenários obrigatórios
├── evidencias/                            # Capturas de tela dos testes executados
├── dados_exemplo/                         # Arquivo binário de exemplo (.dat)
└── src/
    ├── model/                             # Entidades, regras de cálculo, rateio e validações
    │   ├── Despesa.java                   # Classe abstrata base (polimorfismo)
    │   ├── DespesaFixa.java               # Rateio com distribuição exata de centavos
    │   ├── DespesaPorPessoa.java          # Cobrança unitária por participante envolvido
    │   ├── Participante.java              # Código único, nome e orçamento disponível
    │   └── Planejamento.java              # Agregador de participantes, despesas e totais
    ├── persistence/                       # Contrato e implementação de serialização binária
    │   ├── PersistenciaPlanejamento.java  # Interface com operações salvar e carregar
    │   └── GerenciadorArquivo.java        # Serialização Java (ObjectOutputStream / ObjectInputStream)
    ├── controller/                        # Coordenação entre interface, validações e persistência
    │   └── PlanejamentoController.java
    └── view/                              # Interface gráfica em Java Swing (abas e tabelas)
        └── TelaPrincipal.java
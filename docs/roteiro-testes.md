# Roteiro de Testes e Evidências — SplitCore

## 1. Caso de Referência (Seção 4 do Edital)

### Participantes
| Código | Participante | Orçamento Disponível |
| :---: | :--- | :---: |
| 1 | Ana | R$ 70,00 |
| 2 | Bruno | R$ 60,00 |
| 3 | Carla | R$ 70,00 |

### Despesas
| Despesa | Tipo | Valor Informado | Participantes Envolvidos |
| :--- | :--- | :---: | :--- |
| Transporte | Fixa | R$ 90,00 | Ana, Bruno e Carla |
| Almoço | Por pessoa | R$ 20,00 | Ana, Bruno e Carla |
| Atividade recreativa | Fixa | R$ 30,00 | Ana e Carla |
| Sobremesa | Por pessoa | R$ 10,00 | Ana, Bruno e Carla |

### Resultados Esperados vs. Obtidos
| Participante | Transporte | Almoço | Atividade | Sobremesa | Total Esperado | Total Obtido | Situação |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :--- |
| Ana | R$ 30,00 | R$ 20,00 | R$ 15,00 | R$ 10,00 | **R$ 75,00** | **R$ 75,00** | Excedido em R$ 5,00 |
| Bruno | R$ 30,00 | R$ 20,00 | R$ 0,00 | R$ 10,00 | **R$ 60,00** | **R$ 60,00** | Dentro do orçamento |
| Carla | R$ 30,00 | R$ 20,00 | R$ 15,00 | R$ 10,00 | **R$ 75,00** | **R$ 75,00** | Excedido em R$ 5,00 |
| **Total Geral** | — | — | — | — | **R$ 210,00** | **R$ 210,00** | Custo total conferido |

---

## 2. Testes Adicionais Obrigatórios

| Cenário | Entrada / Ação | Resultado Esperado | Resultado Obtido | Evidência |
| :--- | :--- | :--- | :--- | :--- |
| **Centavos no rateio** | Despesa fixa de R$ 100,00 para códigos 1, 2 e 3 | R$ 33,34, R$ 33,33 e R$ 33,33; soma R$ 100,00 | R$ 33,34, R$ 33,33 e R$ 33,33; soma R$ 100,00 | `evidencias/02-rateio-centavos.png` |
| **Entrada ou vínculo inválido** | Despesa sem participantes, valor negativo/zero ou campo vazio | Recusar com mensagem clara sem alterar dados | Mensagem exibida; estado anterior mantido | Execução via GUI |
| **Alteração de orçamento** | Orçamento de Ana alterado para R$ 80,00 no caso principal | Custo mantido em R$ 75,00, agora dentro do limite | Custo R$ 75,00; dentro do orçamento (saldo R$ 5,00) | Execução via GUI |
| **Exclusão de despesa** | Excluir *Atividade recreativa* no caso principal original | R$ 60,00 por pessoa e R$ 180,00 no total | R$ 60,00 cada e R$ 180,00 no total | Execução via GUI |
| **Salvar e reiniciar** | Encerrar e reabrir aplicação com caso principal persistido | Recuperar dados, vínculos e totais sem duplicar | Dados e vínculos recuperados sem duplicação | `evidencias/03-caso-referencia-totais.png` |
| **Problema no arquivo** | Arquivo ausente ou corrompido ao iniciar | Iniciar vazio se ausente; avisar e preservar estado se falhar | Aviso exibido e planejamento em uso preservado | Execução via GUI |
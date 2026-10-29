# Paginação de transações

A tela é aberta pela aba Transactions da barra inferior e consulta as transações da
conta escolhida na aba Accounts.

## Contrato

Transactions usa `GetAccountTransactionsUseCase` e `AccountRepository` para consultar
`GET api/v1/accounts/{id}/transactions`, passando o ID da conta selecionada no DataStore.
O retorno continua sendo `TransactionPage`:
grupos de domínio, página atual, total de páginas e `nextPage` derivada.
O tamanho padrão é 50 grupos por página.

O endpoint global, `GetTransactionsUseCase` e `TransactionRepository` permanecem
disponíveis, mas não são usados pela tela.

## Disponibilidade da conta

Antes da página 1 (inclusive refresh/retry), `GetAccountTransactionsUseCase` consulta
`GET api/v1/accounts/{id}` e verifica `Account.isSelectable`: ativa, tipo asset ou cash.
Isso adiciona uma consulta de detalhes ao início de cada carga; próximas páginas
não repetem essa validação. Mudanças de status/tipo durante a paginação serão
verificadas na próxima carga da página 1.

Um 404 na consulta da conta ou de suas transações vira `ACCOUNT_UNAVAILABLE`;
uma conta retornada com sucesso, mas inelegível, vira `ACCOUNT_NOT_SELECTABLE`.
A interpretação específica de 404 nas transações fica em `AccountTransactionApiErrorMapper`,
sem alterar o tratamento do endpoint global.

Nesses casos a tela limpa os resultados/cursor e oferece escolher outra conta em
Accounts ou tentar novamente. A seleção persistida não é apagada nem substituída.
Falhas de rede, servidor, autenticação (401/403) ou mapeamento não são usadas para
concluir que a conta foi removida; mantêm o tratamento de retry existente.

Não se deduz indisponibilidade pela ausência em uma página da lista de contas.

`TransactionsResponse` decodifica `meta.pagination.current_page` e `total_pages`.
`TransactionPageDtoMapper` valida esses limites e converte os grupos; o repository
também rejeita respostas cuja página atual não corresponde à solicitada. Não se
infere o fim dos dados pelo número de itens, nem se segue URLs de paginação enviadas
pelo servidor. As chamadas continuam usando a URL HTTPS e o PAT configurados.

## Estado e carregamento

- A seleção é gravada pela lista de contas e observada aqui por
  `ObserveSelectedAccountUseCase`. Quando ainda não existe, a lista de contas
  inicializa a primeira conta selecionável. Sem conta selecionada, a tela orienta a
  escolha em Accounts e não faz consultas de transações.
- Falhas de leitura da seleção têm feedback e retry próprios, sem serem tratadas
  como ausência de seleção.
- Ao trocar de conta, a busca anterior é cancelada, os dados/cursor são limpos e a
  página 1 é carregada. Um identificador de geração impede que respostas ou erros
  de uma seleção anterior alterem a conta atual, inclusive na sequência A → B → A.
- Busca e filtros são preservados; a posição de scroll volta ao início ao trocar de conta.
- A primeira carga e o refresh usam `isLoading`/`error`.
- Próximas páginas usam `isLoadingMore`/`loadMoreError`, sem remover os dados visíveis.
- `nextPage = null` indica que não há mais páginas.
- Somente uma chamada é executada por vez. Eventos repetidos de scroll são ignorados.
- Uma falha não avança a página; a próxima tentativa precisa de `RetryNextPage`.
- Refresh bem-sucedido substitui todas as páginas acumuladas e reinicia o cursor.
- Grupos repetidos são deduplicados pelo ID; o agrupamento por data e os totais são
  recalculados sobre todos os grupos aceitos, inclusive quando um dia atravessa páginas.
- Falhas de mapeamento também preservam os dados anteriores e o cursor de retry.

## UI

A tela ocupa a aba Transactions e mantém cabeçalho, busca e filtros fixos enquanto a
lista rola. O rodapé da `LazyColumn` dispara `LoadNextPage` quando entra na área
visível. Sua chave inclui a conta e o cursor para que informações de layout da página
anterior não disparem outro carregamento antes de medir o conteúdo novo. Se a lista
não preencher a tela, novas páginas podem ser carregadas até preencher a área visível
ou chegar ao fim.

Loading, retry e fim da lista usam o tema global e os componentes compartilhados do
Design System. Os textos adicionados possuem inglês padrão e tradução em português.

Busca, tipo, período e categoria são filtros locais combinados, aplicados aos splits
acumulados. A categoria é selecionada por ID; a opção de todas as categorias remove
esse filtro, e splits sem categoria não correspondem a uma categoria específica.
As opções vêm de `GetCategoriesUseCase`, com carga de todas as páginas independente
da conta e das transações, além de loading, erro e retry próprios. A seleção é
preservada no refresh e na troca de conta. Os totais diários por moeda são
recalculados sobre os itens visíveis após os filtros.

A busca considera título, nome da categoria e conta, sem distinção de maiúsculas
ou acentos; não pesquisa tags. Ela informa quando os resultados ainda são parciais.
Um filtro sem resultados pode deixar o rodapé visível e carregar mais páginas à
procura de correspondências. Filtrar período ou categoria no servidor continua
sendo uma evolução separada.

### Tags das transações

`TransactionSplitDto`, `TransactionSplit` e `TransactionUiModel` possuem
`tags: List<String> = emptyList()`. `TransactionSplitDtoMapper` e
`TransactionListUiMapper` propagam as tags da API até a apresentação sem transformações.

Em `TransactionItemRow`, as tags substituem a exibição do horário. O composable
`TransactionTag` usa formato de etiqueta com ponta e furo decorativo, com cores
adaptadas aos temas claro e escuro. As etiquetas são distribuídas em um `FlowRow`
com quebra em linhas; transações sem tags não reservam esse espaço. `moment`
permanece no modelo e no mapper de UI, mas não é exibido na linha. Há previews
das etiquetas nos dois temas e da linha com tags; build e testes não foram
executados nesta etapa.

O suporte a transações `transfer` no mapper de UI continua pendente; uma página com
esse tipo gera erro de mapeamento em vez de descartar silenciosamente a transação.

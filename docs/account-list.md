# Lista de contas

`ui/accountlist` contém o contrato MVI, ViewModel, Route, tela Compose, modelos e
mapper de apresentação. A Route recebe o ViewModel por `hiltViewModel()` e coleta
o estado respeitando o ciclo de vida. A tela recebe apenas estado e ações.

## Dados

```text
AccountListViewModel → GetAccountsUseCase → AccountRepository
                                         ↓
                              AccountRepositoryImpl → FireflyApi
```

`GET api/v1/accounts` usa a URL e o PAT já salvos. A lista pede somente contas do
tipo asset (`AccountTypeFilter.ASSET_ACCOUNT`, convertido para `Asset account` por
`AccountTypeFilterMapper`). O repository consome `ApiResult` e traduz falhas para o
domínio, sem expor o wrapper HTTP ao use case ou ViewModel.

`GetAccountsUseCase.Input` recebe `page`, `limit` e `type` (padrões 1, 50 e `null`).
Os limites de `meta.pagination` controlam o carregamento incremental, e a página
retornada precisa corresponder à solicitada. O ViewModel preserva os dados ao falhar
uma atualização, deduplica contas por ID e exige retry explícito depois de uma falha
ao buscar a próxima página.

Os modelos de domínio usam `BigDecimal` para o saldo. Saldo ausente continua null;
valores inválidos provocam falha de mapeamento. A apresentação usa a moeda informada
pela conta, sem substituí-la pela moeda padrão do aparelho. Códigos personalizados
são apresentados explicitamente, com seu valor decimal.

A lista de transações por conta está integrada: `GetAccountTransactionsUseCase` e
`AccountRepository` consultam `GET api/v1/accounts/{id}/transactions`. O endpoint
global `api/v1/transactions`, `GetTransactionsUseCase` e `TransactionRepository`
permanecem no projeto, mas não são usados pela UI.

## Seleção da conta

A seleção é persistida no mesmo DataStore da configuração do servidor, na chave
`selected_account_id`, e observada por `ObserveSelectedAccountUseCase`. Apenas contas
ativas do tipo asset ou cash são selecionáveis (`Account.isSelectable`). Contas
inativas continuam visíveis, mas com o controle desabilitado.

`SelectAccountUseCase` grava uma escolha explícita do usuário. Após cada página
carregada, o ViewModel chama `InitializeSelectedAccountUseCase` com a primeira conta
selecionável; a operação é atômica e só grava se ainda não houver seleção, portanto
não substitui uma escolha existente. Se nenhuma conta selecionável for encontrada, a
seleção permanece ausente.

Falhas de leitura têm feedback e retry; falhas de gravação exibem erro e mantêm a
seleção observada. A URL do servidor é preservada ao trocar de conta; a seleção é
removida quando a URL ou o token configurado muda ou quando a configuração é
redefinida. Salvar a mesma URL e o mesmo token preserva a seleção. A tela de
transações consome essa seleção e não permite alterá-la.

## Interface

Após o setup, `MainAppRoute` abre `AccountListRoute` na aba Accounts. A lista mostra
nome, tipo, saldo atual, indicação de conta inativa e o estado selecionado. Há
refresh no cabeçalho, loading, erro com retry, estado vazio e paginação ao alcançar
o rodapé. A seleção é feita pelo card/radio button e fica desabilitada enquanto a
seleção é lida ou gravada e para contas inelegíveis.

`BrutusCard`, `BrutusScreenHeader` e os demais componentes usam o tema, tipografia,
formas e espaçamentos globais. Os textos da feature estão no `app`, com inglês padrão
e português em `values-pt`.

A navegação principal usa uma barra inferior com Accounts, Transactions e Categories.
A escolha de uma conta não troca de aba automaticamente; a tela Transactions passa
a usar a nova conta quando for aberta ou quando a seleção mudar.

Há previews da tela em claro/escuro, loading, vazio, erro inicial e erro de página.
Dados fictícios são usados somente nos previews.

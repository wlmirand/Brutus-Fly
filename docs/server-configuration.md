# Configuração do servidor

## Fluxo

`MainAppRoute` observa a configuração persistida via `MainViewModel` e casos de
uso. Enquanto o DataStore é lido, mostra loading; sem configuração, abre o setup;
com configuração, abre a lista de contas. Falhas na leitura oferecem retry e
redefinição.

`SetupViewModel` usa o mesmo contrato MVI das demais telas. `ConfigureServerUseCase`
delega ao repository a normalização, validação remota e persistência. A configuração
anterior só é substituída após sucesso em `GET api/v1/about`, com resposta Firefly
contendo `data.version`. A URL deve apontar para a raiz da instalação, não `/api/v1`.
Instalações em subdiretórios são suportadas, por exemplo `https://host/firefly/`.

## Persistência e segurança

- DataStore Preferences: `files/datastore/server_configuration.preferences_pb`.
- URL normalizada e PAT criptografado com AES-GCM; chave no Android Keystore.
- O mesmo DataStore guarda `selected_account_id` e as associações locais de ícones
  de categorias (`category_icons`). Alterar a URL ou o token limpa as preferências
  anteriores atomicamente ao salvar as novas credenciais; o reset também limpa
  todo o DataStore. Somente salvar a mesma URL e o mesmo token preserva as escolhas.
- HTTPS obrigatório, usando validação TLS padrão do Android; sem exceções para
  certificados inválidos ou HTTP local.
- Credenciais não são registradas em logs nem salvas no estado de restauração da UI.
- O arquivo é excluído de backup e transferência; `allowBackup` permanece desativado.
- Chave indisponível ou falha de decriptação exige redefinir e informar o token novamente.
- Redirecionamentos autenticados não são seguidos. Corrija a URL base se o servidor
  responder com redirecionamento.

## Network

`FireflyApiFactory` cria Retrofit/OkHttp a partir de uma configuração validada.
`ConfiguredFireflyApi` lê a configuração salva antes de cada operação; não há URL
placeholder nem bloqueio da thread principal para carregar credenciais.
O interceptor restringe o Bearer ao host, porta e caminho HTTPS configurados.

Após o setup, a aplicação abre a lista de contas, que usa `AccountRepositoryImpl`
com a URL e o PAT salvos e consulta contas do tipo asset. A conta selecionada é
persistida no mesmo DataStore e usada pela aba Transactions, que consulta
`GET api/v1/accounts/{id}/transactions` por meio de `AccountRepository`. O endpoint
global `api/v1/transactions`, `TransactionRepository` e `GetTransactionsUseCase`
permanecem disponíveis, mas não são usados pela UI.

Componentes visuais compartilhados estão em `:core:designsystem`. Textos próprios
do setup estão nos recursos do `app`, com inglês padrão e tradução em `values-pt`.

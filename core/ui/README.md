# UI infrastructure

`:core:ui` contém infraestrutura compartilhada de apresentação, sem dependência
de Compose, Design System, Hilt ou features.

## BaseViewModel

- `state`: `StateFlow` somente leitura, atualizado mesmo sem coletores.
- `onAction`: entrada abstrata para as ações da UI.
- `updateState`: transformação atômica; use estados imutáveis e funções puras.
- `updateIfState<T>`: transformação atômica somente para o subtipo esperado.
- `getCurrentStateAs<T>`: snapshot tipado, não uma operação de atualização atômica.
- `emitEvent`: envio suspenso de um efeito transitório.

A inicialização deve ser explícita no ViewModel concreto, normalmente no `init`,
com operações assíncronas em `viewModelScope.launch`. Não há `onInit` chamado
pela classe base nem efeitos associados à leitura de `state`.

Eventos usam `Channel.BUFFERED`, com buffer finito e um único coletor na UI.
O envio suspende quando o buffer está cheio. Vários coletores dividem os eventos;
não existe garantia de processamento exatamente uma vez. Não use eventos para
informações críticas ou persistentes: represente-as no estado.

Colete estado e eventos respeitando o ciclo de vida da tela. A feature define seus
próprios tipos de estado, ação e evento e decide como executar os casos de uso.

## Testes

`./gradlew :core:ui:testDebugUnitTest`

Os testes cobrem estado inicial, atualização sem coletores, atualização condicional,
concorrência e envio de evento antes da coleta. Exigem SDK Android configurado.

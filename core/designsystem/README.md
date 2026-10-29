# Brutus Fly Design System

Módulo Android/Compose independente das features e da camada de dados.

Envolva a aplicação e os previews com `BrutusFlyTheme`. Por padrão, ele acompanha
o modo claro/escuro do sistema, usando paletas próprias, sem DynamicColor.
Use `darkTheme = true` ou `false` para previews determinísticos.

## Fonte de verdade

- `theme/Color.kt`: paletas Material e cores semânticas claras/escuras.
- `theme/Typography.kt`: estilos globais via `MaterialTheme.typography`.
- `theme/Shape.kt`: formas via `MaterialTheme.shapes`.
- `theme/Spacing.kt`: margens e espaçamentos via `BrutusTheme.spacing`.
- `components/`: componentes compartilhados, sem dependências de features.

Componentes compartilhados: `BrutusSearchField`, `BrutusTextField`,
`BrutusPasswordField`, `BrutusButton` (com loading), `BrutusErrorMessage`,
`BrutusLoading`, `BrutusCard` e `BrutusScreenHeader`. As listas de contas/transações
e a tela de configuração usam os tokens globais; novas telas devem seguir o mesmo padrão.

Nas telas, use `MaterialTheme.colorScheme`, `MaterialTheme.typography`,
`MaterialTheme.shapes`, `BrutusTheme.colors` e `BrutusTheme.spacing`.
Evite cores, estilos de texto e margens locais. Dimensões específicas de elementos
(por exemplo, tamanho de ícones) podem continuar locais.

Mantenha componentes de negócio nas features. Extraia componentes para este módulo
quando representarem padrões compartilhados, não apenas para encapsular todo o Compose.

## Validação

Há testes locais de contraste das cores de texto (mínimo de 4,5:1), em
`src/test`. Execute `./gradlew :core:designsystem:testDebugUnitTest` em um
ambiente com o SDK Android configurado. Os previews permitem inspecionar os dois temas.

Cada componente em `components/` possui uma função de preview privada. A anotação
`@BrutusPreviews` gera as variantes clara e escura; o wrapper `BrutusPreview`
aplica o tema global, fundo e espaçamentos. Os exemplos cobrem os estados relevantes
de cada componente e podem ser explorados no modo interativo do Android Studio.

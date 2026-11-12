# CG-CoordinateX

Repositório dos laboratórios da disciplina de **Computação Gráfica**.

- **Professor:** Robson Pequeno de Sousa
- **Equipe:** Adrielly Carla · Maria Eduarda · Mirelle Casimiro · Nidlan Hendell · Rui Fernando

| Laboratório | Tema | Pasta |
|---|---|---|
| **Lab 1** | Transformação de coordenadas: Mundo → NDC → Dispositivo (e o caminho inverso) | [`Lab 1/`](Lab%201/) |
| **Lab 1 – Extra** | Tabela de cores (LUT) de 24 bits para pixels de 12 bits | [`Lab 1 - Extra/`](Lab%201%20-%20Extra/) |
| **Lab 3** | Transformações geométricas 2D (aula G5) em um polígono na origem, desenhado com o ponto médio (aula G3) | [`Lab 3/`](Lab%203/) |

Os dois labs também podem ser abertos por uma **interface gráfica única** (`frontend.Interface`), que tem uma tela inicial com um botão para cada lab.

---

## Sumário

1. [Pré-requisitos](#pré-requisitos)
2. [Estrutura do repositório](#estrutura-do-repositório)
3. [Como rodar](#como-rodar)
4. [Lab 1 – Transformação de coordenadas](#lab-1--transformação-de-coordenadas)
5. [Lab 1 – Extra: Tabela de cores](#lab-1--extra-tabela-de-cores)
6. [Interface gráfica (frontend)](#interface-gráfica-frontend)
7. [Problemas comuns](#problemas-comuns)

---

## Pré-requisitos

| Ferramenta | Versão | Observação |
|---|---|---|
| **JDK** | **24 ou superior** | Os `pom.xml` usam `maven.compiler.release = 24`. |
| **Maven** | 3.9+ | Opcional se você usar o IntelliJ. |
| IDE (opcional) | IntelliJ IDEA | O projeto foi desenvolvido com o `openjdk-24`. |

Confira as versões:

```bash
java -version
mvn -v
```

> ⚠️ O `java` que aparece no `java -version` precisa ser o **mesmo JDK (24+)** que o Maven usa (linha `Java version` do `mvn -v`). Se o `java` do PATH for antigo (ex.: Java 8/9), ajuste a variável `JAVA_HOME` e o `PATH`, ou use o caminho completo do `java.exe` do JDK novo.

Nenhuma biblioteca externa é usada: tudo é Java puro com **AWT/Swing**.

---

## Estrutura do repositório

```
CG-CoordinateX/
├── README.md
├── LICENSE
├── .gitignore
│
├── Lab 1/                                  # Lab 1 + interface gráfica
│   ├── pom.xml                             # inclui também o código do "Lab 1 - Extra"
│   └── src/main/java/
│       ├── app/
│       │   ├── Main.java                   # ponto de entrada do Lab 1 (console + tela cheia)
│       │   └── LeitorConsole.java          # leitura de números e do cenário no console
│       ├── modelo/
│       │   ├── JanelaMundo.java            # janela do mundo (xmin, xmax, ymin, ymax)
│       │   ├── Ponto.java                  # ponto real (mundo e NDC)
│       │   └── Pixel.java                  # ponto inteiro (dispositivo)
│       ├── transformacao/
│       │   ├── Transformacao.java          # classe abstrata com os 4 procedimentos
│       │   ├── TransformacaoNdc01.java     # Cenário I:  NDC em [0,1] x [0,1]
│       │   └── TransformacaoNdc11.java     # Cenário II: NDC em [-1,1] x [-1,1]
│       ├── dispositivo/
│       │   ├── Dispositivo.java            # resolução do monitor + frame buffer
│       │   ├── TelaDisplay.java            # janela em tela cheia
│       │   ├── PainelTela.java             # desenha o frame buffer
│       │   ├── OuvinteMouse.java           # clique -> caminho inverso
│       │   └── OuvinteTeclado.java         # ESC fecha o programa
│       └── frontend/
│           └── Interface.java              # interface gráfica com os dois labs
│
└── Lab 1 - Extra/                          # Tabela de cores
    ├── pom.xml
    └── src/main/java/tabelacores/
        ├── MainTabelaCores.java            # ponto de entrada do Lab Extra
        ├── TabelaCores.java                # a LUT: 4096 posições x 24 bits
        ├── FrameBufferIndexado.java        # frame buffer de índices de 12 bits
        ├── ControladorVideo.java           # índice -> tabela -> cor na imagem
        ├── Hexa.java                       # formatação hex/binário e leitura do índice
        ├── TelaCores.java                  # monta a janela e liga os eventos
        ├── PainelPaleta.java               # grade 64 x 64 com as 4096 cores
        ├── PainelInspetor.java             # painel com os dados da cor selecionada
        ├── OuvinteCliquePaleta.java        # clique na grade
        ├── OuvinteCampoIndice.java         # Enter / botão "Ver cor"
        ├── OuvinteBotaoTabela.java         # botões Colorida / Cinza / Fogo
        └── Estilo.java                     # cores e fontes da interface
```

> O `pom.xml` do **Lab 1** usa o `build-helper-maven-plugin` para adicionar `../Lab 1 - Extra/src/main/java` como fonte. Por isso a `frontend.Interface` consegue usar as classes do pacote `tabelacores`, e compilar o **Lab 1** já compila os dois labs.

---

## Como rodar

São três pontos de entrada:

| O que abre | Projeto | Classe `main` |
|---|---|---|
| Lab 1 (console + tela cheia) | `Lab 1` | `app.Main` |
| Interface gráfica com os dois labs | `Lab 1` | `frontend.Interface` |
| Lab Extra sozinho | `Lab 1 - Extra` | `tabelacores.MainTabelaCores` |
| Lab 3 (transformações 2D) | `Lab 3` | `app.Main` |

### Opção A — Terminal (Maven)

**1. Clone o repositório**

```bash
git clone <url-do-repositorio>
cd CG-CoordinateX
```

**2. Compile e rode o Lab 1** (também compila o código do Lab Extra)

```bash
cd "Lab 1"
mvn compile

# Lab 1 no console + tela cheia
java -cp target/classes app.Main

# ou: interface gráfica com os dois labs
java -cp target/classes frontend.Interface
```

**3. Compile e rode o Lab Extra sozinho**

```bash
cd "Lab 1 - Extra"
mvn compile
java -cp target/classes tabelacores.MainTabelaCores
```

> No Windows (PowerShell/CMD) os comandos são os mesmos; `target/classes` também funciona com `\`.

### Opção B — IntelliJ IDEA

1. `File → Open…` e selecione a pasta **`Lab 1`** (ou **`Lab 1 - Extra`**). O IntelliJ reconhece o `pom.xml` como projeto Maven.
2. Em `File → Project Structure → Project`, escolha um **SDK 24+**.
3. Abra a classe desejada (`app.Main`, `frontend.Interface` ou `tabelacores.MainTabelaCores`) e clique no ▶ ao lado do `main`.
4. Para o `app.Main`, use o painel **Run** como console para digitar os valores.

---

## Lab 1 – Transformação de coordenadas

### Objetivo

Dado um ponto no **sistema de coordenadas do mundo** (definido pelo usuário), calcular a sua posição no **NDC** (coordenadas normalizadas do dispositivo) e depois no **dispositivo** (pixel do monitor), acender esse pixel em **verde** e permitir o **caminho inverso** com o clique do mouse.

```
        SAÍDA:    Mundo --userToNdc--> NDC --ndcToDc--> Dispositivo
        ENTRADA:  Mundo <--ndcToUser-- NDC <--inpToNdc-- Dispositivo (clique)
```

### Os quatro procedimentos

Estão declarados na classe abstrata `transformacao.Transformacao` e implementados em cada cenário:

| Método | Faz |
|---|---|
| `userToNdc(Ponto)` | Mundo → NDC |
| `ndcToUser(Ponto)` | NDC → Mundo |
| `ndcToDc(Ponto)` | NDC → Dispositivo (pixel) |
| `inpToNdc(Pixel)` | Entrada do dispositivo (clique) → NDC |

### Fórmulas

`ndh` e `ndv` são a resolução real do monitor (lida em `Dispositivo` com `GraphicsDevice.getDisplayMode()`).

**Cenário I — NDC em [0,1] × [0,1]** (`TransformacaoNdc01`)

| Etapa | Fórmula (para x; y é análogo com ymin/ymax/ndv) |
|---|---|
| Mundo → NDC | `ndcx = (x − xmin) / (xmax − xmin)` |
| NDC → Mundo | `x = xmin + ndcx · (xmax − xmin)` |
| NDC → DC | `dcx = round(ndcx · (ndh − 1))` |
| DC → NDC | `ndcx = dcx / (ndh − 1)` |

**Cenário II — NDC em [−1,1] × [−1,1]** (`TransformacaoNdc11`)

| Etapa | Fórmula |
|---|---|
| Mundo → NDC | `ndcx = 2 · (x − xmin) / (xmax − xmin) − 1` |
| NDC → Mundo | `x = xmin + (ndcx + 1) / 2 · (xmax − xmin)` |
| NDC → DC | `dcx = round((ndcx + 1) / 2 · (ndh − 1))` |
| DC → NDC | `ndcx = 2 · dcx / (ndh − 1) − 1` |

> No dispositivo a origem `(0, 0)` fica no **canto superior esquerdo**, igual ao Java; por isso o pixel é gravado direto no frame buffer, sem inverter o eixo y.

### Passo a passo da execução (`app.Main`)

1. **Escala do sistema desligada** — `sun.java2d.uiScale = 1`, para que 1 pixel do Java seja 1 pixel real mesmo com o Windows em 125%/150%.
2. **Janela do mundo** — o programa pede `xmin`, `xmax`, `ymin`, `ymax`. Se `xmax ≤ xmin` ou `ymax ≤ ymin`, pede de novo.
3. **Ponto no mundo** — pede `x` e `y`. O ponto precisa estar **dentro** da janela. Aceita vírgula ou ponto decimal (`2,5` ou `2.5`).
4. **Cenário** — `1` para NDC [0,1] ou `2` para NDC [−1,1].
5. **Transformações** — calcula NDC e pixel e mostra no console:
   ```
   === Resultado (NDC [0,1] x [0,1]) ===
   Resolução:   1920 x 1080
   Mundo:       (5.0, 5.0)
   NDC:         (0.5, 0.5)
   Dispositivo: (960, 540)
   ```
6. **Display** — acende **um único pixel verde** (`0x00FF00`) num frame buffer preto do tamanho da tela e abre uma janela em **tela cheia**.
7. **Caminho inverso** — cada **clique** na tela imprime no console o pixel clicado, o NDC e a coordenada no mundo.
8. **ESC** fecha o programa.

#### Exemplo de entrada

```
=== Janela do mundo ===
xmin: 0
xmax: 10
ymin: 0
ymax: 10

=== Ponto no mundo ===
x: 5
y: 5

=== Cenário ===
1 - NDC [0,1] x [0,1]
2 - NDC [-1,1] x [-1,1]
Escolha: 1
```

> Dica: o pixel aceso é um só, então procure com atenção um ponto verde bem pequeno na tela preta.

---

## Lab 1 – Extra: Tabela de cores

### Objetivo

Implementar uma **tabela de cores (Look-Up Table)** com **24 bits de profundidade** para um sistema cuja **profundidade de pixel é de 12 bits**.

- Pixel de 12 bits → cada pixel guarda um **índice** de `0x000` a `0xFFF`.
- A tabela tem **2¹² = 4096 posições**.
- Cada posição guarda uma cor de **24 bits** (`#RRGGBB`, 8 bits por canal) → 2²⁴ = 16.777.216 cores possíveis, mas só **4096 ao mesmo tempo**.

```
 Frame buffer (12 bits/pixel)      Tabela de cores (4096 x 24 bits)       Tela
 ┌──────────────────────┐          ┌────────┬───────────┐
 │ pixel (x,y) = 0x180  │ ───────► │ 0x180  │  #118800  │ ───────►  pixel verde-escuro
 └──────────────────────┘  índice  └────────┴───────────┘   cor
                         (ControladorVideo faz essa consulta)
```

### Como funciona

1. **`FrameBufferIndexado`** — matriz `ndv × ndh` de índices. `desenharTodosOsIndices()` divide a imagem em uma grade **64 × 64 = 4096 blocos** e coloca um índice diferente em cada bloco (`índice = linha · 64 + coluna`).
2. **`TabelaCores`** — vetor de 4096 cores `0xRRGGBB`. Tem três preenchimentos:
   | Tabela | Regra |
   |---|---|
   | **Colorida (0xRGB)** | Os 3 dígitos hex do índice viram R, G e B; cada dígito é repetido (`0x8 → 0x88`, isto é, `× 0x11`). Ex.: `0x180 → #118800`. |
   | **Tons de cinza** | 4096 tons do preto ao branco: `cinza = i · 255 / 4095`. |
   | **Fogo** | Degradê preto → vermelho → amarelo → branco. |
3. **`ControladorVideo`** — faz o papel do hardware: para cada pixel lê o índice, consulta a tabela e grava a cor numa `BufferedImage`.
4. **Troca de tabela** — ao trocar a tabela, **o frame buffer não muda**; só as cores mudam. Esse é o ponto principal do exercício.

### Memória (imagem 512 × 512)

O `MainTabelaCores` imprime no console a comparação:

| Item | Cálculo | Bytes |
|---|---|---|
| Frame buffer com 12 bits/pixel | 512 · 512 · 12 / 8 | 393.216 |
| Tabela de cores | 4096 · 24 / 8 | 12.288 |
| **Total com tabela** | | **405.504** |
| Sem tabela (24 bits/pixel) | 512 · 512 · 24 / 8 | 786.432 |

### Passo a passo da execução (`tabelacores.MainTabelaCores`)

1. O console mostra as informações da tabela e o cálculo de memória acima.
2. Abre a janela **"Tabela de Cores"** com:
    - à esquerda, a **paleta 64 × 64** com as 4096 cores (blocos de 8 × 8 px);
    - à direita, o **inspetor** da cor selecionada.
3. Para escolher uma cor:
    - **clique** em um bloco da paleta, **ou**
    - digite um índice no campo — em decimal (`384`) ou hexadecimal (`0x180`) — e aperte **Enter** ou **Ver cor**. Fora de `0…4095` aparece uma mensagem de erro.
4. O inspetor mostra: amostra da cor, `#RRGGBB`, índice em hex/decimal, os **12 bits** do índice (`0001 1000 0000`), os canais R, G e B e o nome da tabela.
5. Os botões **Colorida**, **Cinza** e **Fogo** trocam a tabela; a paleta é redesenhada e o inspetor atualiza a cor do mesmo índice.

---

## Lab 3 – Transformações geométricas 2D

### Objetivo

Implementar **todas as transformações geométricas da aula G5** e aplicá-las em um **polígono (regular ou irregular) que começa na origem** do sistema cartesiano. Cada lado do polígono é desenhado pixel a pixel com o **algoritmo do ponto médio** (Bresenham) da aula G3.

### Como rodar

```bash
cd "Lab 3"
mvn compile
java -cp target/classes app.Main
```

No IntelliJ: abra a pasta **`Lab 3`**, escolha o SDK 24+ e rode `app.Main`.

### Estrutura

```
Lab 3/
├── pom.xml
└── src/main/java/
    ├── app/Main.java                      # ponto de entrada
    ├── modelo/
    │   ├── Ponto.java                     # ponto (x, y) do mundo
    │   └── Poligono.java                  # regular (n lados) ou irregular (bandeira), centro na origem
    ├── transformacoes/
    │   ├── Matriz.java                    # matriz 3x3 homogênea: multiplicar e aplicar P' = M . P
    │   ├── Transformacoes2D.java          # TODAS as matrizes da aula G5
    │   └── Operacao.java                  # nome + matriz + passo a passo (para o histórico)
    ├── rasterizacao/
    │   ├── FrameBuffer.java               # imagem onde só se acende pixel
    │   └── RetaPontoMedio.java            # algoritmo do ponto médio (G3), nos 8 octantes
    └── frontend/
        ├── TelaLab3.java                  # janela (layout do .jar de referência, visual do Lab 1)
        ├── PainelMundo.java               # plano cartesiano -250..250, origem no centro
        └── PainelViewport.java            # porta de visão 300 x 300
```

### Matrizes (coordenadas homogêneas, `P' = M · P` com `P = (x, y, 1)`)

| Transformação | Matriz | Slide G5 |
|---|---|---|
| Translação `T(tx, ty)` | `[1 0 tx; 0 1 ty; 0 0 1]` | 6, 10, 17 |
| Escala `S(sx, sy)` | `[sx 0 0; 0 sy 0; 0 0 1]` | 7, 10, 17 |
| Rotação `R(θ)` | `[cosθ −senθ 0; senθ cosθ 0; 0 0 1]` | 8, 9, 11, 17 |
| Reflexão no eixo x | `[1 0 0; 0 −1 0; 0 0 1]` | 18 |
| Reflexão no eixo y | `[−1 0 0; 0 1 0; 0 0 1]` | 18 |
| Reflexão na origem | `[−1 0 0; 0 −1 0; 0 0 1]` | 19 |
| Reflexão na reta y = x | `[0 1 0; 1 0 0; 0 0 1]` | 19 (o "?") |
| Cisalhamento em x | `[1 a 0; 0 1 0; 0 0 1]` | 20 |
| Cisalhamento em y | `[1 0 0; b 1 0; 0 0 1]` | 20 |
| Composta | `M = Mn · … · M2 · M1` | 21 |
| Escala com ponto fixo | `C = T(px, py) · S · T(−px, −py)` | 22–25 |
| Rotação com ponto fixo | `C = T(x, y) · R(θ) · T(−x, −y)` | 26–27 |
| Janela → Viewport | `M = T(umin, vmin) · S((umax−umin)/(xmax−xmin), (vmax−vmin)/(ymax−ymin)) · T(−xmin, −ymin)` | 29–33 |

> Exemplo do slide 25 conferido: escala 2 com ponto fixo (2,1) dá `C = [2 0 −2; 0 1 0; 0 0 1]`, e (4,1) vai para (6,1).

### Algoritmo do ponto médio (G3)

Mesmas fórmulas do slide 26: `d = 2dy − dx`, `incE = 2dy`, `incNE = 2(dy − dx)`; se `d ≤ 0` escolhe **E**, senão **NE**. Para os lados do polígono poderem ir em qualquer direção, o passo em x/y pode ser +1 ou −1 e, quando `|dy| > |dx|`, x e y trocam de papel. A reta de (20,10) a (30,18) gera exatamente os pixels da tabela do slide 28.

### Usando a tela

1. **Objeto 2D** — escolha *Regular* (nº de lados + raio) ou *Irregular (bandeira)* e clique em **Gerar Objeto**. O polígono nasce com centro em (0, 0).
2. **Abas** — *Trans*, *Rot* (ângulo + ponto fixo), *Escala* (sx, sy + ponto fixo), *Cisal* (a em x, b em y) e *Reflex* (eixo x, eixo y, origem, y = x). Ponto fixo (0, 0) = transformação básica em torno da origem.
3. **Sequência** — configure uma aba e clique em **Adicionar à Sequência** quantas vezes quiser; **Aplicar Sequência** multiplica tudo numa matriz só (`M = Mn…M1`) e aplica uma vez.
4. **Mapeamento 2D** — marque *Ativar Window → Viewport*, defina a janela (mundo) e a viewport (0 a 299) e clique em **Aplicar Limites**. A janela aparece em vermelho no mundo e o conteúdo dela é desenhado na viewport.
5. À direita ficam os **vértices**, o **centro geométrico** e as **dimensões**; embaixo, o **histórico** com todas as matrizes. **Limpar Tudo** volta o polígono para a origem.
6. Menu **Exibir**: mostrar/ocultar o polígono original (cinza) e a grade.

---

## Interface gráfica (frontend)

`frontend.Interface` (dentro de `Lab 1/`) junta os dois labs numa janela única, com visual inspirado no Windows clássico.

1. **Tela inicial** — título *CoordinateX*, professor, equipe e dois botões:
    - **Lab 1 - Coordenadas**
    - **Lab 1 Extra - Tabela de Cores**
2. **Tela do Lab 1**
    - Preencha `xmin`, `xmax`, `ymin`, `ymax`, `ponto x` e `ponto y`.
    - Escolha o cenário na barra lateral: **NDC [0,1]** ou **NDC [-1,1]**.
    - Clique em **Transformar**: o frame buffer (do tamanho do monitor) é desenhado no canvas com o pixel verde, e a barra de status mostra `Mundo → NDC → Dispositivo`. Use as barras de rolagem para chegar ao pixel.
    - **Limpar** volta o canvas ao fundo quadriculado.
    - Menu **Ver Histórico** lista todas as transformações feitas na sessão.
3. **Tela do Lab Extra** — mesma lógica do `MainTabelaCores` (paleta 64 × 64 com blocos de 6 × 6, campo de índice, botões Colorida/Cinza/Fogo e painel com os dados da cor), reaproveitando as classes do pacote `tabelacores`.
4. Pelo menu é possível **voltar ao início** e **sair**. A janela não tem borda do sistema: arraste pela barra de título e use os botões dela para minimizar, maximizar ou fechar.

---

## Problemas comuns

| Sintoma | Causa / solução |
|---|---|
| `error: release version 24 not supported` | O Maven está usando um JDK antigo. Aponte o `JAVA_HOME` para um JDK 24+. |
| `UnsupportedClassVersionError` ao rodar `java -cp …` | O `java` do PATH é mais antigo que o JDK que compilou. Use o `java` do JDK 24+. |
| `package tabelacores does not exist` ao compilar o `Lab 1` na IDE | Recarregue o projeto Maven (o `build-helper-maven-plugin` adiciona a pasta do Lab Extra como fonte), ou compile com `mvn compile`. |
| Não encontro o pixel verde | É um único pixel. Use valores fáceis (ex.: janela 0–10 e ponto 5,5 → centro da tela). |
| Tela cheia não fecha | Clique na janela para dar foco e aperte **ESC**. |

---

## Licença

Distribuído sob a licença descrita em [`LICENSE`](LICENSE).
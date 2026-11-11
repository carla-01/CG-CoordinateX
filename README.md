# CG-CoordinateX

Repositório dos laboratórios da disciplina de **Computação Gráfica**.

- **Professor:** Robson Pequeno de Sousa
- **Equipe:** Adrielly Carla · Maria Eduarda · Mirelle Casimiro · Nidlan Hendell · Rui Fernando

| Laboratório | Tema | Pasta |
|---|---|---|
| **Lab 1** | Transformação de coordenadas: Mundo → NDC → Dispositivo (e o caminho inverso) | [`Lab 1/`](Lab%201/) |
| **Lab 1 – Extra** | Tabela de cores (LUT) de 24 bits para pixels de 12 bits | [`Lab 1 - Extra/`](Lab%201%20-%20Extra/) |
| **Lab 2** | Rasterização de retas (DDA, Ponto Médio/Bresenham) e circunferências (polinomial, trigonométrico, ponto médio) | [`Lab 2/`](Lab%202/) |
| **Lab 3** | Transformações geométricas 2D (translação, escala, rotação, reflexão, cisalhamento e compostas) aplicadas a um polígono desenhado com o Ponto Médio | [`Lab 3/`](Lab%203/) |

Todos os labs são abertos por uma **interface gráfica única** (`frontend.App`), com uma tela inicial que tem um botão para cada lab.

---

## Sumário

1. [Pré-requisitos](#pré-requisitos)
2. [Arquitetura](#arquitetura)
3. [Como rodar](#como-rodar)
4. [Tela do usuário](#tela-do-usuário)
5. [Lab 1 – Transformação de coordenadas](#lab-1--transformação-de-coordenadas)
6. [Lab 1 – Extra: Tabela de cores](#lab-1--extra-tabela-de-cores)
7. [Lab 2 – Retas e circunferências](#lab-2--retas-e-circunferências)
8. [Lab 3 – Transformações geométricas](#lab-3--transformações-geométricas)
9. [Como adicionar um lab novo](#como-adicionar-um-lab-novo)
10. [Problemas comuns](#problemas-comuns)

---

## Pré-requisitos

| Ferramenta | Versão | Observação |
|---|---|---|
| **JDK** | **24 ou superior** | O `pom.xml` da raiz usa `maven.compiler.release = 24`. |
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

## Arquitetura

O projeto é um Maven **multi-módulo**. A regra principal: **os labs só têm os algoritmos; a interface fica toda no `frontend`.**

```
                 ┌──────────────────────────────┐
                 │           frontend           │  interface gráfica (Swing)
                 │  App, telas de cada lab,     │  único ponto de entrada
                 │  componentes compartilhados  │
                 └───┬──────────┬───────────┬────────────┬───┘
                     │          │           │            │
              ┌──────▼───┐ ┌────▼───────┐ ┌─▼───────┐ ┌──▼──────┐
              │  Lab 1   │ │Lab 1 Extra │ │  Lab 2  │ │  Lab 3  │   só algoritmos dos slides
              └──────┬───┘ └────────────┘ └──┬──────┘ └──┬──────┘   (sem Swing)
                     │                       │   ▲       │
                     │                       │   └───────┘  (o Lab 3 reaproveita a reta do Lab 2)
                 ┌───▼───────────────────────▼───────────▼┐
                 │                 core                   │  base comum: Ponto, Pixel,
                 └────────────────────────────────────────┘  JanelaMundo, FrameBuffer
```

```
CG-CoordinateX/
├── pom.xml                                 # pai: lista os módulos, Java 24, plugins
│
├── core/                                   # base comum a todos os labs
│   └── src/main/java/core/
│       ├── modelo/
│       │   ├── Ponto.java                  # ponto real (mundo e NDC)
│       │   ├── Pixel.java                  # ponto inteiro (dispositivo)
│       │   └── JanelaMundo.java            # janela do mundo (xmin, xmax, ymin, ymax)
│       └── dispositivo/
│           ├── Raster.java                 # interface: setPixel(x, y, cor)
│           ├── FrameBuffer.java            # memória de imagem ndh x ndv
│           ├── Monitor.java                # resolução real do monitor
│           └── Cores.java                  # VERDE, VERMELHO, ... (0xRRGGBB)
│
├── Lab 1/                                  # transformações de coordenadas
│   └── src/main/java/lab1/transformacao/
│       ├── Transformacao.java              # classe abstrata com os 4 procedimentos
│       ├── TransformacaoNdc01.java         # Cenário I:  NDC em [0,1] x [0,1]
│       └── TransformacaoNdc11.java         # Cenário II: NDC em [-1,1] x [-1,1]
│
├── Lab 1 - Extra/                          # tabela de cores
│   └── src/main/java/lab1extra/tabelacores/
│       ├── TabelaCores.java                # a LUT: 4096 posições x 24 bits
│       ├── FrameBufferIndexado.java        # frame buffer de índices de 12 bits
│       ├── ControladorVideo.java           # índice -> tabela -> cor na imagem
│       └── Hexa.java                       # formatação hex/binário e leitura do índice
│
├── Lab 2/                                  # rasterização de primitivas
│   └── src/main/java/lab2/
│       ├── reta/
│       │   ├── AlgoritmoReta.java          # interface comum das retas
│       │   ├── RetaDDA.java                # DDA (método do declive)
│       │   └── RetaPontoMedio.java         # Ponto Médio / Bresenham + simetria de octantes
│       └── circunferencia/
│           ├── AlgoritmoCircunferencia.java       # base + simetria de 8 pontos
│           ├── CircunferenciaPolinomial.java      # y = sqrt(r² - x²)
│           ├── CircunferenciaTrigonometrica.java  # x = r cos θ, y = r sin θ
│           └── CircunferenciaPontoMedio.java      # Ponto Médio / Bresenham
│
├── Lab 3/                                  # transformações geométricas 2D
│   ├── docs/Lab3_Estudo_Transformacoes.pdf # PDF de estudo: dedução de todas as fórmulas
│   └── src/main/java/lab3/
│       ├── modelo/
│       │   ├── Matriz3.java                # matriz 3x3 (coordenadas homogêneas)
│       │   ├── Vertice.java                # vértice com coordenadas reais
│       │   └── Poligono.java               # lista de vértices; regular, irregular, retângulo, centralizar
│       ├── transformacao/
│       │   ├── Transformacoes.java         # as matrizes da aula G5 (T, S, R, reflexões, cisalhamento, compostas)
│       │   └── SequenciaTransformacoes.java# lê "T(-2,-1) E(2,2) T(2,1)" e calcula a matriz composta
│       └── desenho/
│           └── DesenhoPoligono.java        # lados do polígono com o Ponto Médio (RetaPontoMedio, Lab 2)
│
└── frontend/                               # interface gráfica
    └── src/main/java/frontend/
        ├── App.java                        # main: registra os labs
        ├── JanelaPrincipal.java            # janela, tela inicial e troca de telas
        ├── TelaLab.java                    # base de todas as telas (layout padrão)
        ├── Navegacao.java                  # o que uma tela pode pedir à janela
        ├── componentes/
        │   ├── PainelRaster.java           # a "tela do usuário" (grade, zoom, mouse)
        │   ├── Regua.java                  # réguas com as coordenadas do dispositivo
        │   ├── TelaCheia.java              # mostra o frame buffer no monitor inteiro
        │   ├── BarraTitulo.java            # barra azul com minimizar/maximizar/fechar
        │   ├── BarraStatus.java            # mensagem + coordenadas do mouse + equipe
        │   ├── Componentes.java            # fábrica de botões, campos, combos
        │   └── Estilo.java                 # cores e fontes
        └── labs/
            ├── TelaLab1.java
            ├── TelaLab1Extra.java
            ├── TelaLab2.java
            └── TelaLab3.java
```

**Por que assim?**

- Os algoritmos ficam iguais aos dos slides, sem nenhum código de interface no meio, e podem ser testados sozinhos.
- Os algoritmos de desenho recebem um `Raster` (só tem `setPixel`). Eles não sabem se estão desenhando no frame buffer, na tela cheia ou numa lista de pixels.
- Toda tela de lab herda de `TelaLab` e usa os mesmos componentes, então todos os labs ficam com a mesma cara.
- Um lab pode usar o código de outro (ex.: o `frontend` usa as transformações do Lab 1) declarando a dependência no `pom.xml`.

---

## Como rodar

### Opção A — Terminal (Maven)

```bash
git clone <url-do-repositorio>
cd CG-CoordinateX

# compila todos os módulos e gera um .jar com tudo dentro
mvn package

# abre a interface
java -jar frontend/target/coordinatex.jar
```

> No Windows (PowerShell/CMD) os comandos são os mesmos.

**Testes automáticos** (JUnit 5): `mvn test`. Conferem o plano cartesiano (`core`) e, no Lab 2, se retas e circunferências com coordenadas **positivas e negativas** caem nos **octantes certos**. No Lab 3, conferem as matrizes (inclusive os exemplos dos slides e a tabela de comutatividade), o polígono centrado na origem, a leitura de sequências compostas e o desenho do contorno.

### Opção B — IntelliJ IDEA

1. `File → Open…` e selecione a **pasta raiz** `CG-CoordinateX` (o `pom.xml` da raiz). O IntelliJ importa todos os módulos.
2. Em `File → Project Structure → Project`, escolha um **SDK 24+**.
3. Abra `frontend/src/main/java/frontend/App.java` e clique no ▶ ao lado do `main`.

---

## Tela do usuário

### Layout padrão (todos os labs)

Toda tela de lab herda de `TelaLab` e segue o mesmo layout, inspirado na tela mostrada pelo professor:

```
┌──────────────────────────────────────────────────────┐
│ Arquivo  Ajuda                                       │
│ [barra de opções: zoom, eixos, limpar, tela cheia]   │
├──────────────────────────────────────────────────────┤
│                                                      │
│              área de desenho (PainelRaster)          │
│                                                      │
├───────────────────────────────────┬──────────────────┤
│ [Aba 1] [Aba 2] [Aba 3]           │ Log  [Limpar log]│
│ campos da operação escolhida      │ ...              │
├───────────────────────────────────┴──────────────────┤
│ mensagem                              │ (x, y) mouse │
└──────────────────────────────────────────────────────┘
```

Cada **operação** do lab vira uma **aba** (ex.: Lab 2 → Pixel | Reta | Circunferência). Os resultados e os pixels calculados vão para o **log**.

### PainelRaster

O `PainelRaster` é o componente que representa o **display do dispositivo** em todos os labs. Ele mostra um `FrameBuffer`:

| Recurso | Como funciona |
|---|---|
| **Coordenadas no mouse** | Passando o mouse, a barra de status mostra o pixel `(dcx, dcy)` embaixo dele, e as réguas destacam a linha e a coluna em amarelo. |
| **Réguas** | Em cima (`dcx`) e à esquerda (`dcy`), com os números do dispositivo. |
| **Zoom** | `1x` = cada pixel do frame buffer é **um pixel do monitor**. A partir de `4x` cada pixel vira uma **célula da grade**, onde dá para ver a rasterização. |
| **Clique** | Avisa a tela do lab qual pixel foi clicado (no Lab 2 liga/desliga pixels ou escolhe pontos). |
| **Tela cheia** | Mostra o frame buffer no monitor inteiro, sem zoom. **ESC** volta. |
| **Eixos** | Linhas azuis passando pelo pixel central `(ndh/2, ndv/2)`, só como referência visual: são desenhadas por cima e **não acendem pixels** no frame buffer. |

> A origem `(0, 0)` fica no **canto superior esquerdo** e o y cresce **para baixo**, igual ao Java: `0 ≤ dcx ≤ ndh − 1` e `0 ≤ dcy ≤ ndv − 1`.
>
> O `App` desliga a escala do Windows (`sun.java2d.uiScale = 1`) para que, no zoom 1x, 1 pixel do Java seja 1 pixel real mesmo com o Windows em 125%/150%.

---

## Lab 1 – Transformação de coordenadas

### Objetivo

Dado um ponto no **sistema de coordenadas do mundo** (definido pelo usuário), calcular a sua posição no **NDC** (coordenadas normalizadas do dispositivo) e depois no **dispositivo** (pixel do monitor), acender esse pixel em **verde** e mostrar o **caminho inverso** com o mouse.

```
        SAÍDA:    Mundo --userToNdc--> NDC --ndcToDc--> Dispositivo
        ENTRADA:  Mundo <--ndcToUser-- NDC <--inpToNdc-- Dispositivo (mouse)
```

### Os quatro procedimentos

Estão declarados na classe abstrata `lab1.transformacao.Transformacao` e implementados em cada cenário:

| Método | Faz |
|---|---|
| `userToNdc(Ponto)` | Mundo → NDC |
| `ndcToUser(Ponto)` | NDC → Mundo |
| `ndcToDc(Ponto)` | NDC → Dispositivo (pixel) |
| `inpToNdc(Pixel)` | Entrada do dispositivo (mouse) → NDC |

### Fórmulas

`ndh` e `ndv` são a resolução real do monitor (lida em `core.dispositivo.Monitor`).

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

### Passo a passo na interface

1. Preencha `xmin`, `xmax`, `ymin`, `ymax`, `ponto x` e `ponto y` (aceita `2,5` ou `2.5`).
2. Na aba **Mundo → Dispositivo**, escolha o cenário: **NDC [0,1] x [0,1]** ou **NDC [-1,1] x [-1,1]**.
3. Clique em **Transformar**: um frame buffer preto do tamanho do monitor recebe **um único pixel verde** (`0x00FF00`), e a tela rola até ele. A barra de status mostra `Mundo → NDC → Dispositivo`.
4. **Passe o mouse** na tela: a barra de status mostra o caminho inverso `Dispositivo → NDC → Mundo` do pixel embaixo do mouse.
5. **Clique** num pixel: a coordenada dele no mundo vira o novo ponto e é transformada de novo (ida e volta acende o mesmo pixel).
6. **Tela cheia** mostra o frame buffer no monitor inteiro (ESC volta). **Limpar** apaga tudo. O **log** guarda todas as transformações da sessão, e a aba **Dispositivo → Mundo** mostra ao vivo o caminho inverso do pixel sob o mouse.

> O zoom padrão é **1x**: o pixel aceso é um pixel real do monitor (item 4 do enunciado). Os zooms maiores só ajudam a achar o pixel.

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

| Item | Cálculo | Bytes |
|---|---|---|
| Frame buffer com 12 bits/pixel | 512 · 512 · 12 / 8 | 393.216 |
| Tabela de cores | 4096 · 24 / 8 | 12.288 |
| **Total com tabela** | | **405.504** |
| Sem tabela (24 bits/pixel) | 512 · 512 · 24 / 8 | 786.432 |

### Passo a passo na interface

1. À esquerda, a **paleta 64 × 64** com as 4096 cores; à direita, os dados da cor selecionada.
2. Para escolher uma cor: **clique** em um bloco da paleta, **ou** digite um índice — em decimal (`384`) ou hexadecimal (`0x180`) — e aperte **Enter** ou **Ver cor**.
3. Os dados mostram: amostra da cor, `#RRGGBB`, índice em hex/decimal, os **12 bits** do índice (`0001 1000 0000`), os canais R, G e B e o nome da tabela.
4. Os botões **Colorida**, **Cinza** e **Fogo** trocam a tabela; a paleta é redesenhada com o mesmo frame buffer.

---

## Lab 2 – Retas e circunferências

### Objetivo

Implementar os algoritmos de desenho de reta da aula 3 (**DDA** e **Ponto Médio/Bresenham**, com os demais octantes por **simetria**) e os algoritmos de desenho da circunferência (**polinomial**, **trigonométrico** e **ponto médio**).

Todos trabalham com coordenadas inteiras e desenham chamando `raster.setPixel(x, y, cor)`. A tela pode usar dois sistemas (caixa **Plano cartesiano** na barra de opções):

| Sistema | Origem | y cresce | Coordenadas |
|---|---|---|---|
| **Plano cartesiano** (padrão) | centro da tela `(ndh/2, ndv/2)` | para **cima** | positivas e **negativas** (64 × 48: `−32 ≤ x ≤ 31`, `−23 ≤ y ≤ 24`) |
| **Dispositivo** | canto superior esquerdo | para **baixo** | `0 ≤ x ≤ ndh − 1`, `0 ≤ y ≤ ndv − 1` |

No plano cartesiano o `core.dispositivo.PlanoCartesiano` converte cada `setPixel(x, y)` para o pixel `(ndh/2 + x, ndv/2 − y)` do dispositivo, e a tela mostra por cima (sem acender pixels) os **eixos x e y**, as diagonais tracejadas `y = x` e `y = −x` e o **número de cada octante**.

### Retas

| Algoritmo | Classe | Ideia |
|---|---|---|
| **DDA** | `RetaDDA` | `steps = max(|dx|, |dy|)`; a cada passo `x += dx/steps`, `y += dy/steps` e `setPixel(round(x), round(y))`. Funciona em qualquer direção. |
| **Ponto Médio (Bresenham)** | `RetaPontoMedio` | Só inteiros. `d = 2dy − dx`; se `d ≤ 0` escolhe **E** (`d += 2dy`), senão **NE** (`d += 2(dy − dx)`). |

**Simetria de octantes (Bresenham).** O algoritmo do slide só vale no 1º octante (`0 ≤ m ≤ 1`, x crescendo). Para os outros:

1. `descobrirOctante(dx, dy)` diz em qual dos 8 octantes a reta está;
2. `paraPrimeiroOctante` leva P1 e P2 para o 1º octante (reflexões no eixo x, no eixo y e na reta y = x);
3. o algoritmo roda no 1º octante;
4. `doPrimeiroOctante` leva cada pixel calculado de volta ao octante original antes do `setPixel`.

| Octante | Condição | Para o 1º octante |
|---|---|---|
| 1 | `dx ≥ dy ≥ 0` | `(x, y)` |
| 2 | `dy > dx ≥ 0` | `(y, x)` |
| 3 | `dy > −dx > 0` | `(y, −x)` |
| 4 | `−dx ≥ dy ≥ 0` | `(−x, y)` |
| 5 | `dx ≤ dy < 0` | `(−x, −y)` |
| 6 | `dy < dx < 0` | `(−y, −x)` |
| 7 | `dy < −dx ≤ 0` | `(−y, x)` |
| 8 | `dx ≥ −dy > 0` | `(x, −y)` |

> No plano cartesiano o 1º octante aparece "para a direita e para cima", como na matemática. Com o plano desligado (dispositivo, y para baixo) ele aparece "para a direita e para baixo".

**Conferindo com o slide** (com o plano cartesiano **desligado**): a reta de `(20,10)` a `(30,18)` com o Ponto Médio acende exatamente `(20,10) (21,11) (22,12) (23,12) (24,13) (25,14) (26,15) (27,16) (28,16) (29,17) (30,18)`, igual à tabela da aula 3. O DDA de `(6,9)` a `(11,12)` acende `(6,9) (7,10) (8,10) (9,11) (10,11) (11,12)`.

### Circunferências

Todos calculam só 1/8 da circunferência (de 90° a 45°) e usam a **simetria de 8 pontos** (`pontosCirculo`): `(x, y)` gera `(±x, ±y)` e `(±y, ±x)` em volta do centro.

| Algoritmo | Classe | Ideia |
|---|---|---|
| **Polinomial** | `CircunferenciaPolinomial` | Igual ao slide: `x = 0`, `xend = r/√2`; enquanto `x <= xend`: `y = sqrt(r² − x²)`, `Círculo_ponto(round x, round y)`, `x += 1`. |
| **Trigonométrico** | `CircunferenciaTrigonometrica` | Igual ao slide: `θ = 0`, `θend = π/4`; enquanto `θ <= θend`: `x = r cos θ`, `y = r sen θ`, `Círculo_ponto(round x, round y)`, `θ += i`. Como no slide ("i = define o passo"), o passo `i` é escolhido na tela, em graus (padrão **5°**). Passo maior = menos pontos calculados, mas a circunferência pode ficar com buracos. Se `i` não divide 45°, o ponto de 45° é desenhado no fim. Com **0** o passo é automático: divide `π/4` em partes iguais de no máximo `1/r` (≈ 1 pixel), sem buracos. |
| **Ponto Médio** | `CircunferenciaPontoMedio` | Começa em `(0, r)`, `d = 1 − r`; se `d < 0` escolhe **E** (`d += 2x + 3`), senão **SE** (`d += 2(x − y) + 5`, `y−−`). |

### Passo a passo na interface

1. Defina o dispositivo: `ndh` × `ndv` (padrão 64 × 48) e clique em **Aplicar**. Escolha o **zoom** (padrão 8x).
2. Escolha a ferramenta pela aba. A tela imita o display: fundo preto sem grade e os pixels são acesos sempre na cor **`00FF00`**. Com o **Plano cartesiano** ligado aparecem os eixos e os octantes (desligue para ver o display totalmente limpo, em coordenadas do dispositivo):
   - **Pixel** — clique numa célula para **ligar/desligar** um pixel.
   - **Reta** — escolha o algoritmo; clique em **P1** e depois em **P2**, ou digite `x1 y1 x2 y2` e clique em **Desenhar**. Enquanto escolhe o P2, a barra de status mostra `dx`, `dy`, o declive `m` e o **octante**. O botão **Testar os 8 octantes** desenha, a partir da origem, uma reta em cada octante (x e y positivos e negativos) e escreve no log se cada uma caiu no octante certo (`OK`/`ERRO`).
   - **Circunferência** — escolha o algoritmo (no **Trigonométrico**, o campo **passo θ** define o `i`, em graus; 0 = automático); clique no **centro** e depois num ponto da borda (define o raio), ou digite `xc yc raio` e clique em **Desenhar**.
3. O **log** mostra cada `setPixel` de cada desenho, na ordem em que o algoritmo chamou. Serve para conferir com as tabelas dos slides.
4. **Limpar** apaga a tela; **Tela cheia** mostra o frame buffer no monitor sem zoom (ESC volta).

---

## Lab 3 – Transformações geométricas

### Objetivo

Implementar **todas as transformações geométricas 2D da aula G5** e aplicá-las a um **polígono**
(regular ou irregular) que começa **na origem do plano cartesiano**. Os lados do polígono são
desenhados com o **algoritmo do Ponto Médio** da aula G3.

O Lab 3 é **independente dos outros**: na tela inicial ele tem o **próprio botão** e abre a **própria
tela** (`TelaLab3`), como o Lab 1 e o Lab 2. Só reaproveita código por dependência no `pom.xml`: do
`core` (`Raster`, `Pixel`) e do Lab 2 (`RetaPontoMedio`, que desenha cada lado do polígono).

> O PDF [`Lab 3/docs/Lab3_Estudo_Transformacoes.pdf`](Lab%203/docs/Lab3_Estudo_Transformacoes.pdf) tem a
> dedução de cada fórmula usada aqui, exemplos numéricos e exercícios.

### Convenções

- O ponto é um vetor **coluna** em coordenadas homogêneas, `P = (x, y, 1)ᵀ`, e a transformação é `P' = M · P`.
- Coordenadas do plano cartesiano: origem **sempre no centro** da tela, **y para cima** (as transformações são em
  torno da origem). A caixa **Plano cartesiano** só mostra ou esconde os eixos e octantes. Ângulo positivo = **anti-horário**.
- Os vértices são **números reais**. Só na hora de desenhar eles são **arredondados** (para longe do zero),
  então o erro de arredondamento **não se acumula** entre as transformações.
- A transformação mais recente multiplica **pela esquerda**: `M_acumulada = M_nova · M_acumulada`.
  O polígono mostrado é sempre `M_acumulada · P_original`.

### As matrizes (`lab3.transformacao.Transformacoes`)

| Transformação | Método | Matriz 3 × 3 (linhas separadas por `;`) | Slide |
|---|---|---|---|
| Translação | `translacao(tx, ty)` | `[1 0 tx; 0 1 ty; 0 0 1]` | 6 |
| Escala | `escala(sx, sy)` | `[sx 0 0; 0 sy 0; 0 0 1]` | 7 |
| Rotação (origem) | `rotacao(θ)` | `[cos θ  −sen θ  0; sen θ  cos θ  0; 0 0 1]` | 8–11 |
| Reflexão em x | `reflexaoEixoX()` | `[1 0 0; 0 −1 0; 0 0 1]` | 18 |
| Reflexão em y | `reflexaoEixoY()` | `[−1 0 0; 0 1 0; 0 0 1]` | 18 |
| Reflexão na origem | `reflexaoOrigem()` | `[−1 0 0; 0 −1 0; 0 0 1]` | 19 |
| Reflexão em y = x | `reflexaoRetaYigualX()` | `[0 1 0; 1 0 0; 0 0 1]` | 19 (a "?") |
| Reflexão em y = −x | `reflexaoRetaYigualMenosX()` | `[0 −1 0; −1 0 0; 0 0 1]` | 19 (a "?") |
| Reflexão numa reta pela origem | `reflexaoEmReta(θ)` | `[cos 2θ  sen 2θ  0; sen 2θ  −cos 2θ  0; 0 0 1]` | extra |
| Cisalhamento em x | `cisalhamentoX(a)` | `[1 a 0; 0 1 0; 0 0 1]` | 20 |
| Cisalhamento em y | `cisalhamentoY(b)` | `[1 0 0; b 1 0; 0 0 1]` | 20 |
| Escala com ponto fixo | `escalaComPontoFixo(sx, sy, px, py)` | `T(p) · S · T(−p)` | 22–25 |
| Rotação com ponto fixo | `rotacaoComPontoFixo(θ, px, py)` | `T(p) · R(θ) · T(−p)` | 26–27 |
| Composição qualquer | `compor(M1, M2, ...)` | `Mn · ... · M2 · M1` | 21 |
| Janela → porta de visão | `janelaViewport(...)` | `T(umin,vmin) · S · T(−xmin,−ymin)` | 29–33 |

A comutatividade da composição (slide 28) é conferida pelo botão **Testar comutatividade**
e pelos testes automáticos: Translação·Translação, Escala·Escala, Rotação·Rotação e Escala(sx = sy)·Rotação
comutam; Translação·Rotação, Translação·Escala e Escala(sx ≠ sy)·Rotação **não** comutam.

### Como o polígono é desenhado

1. `Poligono` guarda os vértices reais (regular: vértice `k` em `90° + k·360°/n`; irregular: pentágono pronto ou
   vértices digitados). Ele sempre é **centrado na origem** (a média dos vértices vai para `(0, 0)`).
2. Cada transformação multiplica **só os vértices** (`Poligono.transformar`). Como as transformações afins levam
   retas em retas, transformar os vértices e ligá-los é o mesmo que transformar o polígono inteiro.
3. `DesenhoPoligono` arredonda os vértices e liga um ao seguinte (e o último ao primeiro) com `RetaPontoMedio`
   (Ponto Médio, com simetria dos 8 octantes, do Lab 2).

### Passo a passo na interface

1. Na tela inicial, clique em **Lab 3 – Transformações Geométricas**. A tela é igual à do Lab 2: display **preto**,
   sem grade, 64 × 48 (**ndh/ndv + Aplicar** mudam a resolução), e o **Plano cartesiano** só aparece se a caixa for
   marcada. **Limpar** apaga tudo (inclusive o polígono).
2. Aba **Pixel** (continuação do Lab 2), escolhendo a *Ferramenta*:
   - **Pixel (liga/desliga)** — clique numa célula para ligar/desligar um pixel, como no Lab 2;
   - **Polígono** — clique em cada vértice; o próximo lado **acompanha o mouse**. Feche clicando no vértice 1
     (marcado) ou em **Fechar polígono**;
   - **Retângulo** — clique num canto, puxe e clique no canto oposto;
   - **Polígono regular** — clique no centro, puxe até a borda (define o raio e o vértice 1) e clique de novo
     (`lados` no campo ao lado).

   O desenho feito aqui vira o polígono das transformações e **fica onde foi desenhado**. **Cancelar** desiste do
   desenho em andamento.
3. Aba **Polígono**: escolha *Regular (n lados)* (`lados`, `raio`), *Irregular (pronto)* ou *Irregular (digitar
   vértices)* (`x,y; x,y; ...`, com ponto decimal) e clique em **Criar polígono**. O padrão é o pentágono
   *irregular* `(-8,-7) (6,-8) (9,2) (1,9) (-8,4)`, de propósito sem simetria, para a rotação e a reflexão aparecerem.
4. Use as abas **Translação**, **Escala**, **Rotação** (as duas com *ponto fixo* opcional), **Reflexão** (eixo x,
   eixo y, origem, y = x, y = −x ou reta pela origem com ângulo θ) e **Cisalhamento** (em x ou em y). Cada
   **Aplicar** transforma o polígono **atual** — as transformações se acumulam.
5. Aba **Janela** (janela do usuário → viewport, slides 29–33): digite a **janela do usuário** no mundo
   (`xmin`, `xmax`, `ymin`, `ymax`) e a **viewport** (`umin`, `umax`, `vmin`, `vmax`) e clique em **Aplicar**: o que está
   dentro da janela é levado para a viewport pela matriz `T(umin,vmin) · S · T(−xmin,−ymin)`. A janela aparece em
   **amarelo** e a viewport em **ciano** (caixa *Mostrar janela e viewport*). Com os valores padrão, a janela
   [−10, 10] × [−10, 10] vai para [4, 28] × [2, 20]: a escala em x (1,2) é diferente da escala em y (0,9), então
   o polígono fica deformado (esticado em x, achatado em y). Com janela e viewport de mesma proporção não deforma.
6. Aba **Composta**: escreva uma sequência aplicada da esquerda para a direita, por exemplo
   `T(-2,-1) E(2,2) T(2,1)` (escala por 2 com ponto fixo (2,1)). Há exemplos prontos na caixa *Exemplo*.
   Comandos: `T(tx,ty)` `E(sx,sy)` `R(graus)` `EP(sx,sy,px,py)` `RP(graus,px,py)` `CisX(a)` `CisY(b)`
   `RefX` `RefY` `RefO` `RefYX` `RefYMX` `RefR(graus)` `JV(xmin,xmax,ymin,ymax,umin,umax,vmin,vmax)`.
7. Na tela fica **só o polígono transformado, em verde**: a forma antiga é apagada a cada transformação. O contorno
   magenta marca o **vértice 1** (mostra o giro e o espelhamento).
8. **Desfazer** volta uma transformação; **Voltar ao original** zera tudo; **Tela cheia** mostra o frame buffer
   (ESC volta).
9. O **log** mostra, a cada transformação, a matriz `M`, a matriz acumulada e os vértices (reais e o pixel de cada um).

---

## Como adicionar um lab novo

Exemplo para o **Lab 4**:

1. **Algoritmos** — crie a pasta `Lab 4/` com `src/main/java/lab4/...` e um `pom.xml` igual ao do `Lab 2` (troque o `artifactId` para `lab4`). Use só o `core` (`Raster`, `FrameBuffer`, `Pixel`...), sem Swing.
2. **Módulo** — no `pom.xml` da raiz, adicione `<module>Lab 4</module>` (antes do `frontend`).
3. **Dependência** — no `frontend/pom.xml`, adicione a dependência `lab4`.
4. **Tela** — crie `frontend/labs/TelaLab4.java` estendendo `TelaLab`: monte a barra de opções (`Componentes.linhaEntrada`), a área de desenho (normalmente um `PainelRaster`) e as abas (`criarAbas()`, uma aba por operação, com o conteúdo feito por `Componentes.painelAba`), e chame `montar(...)` no fim do construtor. Use `log(...)` para escrever no log.
5. **Registro** — no `frontend/App.java`, adicione `janela.adicionarLab(new TelaLab4(janela));`. O botão aparece sozinho na tela inicial.

---

## Problemas comuns

| Sintoma | Causa / solução |
|---|---|
| `error: release version 24 not supported` | O Maven está usando um JDK antigo. Aponte o `JAVA_HOME` para um JDK 24+. |
| `UnsupportedClassVersionError` ao rodar `java -jar …` | O `java` do PATH é mais antigo que o JDK que compilou. Use o `java` do JDK 24+. |
| `package lab1.transformacao does not exist` (ou outro lab) na IDE | Abra a **pasta raiz** no IntelliJ (não a pasta de um lab) e recarregue o projeto Maven. |
| Não encontro o pixel verde (Lab 1) | A tela rola até ele depois do Transformar. Com zoom ≥ 4x ele ganha um contorno magenta. |
| Tela cheia não fecha | Clique na janela para dar foco e aperte **ESC**. |

---

## Licença

Distribuído sob a licença descrita em [`LICENSE`](LICENSE).

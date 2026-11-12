# CG-CoordinateX

Repositório dos laboratórios da disciplina de **Computação Gráfica**.

- **Professor:** Robson Pequeno de Sousa
- **Equipe:** Adrielly Carla · Maria Eduarda · Mirelle Casimiro · Nidlan Hendell · Rui Fernando

| Laboratório | Tema | Pasta |
|---|---|---|
| **Lab 1** | Transformação de coordenadas: Mundo → NDC → Dispositivo (e o caminho inverso) | [`Lab 1/`](Lab%201/) |
| **Lab 1 – Extra** | Tabela de cores (LUT) de 24 bits para pixels de 12 bits | [`Lab 1 - Extra/`](Lab%201%20-%20Extra/) |
| **Lab 2** | Rasterização de retas (DDA, Ponto Médio/Bresenham) e circunferências (polinomial, trigonométrico, ponto médio) | [`Lab 2/`](Lab%202/) |
| **Lab 3** | Transformações geométricas 2D (aula G5) em um polígono na origem, desenhado com o ponto médio do Lab 2 | [`Lab 3/`](Lab%203/) |

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
8. [Lab 3 – Transformações geométricas 2D](#lab-3--transformações-geométricas-2d)
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
                 └───┬──────────┬───────────┬───┘
                     │          │           │
              ┌──────▼──┐ ┌─────▼──────┐ ┌──▼──────┐ ┌─────────┐
              │  Lab 1  │ │Lab 1 Extra │ │  Lab 2  │◄┤  Lab 3  │  só algoritmos dos slides
              └──────┬──┘ └────────────┘ └──┬──────┘ └────┬────┘  (sem Swing)
                     │                      │             │
                 ┌───▼──────────────────────▼─────────────▼───┐
                 │             core             │  base comum: Ponto, Pixel,
                 └────────────────────────────────────────────┘  JanelaMundo, FrameBuffer
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
│   └── src/main/java/lab3/
│       ├── transformacoes/
│       │   ├── Matriz.java                 # matriz 3x3 homogênea: P' = M . P
│       │   ├── Transformacoes2D.java       # todas as matrizes da aula G5
│       │   └── Operacao.java               # nome + matriz + passo a passo (log)
│       └── poligono/
│           └── Poligono.java               # regular/irregular na origem; desenha com o RetaPontoMedio do Lab 2
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

**Testes automáticos** (JUnit 5): `mvn test`. Conferem o plano cartesiano (`core`), no Lab 2 se retas e circunferências com coordenadas **positivas e negativas** caem nos **octantes certos** e, no Lab 3, as matrizes com os exemplos dos slides da aula G5.

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
| **Trigonométrico** | `CircunferenciaTrigonometrica` | Igual ao slide: `θ = 0`, `θend = π/4`; enquanto `θ <= θend`: `x = r cos θ`, `y = r sen θ`, `Círculo_ponto(round x, round y)`, `θ += i`. O passo `i` divide `π/4` em partes iguais de no máximo `1/r` (≈ 1 pixel), para o último ponto cair em 45°. |
| **Ponto Médio** | `CircunferenciaPontoMedio` | Começa em `(0, r)`, `d = 1 − r`; se `d < 0` escolhe **E** (`d += 2x + 3`), senão **SE** (`d += 2(x − y) + 5`, `y−−`). |

### Passo a passo na interface

1. Defina o dispositivo: `ndh` × `ndv` (padrão 64 × 48) e clique em **Aplicar**. Escolha o **zoom** (padrão 8x).
2. Escolha a ferramenta pela aba. A tela imita o display: fundo preto sem grade e os pixels são acesos sempre na cor **`00FF00`**. Com o **Plano cartesiano** ligado aparecem os eixos e os octantes (desligue para ver o display totalmente limpo, em coordenadas do dispositivo):
   - **Pixel** — clique numa célula para **ligar/desligar** um pixel.
   - **Reta** — escolha o algoritmo; clique em **P1** e depois em **P2**, ou digite `x1 y1 x2 y2` e clique em **Desenhar**. Enquanto escolhe o P2, a barra de status mostra `dx`, `dy`, o declive `m` e o **octante**. O botão **Testar os 8 octantes** desenha, a partir da origem, uma reta em cada octante (x e y positivos e negativos) e escreve no log se cada uma caiu no octante certo (`OK`/`ERRO`).
   - **Circunferência** — escolha o algoritmo; clique no **centro** e depois num ponto da borda (define o raio), ou digite `xc yc raio` e clique em **Desenhar**.
3. O **log** mostra cada `setPixel` de cada desenho, na ordem em que o algoritmo chamou. Serve para conferir com as tabelas dos slides.
4. **Limpar** apaga a tela; **Tela cheia** mostra o frame buffer no monitor sem zoom (ESC volta).

---

## Lab 3 – Transformações geométricas 2D

### Objetivo

Implementar **todas as transformações geométricas da aula G5** e aplicá-las em um **polígono (regular ou irregular) que começa na origem** do sistema cartesiano. Cada lado do polígono é desenhado com o **algoritmo do ponto médio** da aula G3 — o mesmo `lab2.reta.RetaPontoMedio` do Lab 2 (por isso o `Lab 3` depende do `Lab 2`).

### Matrizes (coordenadas homogêneas, `P' = M · P` com `P = (x, y, 1)`)

Todas ficam em `lab3.transformacoes.Transformacoes2D`, escritas como nos slides:

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

> Conferido no teste `Transformacoes2DTest`: a escala por 2 com ponto fixo (2,1) do slide 25 dá `C = [2 0 −2; 0 1 0; 0 0 1]`, e (4,1) vai para (6,1).

### Passo a passo na interface

A tela segue o layout padrão (`TelaLab`) com os mesmos painéis da tela "2D" do programa de referência:

```
┌──────────────────────────────────────────────────────────────────┐
│ [Limpar Tudo] [x] Polígono original [x] Plano cartesiano  Zoom   │
├──────────────┬───────────────────────────────┬───────────────────┤
│ Objeto 2D    │                               │ Viewport          │
│ Sequência    │  Mundo (−200..200, origem     │                   │
│ Mapeamento 2D│  no centro)                   │ Informações       │
├──────────────┴───────────────────────────────┼───────────────────┤
│ [Trans] [Rot] [Escala] [Cisal] [Reflex]      │ Log (matrizes)    │
└──────────────────────────────────────────────┴───────────────────┘
```

1. **Objeto 2D** — escolha *Regular* (nº de lados + raio) ou *Irregular* (uma bandeira não simétrica, para as reflexões ficarem visíveis) e clique em **Gerar Objeto**. O polígono nasce com centro em **(0, 0)** e aparece em verde (`00FF00`).
2. **Abas** — *Trans*, *Rot* (ângulo + ponto fixo), *Escala* (sx, sy + ponto fixo), *Cisal* (a em x, b em y) e *Reflex* (eixo x, eixo y, origem, y = x). Com ponto fixo (0, 0) é a transformação básica, em torno da origem; com outro ponto, o log mostra `T(−p)`, a matriz básica, `T(p)` e a matriz final `C`.
3. **Sequência** — o botão **+ Sequência** de cada aba guarda a transformação; **Aplicar Sequência** multiplica tudo numa matriz só (`M = Mn…M1`) e aplica uma vez.
4. **Mapeamento 2D** — marque *Ativar Window -> Viewport*, defina a window (mundo) e a viewport (0 a 199) e clique em **Aplicar Limites**. A window aparece em vermelho no mundo e o que está dentro dela é desenhado na viewport (v cresce para cima).
5. À direita ficam os **vértices**, o **centro geométrico** e as **dimensões**; o **log** guarda todas as matrizes e os vértices resultantes. **Limpar Tudo** volta o polígono para a origem.
6. O polígono original fica em cinza (caixa **Polígono original**). Os eixos são só referência visual (não acendem pixels).

---

## Como adicionar um lab novo

Exemplo para o **Lab 4** (foi assim que o Lab 3 entrou):

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

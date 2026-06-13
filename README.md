# pesagem-graos

Sistema para receber, estabilizar e armazenar leituras de peso de balanças distribuídas nas filiais de uma empresa de transporte de grãos. Cada balança é composta por um ESP32 com câmera LPR que envia leituras automaticamente a cada 100ms enquanto há um caminhão sobre ela.

---

## Stack

| Tecnologia | Uso |
|---|---|
| Java 17 | Linguagem |
| Spring Boot 3.5 | Framework principal |
| Spring Data JPA | Persistência |
| Spring Validation | Validação de DTOs |
| PostgreSQL | Banco de dados |
| Flyway | Migrations |
| Docker | Ambiente local |

---

## Como executar

### Pré-requisitos

- Java 17+
- Docker
- Python 3 (só pro simulador)

### Passo a passo completo

Abra **dois terminais** (PowerShell, CMD ou terminal do IntelliJ ou Vscode).

**Terminal 1 — banco e aplicação:**

```powershell
cd pesagem-graos
docker compose up -d
.\mvnw.cmd spring-boot:run
```

Espere aparecer `Started PesagemGraosApplication` no log. As migrations do Flyway rodam sozinhas na subida, não precisa executar SQL manual.

**Terminal 2 — cadastros e simulação:**

```powershell
cd pesagem-graos
pip install requests
.\scripts\seed_demo.ps1
python scripts\simular_balanca.py
```

**Conferir se salvou:**

```powershell
Invoke-RestMethod http://localhost:8080/api/v1/pesagens
Invoke-RestMethod http://localhost:8080/api/v1/transacoes
```

Ou abra no navegador: `http://localhost:8080/api/v1/pesagens`

Esperado: pesagem com placa `ABC1D23` e transação com status `CONCLUIDA`.

### O que o `seed_demo.ps1` faz

Antes de simular a balança, o sistema precisa de cadastros no banco filial, balança com API key, caminhão com tara, tipo de grão e uma transação em andamento. Sem isso a leitura até entra na fila, mas na hora de salvar quebra porque não acha caminhão ou transação aberta.

Criei o script `scripts/seed_demo.ps1` pra não ficar montando curl toda vez que fui testar. Ele cadastra tudo na ordem certa com os mesmos dados que o simulador Python usa (`balanca-01`, placa `ABC1D23`, etc.).

Se der erro de duplicata (placa já cadastrada, transação já aberta), é porque o banco já tem dados de um teste anterior. Reseta assim:

```powershell
docker compose down -v
docker compose up -d
# espera uns 5 segundos e roda o seed de novo
.\scripts\seed_demo.ps1
```

### Simulando a balança (Python)

O script `scripts/simular_balanca.py` imita o ESP32, manda leituras a cada 100ms em três fases: oscilação alta (caminhão posicionando), oscilação menor e peso estável. Todas retornam `202 Accepted`.

Usei IA só pra gerar esse script Python. O backend inteiro eu fiz manualmente; o simulador é só porque não tinha o ESP32 físico pra demonstrar o fluxo e queria mostrar o projeto rodando na simulação.

---

## Arquitetura

O projeto usa arquitetura em camadas com processamento assíncrono interno.

```
Controller  →  recebe a requisição, valida, responde
Service     →  regras de negócio, estabilização, cálculos
Repository  →  Spring Data JPA + PostgreSQL
```

A escolha foi deliberada. Com 50 balanças enviando a cada 100ms o volume máximo é em torno de 500 requisições por segundo, dentro do que o Spring Boot gerencia tranquilamente sem precisar de Kafka ou qualquer infraestrutura extra.

O endpoint de recepção das balanças retorna `202 Accepted` imediatamente e delega o processamento para uma fila em memória. `202` em vez de `200` porque a ação não foi concluída no momento da resposta, o peso só é salvo depois que o scheduler detectar estabilização.

---

## Estrutura do projeto

```
src/main/java/com/pesagem_graos/
│
├── controller/
│   ├── BalancaController.java
│   ├── CaminhaoController.java
│   ├── FilialController.java
│   ├── TipoGraoController.java
│   ├── PesagemController.java
│   ├── TransacaoController.java
│   └── advice/
│       └── GlobalExceptionHandler.java
│
├── service/
│   ├── BalancaService.java
│   ├── PesagemService.java
│   ├── EstabilizacaoService.java
│   ├── PrecificacaoService.java
│   └── IdempotenciaService.java
│
├── repository/
│   ├── BalancaRepository.java
│   ├── CaminhaoRepository.java
│   ├── FilialRepository.java
│   ├── TipoGraoRepository.java
│   ├── PesagemRepository.java
│   ├── TransacaoRepository.java
│   └── IdempotenciaRepository.java
│
├── domain/
│   ├── Balanca.java
│   ├── Caminhao.java
│   ├── Filial.java
│   ├── TipoGrao.java
│   ├── Pesagem.java
│   ├── TransacaoTransporte.java
│   ├── StatusTransacao.java
│   └── LeituraRecebida.java
│
├── dto/
│   ├── LeituraDTO.java
│   ├── BalancaDTO.java
│   ├── CaminhaoDTO.java
│   ├── FilialDTO.java
│   ├── TipoGraoDTO.java
│   ├── TransacaoRequestDTO.java
│   └── ErroDTO.java
│
├── infra/
│   ├── FilaDePesagem.java
│   └── IdempotenciaUtil.java
│
├── scheduler/
│   └── EstabilizacaoScheduler.java
│
└── exception/
    ├── BalancaNaoAutorizadaException.java
    ├── CaminhaoNaoEncontradoException.java
    ├── PesagemNaoEstabilizadaException.java
    └── RegraDeNegocioException.java

src/main/resources/
├── application.properties
└── db/migration/
    ├── V1__cria_tabela_filial.sql
    ├── V2__cria_tabela_tipo_grao.sql
    ├── V3__cria_tabela_caminhao.sql
    ├── V4__cria_tabela_balanca.sql
    ├── V5__cria_tabela_pesagem.sql
    ├── V6__cria_tabela_transacao_transporte.sql
    ├── V7__cria_tabela_idempotencia.sql
    └── V8__corrige_tipo_coluna_tara_caminhao.sql
```

---

## Migrations

Uma migration por tabela — facilita rastrear erro e a ordem já documenta as dependências do modelo.

```
V1  filial                →  tabela base, sem dependências
V2  tipo_grao             →  tabela base, sem dependências
V3  caminhao              →  tabela base, sem dependências
V4  balanca               →  depende de filial
V5  pesagem               →  depende de balanca e tipo_grao
V6  transacao_transporte  →  depende de caminhao, tipo_grao, filial e pesagem
V7  leitura_recebida      →  controle de idempotência, sem dependências
V8  caminhao.tara         →  corrige FLOAT8 → DECIMAL(10,2) em bancos já criados
```

---

## Modelo de dados

```
Filial (1) ──── (N) Balanca
Balanca (1) ──── (N) Pesagem
Caminhao (1) ──── (N) TransacaoTransporte
TipoGrao (1) ──── (N) TransacaoTransporte
TransacaoTransporte (1) ──── (1) Pesagem
```

---

## Decisões técnicas

### IDs com sequence

Todos os IDs usam `GenerationType.SEQUENCE` com `allocationSize = 1`. O padrão do JPA sem configuração explícita reserva blocos de 50 IDs de uma vez, o banco incrementa normalmente mas os IDs na aplicação ficam com saltos tipo 1, 51, 101. Com `allocationSize = 1` o incremento é sempre de 1 em 1, deixando o banco previsível e os registros em ordem de criação.

### Fila em memória por balança

Cada balança tem sua própria fila dentro de um `ConcurrentHashMap`. A fila é uma `ConcurrentLinkedDeque`, thread-safe para inserções simultâneas de múltiplas threads. Usei `ConcurrentLinkedDeque` em vez de `ArrayDeque` porque o endpoint de recepção é assíncrono e mais de uma thread pode tentar enfileirar na mesma balança ao mesmo tempo. `ArrayDeque` não é thread-safe e quebraria nesse cenário.

### Algoritmo de estabilização — janela deslizante com desvio padrão

Esse foi o ponto mais interessante do projeto. O problema era: como saber automaticamente quando o peso parou de oscilar?

A primeira ideia óbvia seria comparar a diferença entre a última e a penúltima leitura se for pequena, estabilizou. Mas o sensor oscila, um spike isolado derrubaria esse critério mesmo com o caminhão completamente parado.

Cheguei na janela deslizante com desvio padrão depois de lembrar de um conceito que vi estudando algoritmos. A ideia de usar uma janela de observação sobre uma série de valores em vez de olhar ponto a ponto. O desvio padrão distribui o peso de todas as leituras da janela, então um outlier sozinho não quebra o diagnóstico de estabilidade.

```
janela-minima = 10 leituras  →  100ms × 10 = 1 segundo de observação
threshold     = 0.5 kg       →  variação máxima aceitável
```

O scheduler roda a cada 200ms — mais frequente que as leituras para não perder a janela de estabilização.

A fila acumula leituras enquanto o caminhão está na balança. Quando chegam pelo menos 10 leituras, o desvio padrão é calculado sobre as **últimas 10** a janela deslizante. Leituras antigas com oscilação forte saem do cálculo conforme entram leituras novas.

`max - min` é sensível a picos isolados. Se uma leitura espúria aparecer no meio de uma sequência estável, a diferença entre max e min sobe e o sistema conclui erroneamente que o peso ainda está oscilando. O desvio padrão considera todas as leituras, um outlier tem pouco peso sobre o resultado final.

### Idempotência sem Redis

O ESP32 pode reenviar a mesma leitura se a rede falhar. Coloquei uma tabela `leitura_recebida` no próprio PostgreSQL sem Redis, sem infra extra. A chave hoje é `balancaId + placa + peso (2 casas decimais)`. Se a mesma combinação chegar de novo, retorno 202 e não enfileiro de novo.

Na primeira versão eu truncava o timestamp pro segundo. Só depois percebi que com leituras a cada 100ms isso bloqueava quase tudo, entrava 1 leitura por segundo na fila. Detalhe desse ajuste tá na seção de desafios lá embaixo.

### Autenticação por API Key

Cada balança tem uma `apiKey` única enviada no header `X-Balance-Key`. JWT seria overhead desnecessário, o ESP32 não tem contexto de sessão. API Key por dispositivo é o padrão adequado para esse tipo de integração.

### TransacaoTransporte — campo fim nullable

Quando a transação começa o fim ainda não existe. Só é preenchido depois que a pesagem estabiliza e é vinculada. Forçar `nullable = false` quebraria o fluxo.

### Pesagem guarda a placa como String

Mesmo tendo o relacionamento com `Caminhao`, a `Pesagem` guarda a placa diretamente. É dado histórico, se o caminhão for removido do cadastro um dia, o registro da pesagem ainda precisa saber qual placa passou pela balança.

### StatusTransacao como @Enumerated(EnumType.STRING)

Os status são fixos e definidos no código, nunca vão ser criados dinamicamente. Salvar o nome do enum como texto no banco deixa o dado legível sem precisar de join com tabela auxiliar.

### GlobalExceptionHandler separado em advice/

Centraliza o tratamento de exceções em um único ponto da aplicação. Com isso, os controllers ficam focados apenas no fluxo principal, sem a necessidade de espalhar blocos try/catch pelo código. Além disso, todas as respostas de erro seguem um padrão único, independentemente de onde a exceção tenha sido gerada.

---

## Precificação dinâmica

A margem varia inversamente ao estoque disponível do grão na doca.

```
proporcao = estoqueAtual / estoqueMaximo
margem    = 0.20 - (proporcao × (0.20 - 0.05))

Estoque 0%   →  margem 20%   (grão escasso)
Estoque 50%  →  margem 12.5%
Estoque 100% →  margem 5%    (grão abundante)
```

---

## Endpoints principais

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/v1/pesagem` | Recebe leitura da balança (fire and forget) |
| GET | `/api/v1/pesagens` | Lista pesagens já salvas (relatório básico) |
| POST | `/api/v1/transacoes` | Inicia uma transação de transporte |
| GET | `/api/v1/transacoes` | Lista todas as transações |
| POST | `/api/v1/filiais` | Cadastra filial |
| POST | `/api/v1/caminhoes` | Cadastra caminhão |
| POST | `/api/v1/tipos-grao` | Cadastra tipo de grão |
| POST | `/api/v1/balancas` | Cadastra balança |

### Exemplo de leitura da balança

```bash
curl -X POST http://localhost:8080/api/v1/pesagem \
  -H "Content-Type: application/json" \
  -H "X-Balance-Key: chave-da-balanca-1" \
  -d '{"id": "balanca-01", "placa": "ABC1D23", "peso": 28500.0}'
```

---

## Relatórios e métricas administrativas

O desafio pede identificar dados úteis para análise. Com o que já está persistido, dá pra extrair:

| Métrica | Fonte |
|---|---|
| Volume carregado por tipo de grão | `pesagem` + `tipo_grao` |
| Valor total por filial | `pesagem` → `balanca` → `filial` |
| Tempo de cada operação | `transacao_transporte.inicio` e `fim` |
| Histórico de pesagens por placa | `pesagem.placa` |
| Margem aplicada no momento | calculada em `PrecificacaoService` com estoque do grão |

O endpoint `GET /api/v1/pesagens` expõe as pesagens salvas. Relatórios mais elaborados (filtro por data, agrupamento) seriam o próximo passo.

---

## Cálculo do peso líquido e custo da carga

Quando a pesagem estabiliza o sistema faz dois cálculos automáticos antes de salvar.

### Peso líquido

A balança lê o peso bruto — caminhão + carga. Para saber quanto de grão foi carregado de fato, subtrai a tara, que é o peso do caminhão vazio cadastrado no sistema.

```
pesoLiquido = pesoBrutoEstabilizado - tara
```

Exemplo:
```
pesoBrutoEstabilizado = 28.500 kg  (o que a balança leu)
tara                  =  8.000 kg  (peso do caminhão vazio, vem do cadastro)
pesoLiquido           = 20.500 kg  (peso real da carga de grão)
```

A tara varia de caminhão para caminhão, cada modelo tem um peso vazio diferente. Por isso ela fica no cadastro do caminhão e não é um valor fixo no sistema.

### Custo da carga

Com o peso líquido calculado, o sistema aplica a margem dinâmica (5% a 20% conforme estoque) e multiplica pelo preço de venda por tonelada.

```
margem = calculada pelo estoque atual do grão
precoVenda = precoCompra × (1 + margem)
pesoLiquidoToneladas = pesoLiquido / 1000
custoDaCarga = pesoLiquidoToneladas × precoVenda
```

Exemplo (estoque a 50% da capacidade → margem 12,5%):

```
pesoLiquidoToneladas = 20,5 t
precoCompra          = R$ 120,00
margem               = 12,5%
precoVenda           = R$ 135,00
custoDaCarga         = R$ 2.767,50
```

---

## Desafios encontrados

Coisas que apareceram durante o desenvolvimento algumas eu resolvi, outras documentei como evolução futura.

### Critério de estabilização

O maior desafio foi definir quando considerar o peso estabilizado. Comparei as duas últimas leituras quebra com spike. Tentei `max - min` da janela mesmo problema.

Fui pro desvio padrão sobre as últimas 10 leituras. Lembrei disso de estudo de algoritmos (janela deslizante). Um outlier isolado pesa pouco no resultado.

### A fila inteira vs as últimas 10 (bug que só apareceu no teste)

Na primeira versão o desvio padrão olhava **toda a fila acumulada**. Na teoria fazia sentido. Quanto mais dado, melhor. Na prática, com o simulador, as 15 primeiras leituras oscilavam ±80 kg. Mesmo depois do peso estabilizar, o desvio geral ficava em ~29 kg e nunca passava no threshold de 0,5 kg. A pesagem nunca salvava.

Ajustei pra calcular só sobre as **últimas 10 leituras**, que era o que eu já tinha descrito como janela deslizante. Depois disso o simulador passou a funcionar de ponta a ponta.

### Idempotência bloqueando leituras legítimas

Outro bug que só vi rodando o simulador: primeiro usei timestamp truncado ao segundo na chave de idempotência. Com ESP32 mandando a cada 100ms, só 1 leitura por segundo entrava na fila — a estabilização não tinha dados suficientes.

Mudei pra incluir o peso na chave. Aí outro problema: arredondar pro inteiro fazia `28500.01` e `28500.10` virarem a mesma chave. Na fase estável do simulador várias leituras caíam no mesmo inteiro e eram ignoradas.

Solução final: peso com 2 casas decimais na chave. Reenvio idêntico continua bloqueado; leituras diferentes passam.

### Schema do banco vs entidade JPA

A aplicação subia com erro de validação do Hibernate: coluna `tara` na tabela `caminhao` estava como `FLOAT8` na migration, mas a entidade usa `BigDecimal` (PostgreSQL espera `NUMERIC`). Corrigi a migration V3 e criei a V8 pro banco que já existia.

### Margem calculada mas não aplicada

O `PrecificacaoService` já tinha a fórmula de margem dinâmica. Só percebi depois que o `PesagemService` calculava a margem e descartava. salvava o custo só com preço de compra. Conectei o `calculaPrecoVenda` no fluxo de salvamento.

### Thread safety da fila

Primeiro rascunho usava `ArrayDeque` no `ConcurrentHashMap`. `ArrayDeque` não é thread-safe — duas requisições simultâneas na mesma balança podiam corromper a fila. Troquei por `ConcurrentLinkedDeque`.

### IDs sequenciais com saltos

O JPA com `GenerationType.SEQUENCE` sem config reserva blocos de 50 IDs. Ficava 1, 51, 101... Configurei `allocationSize = 1` pra incrementar de 1 em 1.

### O que ainda daria pra melhorar

- Fila persistente (Redis ou banco), reiniciar o servidor perde leituras em memória
- Relatórios com filtro por data e agrupamento
- Testes automatizados do algoritmo de estabilização
- Autenticação nos endpoints de cadastro

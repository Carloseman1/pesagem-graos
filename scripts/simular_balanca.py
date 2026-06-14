import requests
import time
import random
import threading
from datetime import datetime

# ─────────────────────────────────────────
#  CONFIGURAÇÕES GERAIS
# ─────────────────────────────────────────
BASE_URL        = "http://localhost:8080/api/v1"
TOTAL_BALANCAS  = 50
PESO_BASE       = 28500.0

# Lock para não misturar logs de threads diferentes no terminal
log_lock = threading.Lock()

# Contadores globais (thread-safe)
total_enviadas  = 0
total_erros     = 0
total_202       = 0
counter_lock    = threading.Lock()


# ─────────────────────────────────────────
#  HELPERS DE LOG
# ─────────────────────────────────────────
def ts():
    """Timestamp formatado para o log."""
    return datetime.now().strftime("%H:%M:%S.%f")[:-3]


def log(balanca_id, fase, leitura_num, peso, status=None, erro=None):
    global total_enviadas, total_erros, total_202

    with counter_lock:
        total_enviadas += 1
        if erro:
            total_erros += 1
        elif status == 202:
            total_202 += 1

    with log_lock:
        if erro:
            print(
                f"[{ts()}] ❌ {balanca_id} | {fase} | leitura {leitura_num:02d} "
                f"| {peso:.2f} kg | ERRO: {erro}"
            )
        else:
            icone = "✅" if status == 202 else "⚠️ "
            print(
                f"[{ts()}] {icone} {balanca_id} | {fase:<12} | leitura {leitura_num:02d} "
                f"| {peso:.2f} kg | HTTP {status}"
            )


def log_inicio(balanca_id, placa, peso_real):
    with log_lock:
        print(f"[{ts()}] 🚛 {balanca_id} | placa={placa} | peso_real={peso_real} kg | iniciando simulação")


def log_fim(balanca_id, placa):
    with log_lock:
        print(f"[{ts()}] 🏁 {balanca_id} | placa={placa} | simulação concluída")


# ─────────────────────────────────────────
#  ENVIO DE UMA LEITURA
# ─────────────────────────────────────────
def enviar_leitura(balanca_id, api_key, placa, peso, fase, leitura_num):
    try:
        response = requests.post(
            f"{BASE_URL}/pesagem",
            json={"id": balanca_id, "placa": placa, "peso": peso},
            headers={
                "Content-Type": "application/json",
                "X-Balance-Key": api_key
            },
            timeout=5
        )
        log(balanca_id, fase, leitura_num, peso, status=response.status_code)
    except requests.exceptions.ConnectionError:
        log(balanca_id, fase, leitura_num, peso, erro="aplicação offline")
    except requests.exceptions.Timeout:
        log(balanca_id, fase, leitura_num, peso, erro="timeout 5s")
    except Exception as e:
        log(balanca_id, fase, leitura_num, peso, erro=str(e))


# ─────────────────────────────────────────
#  SIMULAÇÃO DE UMA BALANÇA (roda em thread)
# ─────────────────────────────────────────
def simular_balanca(indice):
    balanca_id = f"balanca-{indice:02d}"
    api_key    = f"chave-da-balanca-{indice}"
    placa      = f"BAL{indice:04d}"          # ex: BAL0001, BAL0050
    peso_real  = round(PESO_BASE + random.uniform(-2000, 2000), 2)  # cada caminhão tem peso diferente

    log_inicio(balanca_id, placa, peso_real)

    # FASE 1 — oscilação alta (caminhão posicionando)
    for i in range(15):
        variacao = random.uniform(-80, 80)
        peso = round(peso_real + variacao, 2)
        enviar_leitura(balanca_id, api_key, placa, peso, "OSCILAÇÃO", i + 1)
        time.sleep(0.1)

    # FASE 2 — estabilizando
    for i in range(10):
        variacao = random.uniform(-10, 10)
        peso = round(peso_real + variacao, 2)
        enviar_leitura(balanca_id, api_key, placa, peso, "ESTABILIZ.", i + 1)
        time.sleep(0.1)

    # FASE 3 — peso estável (scheduler deve detectar aqui)
    for i in range(15):
        variacao = random.uniform(-0.2, 0.2)
        peso = round(peso_real + variacao, 2)
        enviar_leitura(balanca_id, api_key, placa, peso, "ESTÁVEL", i + 1)
        time.sleep(0.1)

    log_fim(balanca_id, placa)


# ─────────────────────────────────────────
#  RESUMO FINAL
# ─────────────────────────────────────────
def imprimir_resumo(duracao_segundos):
    print()
    print("=" * 60)
    print("  RESUMO DA SIMULAÇÃO")
    print("=" * 60)
    print(f"  Balanças simuladas : {TOTAL_BALANCAS}")
    print(f"  Leituras enviadas  : {total_enviadas}")
    print(f"  Respostas 202      : {total_202}")
    print(f"  Erros              : {total_erros}")
    print(f"  Duração            : {duracao_segundos:.1f}s")
    print(f"  Throughput médio   : {total_enviadas / duracao_segundos:.1f} req/s")
    print("=" * 60)
    print()
    print("  Próximos passos para verificar:")
    print("  curl http://localhost:8080/api/v1/pesagens")
    print("  curl http://localhost:8080/api/v1/transacoes")
    print()
    print("  Esperado: 0 pesagens salvas (balanças não cadastradas no seed).")
    print("  Para salvar de verdade, ajuste o seed_demo.ps1 para cadastrar")
    print("  as 50 balanças e rode este script depois.")
    print("=" * 60)


# ─────────────────────────────────────────
#  MAIN
# ─────────────────────────────────────────
if __name__ == "__main__":
    print()
    print("=" * 60)
    print("  SIMULADOR DE 50 BALANÇAS SIMULTÂNEAS — pesagem-graos")
    print("=" * 60)
    print(f"  Endpoint : {BASE_URL}/pesagem")
    print(f"  Balanças : {TOTAL_BALANCAS} threads simultâneas")
    print(f"  Leituras : 40 por balança (15 + 10 + 15)")
    print(f"  Total    : {TOTAL_BALANCAS * 40} requisições")
    print(f"  Iniciando em 3 segundos...")
    print("=" * 60)
    print()
    time.sleep(3)

    threads = []
    inicio = time.time()

    for i in range(1, TOTAL_BALANCAS + 1):
        t = threading.Thread(target=simular_balanca, args=(i,), daemon=True)
        threads.append(t)

    # Sobe todas as threads de uma vez para simular simultaneidade real
    for t in threads:
        t.start()

    # Aguarda todas terminarem
    for t in threads:
        t.join()

    duracao = time.time() - inicio
    imprimir_resumo(duracao)
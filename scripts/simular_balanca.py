import requests
import time
import random
from datetime import datetime

BASE_URL = "http://localhost:8080/api/v1"

def buscar_transacao_em_andamento():
    """Busca automaticamente a primeira transação EM_ANDAMENTO no banco."""
    response = requests.get(f"{BASE_URL}/transacoes", timeout=5)
    transacoes = response.json()

    em_andamento = [t for t in transacoes if t["status"] == "EM_ANDAMENTO"]

    if not em_andamento:
        print("ERRO: Nenhuma transação EM_ANDAMENTO encontrada.")
        print("Execute o seed antes de rodar o simulador.")
        exit(1)

    return em_andamento[0]

def buscar_balanca_da_filial(filial_id):
    """Busca a balança cadastrada na filial da transação."""
    response = requests.get(f"{BASE_URL}/balancas", timeout=5)
    balancas = response.json()

    for balanca in balancas:
        if balanca["filial"]["id"] == filial_id:
            return balanca

    print(f"ERRO: Nenhuma balança encontrada para a filial {filial_id}")
    exit(1)

def buscar_tara_caminhao(placa):
    """Busca a tara do caminhão pelo placa."""
    response = requests.get(f"{BASE_URL}/caminhoes", timeout=5)
    caminhoes = response.json()

    for caminhao in caminhoes:
        if caminhao["placa"] == placa:
            return caminhao["tara"]

    print(f"ERRO: Caminhão {placa} não encontrado")
    exit(1)

def enviar_leitura(balanca_id, api_key, placa, peso):
    inicio = time.perf_counter()
    try:
        response = requests.post(
            f"{BASE_URL}/pesagem",
            json={"id": balanca_id, "placa": placa, "peso": peso},
            headers={
                "Content-Type": "application/json",
                "X-Balance-Key": api_key,
                "X-Skip-Idempotencia": "true"
            },
            timeout=5
        )
        tempo_ms = (time.perf_counter() - inicio) * 1000
        return {"status": response.status_code, "tempo_ms": round(tempo_ms, 2)}

    except requests.exceptions.ConnectionError:
        print("ERRO: aplicacao nao esta rodando em localhost:8080")
        exit(1)
    except requests.exceptions.Timeout:
        return {"status": "TIMEOUT", "tempo_ms": 0}


def log_leitura(fase, numero, peso, peso_real, resultado):
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-3]
    diferenca = peso - peso_real
    print(
        f"[{timestamp}] [{fase}] seq={numero:02d} "
        f"peso={peso:8.2f}kg delta={diferenca:+7.2f}kg "
        f"status={resultado['status']} latencia={resultado['tempo_ms']:6.2f}ms"
    )


def executar_fase(nome, quantidade, variacao_min, variacao_max, balanca_id, api_key, placa, peso_real):
    pesos = []
    print()
    print("=" * 25 + f" {nome} " + "=" * 25)

    for i in range(quantidade):
        variacao = random.uniform(variacao_min, variacao_max)
        peso = round(peso_real + variacao, 2)
        resultado = enviar_leitura(balanca_id, api_key, placa, peso)
        log_leitura(nome, i + 1, peso, peso_real, resultado)
        pesos.append(peso)
        time.sleep(0.1)

    return pesos


def resumo(nome, pesos):
    media = sum(pesos) / len(pesos)
    print(f"\nResumo [{nome}] — leituras={len(pesos)} media={media:.2f}kg "
          f"min={min(pesos):.2f}kg max={max(pesos):.2f}kg faixa={max(pesos)-min(pesos):.2f}kg")


def simular():
    print("=" * 80)
    print("Buscando transação EM_ANDAMENTO no banco...")

    transacao   = buscar_transacao_em_andamento()
    placa       = transacao["caminhao"]["placa"]
    filial_id   = transacao["filial"]["id"]
    tara        = transacao["caminhao"]["tara"]
    tipo_grao   = transacao["tipoGrao"]["nome"]
    balanca     = buscar_balanca_da_filial(filial_id)
    balanca_id  = balanca["identificador"]
    api_key     = balanca["apiKey"]

    peso_real = tara + random.uniform(15000, 25000)
    peso_real = round(peso_real, 2)

    print("=" * 80)
    print("SIMULADOR DE BALANCA - PESAGEM DE GRAOS")
    print("=" * 80)
    print(f"Transacao : id={transacao['id']} status={transacao['status']}")
    print(f"Balanca   : {balanca_id}")
    print(f"Placa     : {placa}")
    print(f"Motorista : {transacao['caminhao']['motorista']}")
    print(f"Grao      : {tipo_grao}")
    print(f"Filial    : {transacao['filial']['nome']} - {transacao['filial']['cidade']}")
    print(f"Tara      : {tara:.2f} kg")
    print(f"Peso real : {peso_real:.2f} kg")
    print(f"Endpoint  : {BASE_URL}/pesagem")
    print("=" * 80)

    pesos_f1 = executar_fase("OSCILACAO_ALTA", 15, -80, 80, balanca_id, api_key, placa, peso_real)
    pesos_f2 = executar_fase("ESTABILIZANDO",  10, -10, 10, balanca_id, api_key, placa, peso_real)
    pesos_f3 = executar_fase("ESTAVEL",        15, -0.2, 0.2, balanca_id, api_key, placa, peso_real)

    print("\n" + "=" * 80)
    print("RESUMO FINAL")
    print("=" * 80)
    resumo("OSCILACAO_ALTA", pesos_f1)
    resumo("ESTABILIZANDO",  pesos_f2)
    resumo("ESTAVEL",        pesos_f3)

    print("\n" + "=" * 80)
    print("Simulacao concluida.")
    print("Verifique os logs da aplicacao para confirmar o salvamento da pesagem.")
    print("=" * 80)


if __name__ == "__main__":
    simular()
import requests
import time
import random

BASE_URL = "http://localhost:8080/api/v1"
BALANCA_ID = "balanca-01"
API_KEY = "chave-da-balanca-1"
PLACA = "ABC1D23"

PESO_REAL = 28500.0

def enviar_leitura(peso):
    try:
        response = requests.post(
            f"{BASE_URL}/pesagem",
            json={"id": BALANCA_ID, "placa": PLACA, "peso": peso},
            headers={
                "Content-Type": "application/json",
                "X-Balance-Key": API_KEY
            },
            timeout=5
        )
        return response.status_code
    except requests.exceptions.ConnectionError:
        print("Erro: aplicação não está rodando em localhost:8080")
        exit(1)


def simular():
    print("=" * 50)
    print("Simulador de Balança — pesagem-graos")
    print("=" * 50)
    print(f"Balança:  {BALANCA_ID}")
    print(f"Placa:    {PLACA}")
    print(f"Peso real: {PESO_REAL} kg")
    print("=" * 50)

    print("\n[FASE 1] Oscilação alta — caminhão posicionando...")
    for i in range(15):
        variacao = random.uniform(-80, 80)
        peso = round(PESO_REAL + variacao, 2)
        status = enviar_leitura(peso)
        print(f"  leitura {i+1:02d}: {peso:.2f} kg  -> {status}")
        time.sleep(0.1)

    print("\n[FASE 2] Estabilizando...")
    for i in range(10):
        variacao = random.uniform(-10, 10)
        peso = round(PESO_REAL + variacao, 2)
        status = enviar_leitura(peso)
        print(f"  leitura {i+1:02d}: {peso:.2f} kg  -> {status}")
        time.sleep(0.1)

    print("\n[FASE 3] Peso estável — scheduler deve detectar e salvar...")
    for i in range(15):
        variacao = random.uniform(-0.2, 0.2)
        peso = round(PESO_REAL + variacao, 2)
        status = enviar_leitura(peso)
        print(f"  leitura {i+1:02d}: {peso:.2f} kg  -> {status}")
        time.sleep(0.1)

    print("\n" + "=" * 50)
    print("Simulação concluída.")
    print("Verifique no log da aplicação se a pesagem foi salva.")
    print("=" * 50)


if __name__ == "__main__":
    simular()

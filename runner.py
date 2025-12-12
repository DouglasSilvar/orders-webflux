import requests
import uuid
import random
import concurrent.futures
import time
import argparse
from typing import List, Dict

def generate_barcode() -> str:
    """
    Gera um código de barras de 13 dígitos (padrão EAN-13).
    Os primeiros 12 dígitos são aleatórios, o último é o dígito verificador.
    """
    # Gera 12 dígitos aleatórios
    first_12 = ''.join([str(random.randint(0, 9)) for _ in range(12)])

    # Calcula dígito verificador (algoritmo EAN-13)
    sum_odd = sum(int(first_12[i]) for i in range(0, 12, 2))
    sum_even = sum(int(first_12[i]) for i in range(1, 12, 2))
    total = sum_odd + sum_even * 3
    check_digit = (10 - (total % 10)) % 10

    return first_12 + str(check_digit)

def generate_order_data() -> Dict:
    """
    Gera dados aleatórios para um pedido.
    """
    return {
        "orderId": str(uuid.uuid4()),
        "barCode": generate_barcode(),
        "quantity": random.randint(1, 25),
        "price": round(random.uniform(1.0, 50.0), 2)
    }

def send_order(url: str, order_data: Dict) -> Dict:
    """
    Envia um pedido para a API.
    Retorna um dicionário com o resultado.
    """
    headers = {
        'Content-Type': 'application/json'
    }

    try:
        response = requests.post(url, json=order_data, headers=headers, timeout=10)
        return {
            "success": True,
            "status_code": response.status_code,
            "order_id": order_data["orderId"],
            "response_time": response.elapsed.total_seconds(),
            "response_text": response.text[:200]  # Primeiros 200 caracteres
        }
    except Exception as e:
        return {
            "success": False,
            "order_id": order_data["orderId"],
            "error": str(e)
        }

def main():
    parser = argparse.ArgumentParser(description='Envia requisições paralelas para a API de pedidos')
    parser.add_argument('--url', type=str, default='http://localhost:8080/v1/api/orders',
                        help='URL da API (padrão: http://localhost:8080/v1/api/orders)')
    parser.add_argument('--requests', type=int, default=10000, ################################################################################
                        help='Número de requisições paralelas (padrão: 10)')
    parser.add_argument('--max-workers', type=int, default=None,
                        help='Número máximo de workers paralelos (padrão: igual ao número de requisições)')

    args = parser.parse_args()

    print("=" * 60)
    print(f"INICIANDO TESTE DE CARGA")
    print(f"URL: {args.url}")
    print(f"Número de requisições: {args.requests}")
    print(f"Execução paralela: SIM")
    print("=" * 60)

    # Gera dados para todos os pedidos
    orders_data = [generate_order_data() for _ in range(args.requests)]

    # Exibe alguns exemplos
    print("\nExemplos de dados gerados:")
    for i in range(min(3, len(orders_data))):
        print(f"Pedido {i+1}: {orders_data[i]}")

    if args.requests > 3:
        print("...")

    print(f"\nEnviando {args.requests} requisições paralelas...")
    print("-" * 60)

    start_time = time.time()

    # Envia requisições em paralelo
    max_workers = args.max_workers if args.max_workers else args.requests

    with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as executor:
        # Prepara as tarefas
        futures = [executor.submit(send_order, args.url, order_data)
                   for order_data in orders_data]

        # Coleta os resultados
        results = []
        for i, future in enumerate(concurrent.futures.as_completed(futures), 1):
            result = future.result()
            results.append(result)

            status = "✓" if result["success"] else "✗"
            if result["success"]:
                print(f"{status} Pedido {i}/{args.requests}: "
                      f"ID={result['order_id'][:8]}... "
                      f"Status={result['status_code']} "
                      f"Tempo={result['response_time']:.3f}s")
            else:
                print(f"{status} Pedido {i}/{args.requests}: "
                      f"ID={result['order_id'][:8]}... "
                      f"ERRO: {result['error']}")

    end_time = time.time()
    total_time = end_time - start_time

    print("-" * 60)

    # Estatísticas
    successful = sum(1 for r in results if r["success"])
    failed = args.requests - successful

    if successful > 0:
        response_times = [r["response_time"] for r in results if r.get("response_time")]
        avg_time = sum(response_times) / len(response_times)

        status_codes = {}
        for r in results:
            if r.get("status_code"):
                status_codes[r["status_code"]] = status_codes.get(r["status_code"], 0) + 1
    else:
        avg_time = 0
        status_codes = {}

    print("RESULTADOS:")
    print(f"  Total de requisições: {args.requests}")
    print(f"  Sucessos: {successful}")
    print(f"  Falhas: {failed}")
    print(f"  Tempo total: {total_time:.2f} segundos")
    print(f"  Tempo médio por requisição: {avg_time:.3f} segundos")
    print(f"  Requisições por segundo: {args.requests/total_time:.2f}")

    if status_codes:
        print(f"  Códigos de status HTTP:")
        for code, count in status_codes.items():
            print(f"    {code}: {count} requisições")

    # Exemplo de pedido que falhou (se houver)
    failed_orders = [r for r in results if not r["success"]]
    if failed_orders:
        print(f"\nExemplo de pedido com falha:")
        print(f"  Order ID: {failed_orders[0]['order_id']}")
        print(f"  Erro: {failed_orders[0]['error']}")

if __name__ == "__main__":
    main()
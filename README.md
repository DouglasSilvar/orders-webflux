# 🚀 Desafio: API de Pedidos com MongoDB Replica Set

Este projeto consiste em uma infraestrutura robusta para uma aplicação de pedidos, utilizando Docker para orquestrar um cluster de MongoDB em modo Replica Set, garantindo alta disponibilidade e resiliência do banco de dados.

## ✨ Visão Geral da Arquitetura

A arquitetura é composta por:

- **3x Instâncias MongoDB**: `mongo1`, `mongo2`, e `mongo3` configuradas para operar como um Replica Set chamado `rs0`. Isso significa que se o nó primário falhar, um dos secundários será automaticamente eleito para assumir, mantendo a aplicação operacional.
- **1x Serviço de Iniciação**: O `mongo-initiate` é um contêiner temporário responsável por configurar e iniciar o Replica Set, conectando as três instâncias MongoDB.
- **Volumes Persistentes**: Cada instância do MongoDB utiliza um volume Docker (`mongo1_data`, `mongo2_data`, `mongo3_data`) para garantir que os dados não sejam perdidos ao reiniciar os contêineres.

## 📋 Pré-requisitos

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas em sua máquina:

- Docker
- Docker Compose (geralmente já vem com o Docker Desktop)
- PowerShell (para executar o script de teste no Windows)

## ▶️ Como Executar o Projeto

Siga os passos abaixo para subir a infraestrutura do banco de dados.

1.  **Clone o repositório (se aplicável) ou tenha os arquivos em um diretório local.**

2.  **Inicie os serviços com o Docker Compose:**

    Abra um terminal na raiz do projeto e execute o seguinte comando:

    ```bash
    docker-compose up -d
    ```

    Este comando irá baixar as imagens necessárias, criar e iniciar os contêineres em segundo plano (`-d`). O serviço `mongo-initiate` será executado automaticamente para configurar o Replica Set e depois será finalizado.

3.  **Verifique se os contêineres estão em execução:**

    ```bash
    docker-compose ps
    ```

    Você deverá ver os três contêineres do MongoDB (`mongo1`, `mongo2`, `mongo3`) com o status `Up`.

4.  **(Opcional) Verifique o status do Replica Set:**

    Para confirmar que o Replica Set foi iniciado corretamente, você pode se conectar a uma das instâncias e verificar seu status.

    ```bash
    docker exec -it mongo1 mongosh --eval "rs.status()"
    ```

    O resultado deve mostrar os três membros do `rs0`, com um deles sendo o `PRIMARY` e os outros dois `SECONDARY`.

## 📡 Realizando Chamadas para a API

Este projeto foi desenvolvido com foco em alta performance e processamento de um grande volume de requisições. Como tal, o método recomendado para testar a API é através do script de teste de carga `runner.py`.

Assumimos que a sua API está rodando localmente na porta `8080`.

### Teste de Carga com `runner.py`

#### Pré-requisitos

- **Python 3** instalado.
- A biblioteca `requests`. Instale-a com o seguinte comando:
  ```bash
  pip install requests
  ```

#### Executando o Teste de Carga

1.  Certifique-se de que sua aplicação/API esteja rodando.
2.  Abra um terminal na raiz do projeto.
3.  Execute o script:
    ```bash
    python runner.py
    ```
    Por padrão, o script enviará **10.000 requisições** para `http://localhost:8080/v1/api/orders`. Ele exibirá o progresso em tempo real e, ao final, apresentará um resumo com estatísticas de sucesso, falha, tempo médio e requisições por segundo.

#### Customizando a Execução

Você pode alterar o comportamento do script usando argumentos de linha de comando:

- `--url`: Altera a URL do endpoint.
- `--requests`: Define o número total de requisições.
- `--max-workers`: Controla o número de threads paralelas.

**Exemplo:** Enviando 500 requisições com no máximo 50 workers (threads) em paralelo.
```bash
python runner.py --requests 500 --max-workers 50
```

## 🛑 Como Parar os Serviços

Para parar e remover os contêineres, redes e volumes criados pelo Docker Compose, execute o comando abaixo na raiz do projeto:

```bash
docker-compose down
```

Se desejar parar os contêineres sem remover os volumes (preservando os dados), utilize:

```bash
docker-compose stop
```

# E-commerce API

API REST para gerenciamento de produtos e pedidos de um e-commerce, desenvolvida com Spring Boot.

## Sobre o projeto

Projeto iniciado em grupo durante o programa de bolsas da Compass UOL e finalizado individualmente por mim, com correções de bugs, melhorias de qualidade de código e ampliação da cobertura de testes.

## Tecnologias

- Java 17
- Spring Boot 3.2.2
- Spring Data JPA
- Spring Validation
- Spring Cloud OpenFeign (integração com ViaCEP)
- MySQL (produção) / H2 (testes)
- Springdoc OpenAPI (Swagger)
- Lombok
- ModelMapper
- JUnit 5 + Mockito

## Funcionalidades

- CRUD completo de produtos com validação e paginação
- CRUD completo de pedidos com regras de negócio
- Preenchimento automático de endereço via CEP (ViaCEP)
- Desconto de 5% automático para pagamento via PIX
- Cancelamento de pedido com validação de prazo (até 90 dias)
- Documentação interativa via Swagger UI
- Tratamento global de exceções

## Como rodar

### Opção 1 — Docker (recomendado)

Pré-requisito: [Docker](https://www.docker.com/) instalado.

```bash
docker-compose up --build
```

A API estará disponível em `http://localhost:8080`.
A documentação Swagger estará em `http://localhost:8080/docs-EcommerceMenteBinaria.html`.

Para parar:

```bash
docker-compose down
```

### Opção 2 — Local

**Pré-requisitos:** Java 17+, MySQL 8+, Maven.

Crie o banco de dados:

```sql
CREATE DATABASE ecommerce;
```

Configure as variáveis de ambiente ou edite `src/main/resources/application.properties`:

```properties
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
```

Rode a aplicação:

```bash
mvn spring-boot:run
```

### Rodando os testes

```bash
mvn test
```

## Endpoints

### Produtos — `/products`

| Método | Endpoint | Descrição | Status |
|--------|----------|-----------|--------|
| POST | `/products` | Criar produto | 201 |
| GET | `/products` | Listar todos | 200 |
| GET | `/products/{id}` | Buscar por ID | 200 |
| GET | `/products/page` | Listar paginado | 200 |
| PUT | `/products/{id}` | Atualizar | 200 |
| DELETE | `/products/{id}` | Deletar | 204 |

**Exemplo — criar produto:**

```json
POST /products
{
  "name": "Smartphone",
  "description": "Smartphone top de linha com câmera de 108MP",
  "price": 1999.99
}
```

**Exemplo — resposta:**

```json
{
  "id": 1,
  "name": "Smartphone",
  "description": "Smartphone top de linha com câmera de 108MP",
  "price": 1999.99
}
```

### Pedidos — `/orders`

| Método | Endpoint | Descrição | Status |
|--------|----------|-----------|--------|
| POST | `/orders` | Criar pedido | 201 |
| GET | `/orders` | Listar todos | 200 |
| GET | `/orders/{id}` | Buscar por ID | 200 |
| GET | `/orders/page` | Listar paginado | 200 |
| PUT | `/orders/{id}` | Atualizar | 200 |
| DELETE | `/orders/{id}` | Cancelar pedido | 200 |

**Exemplo — criar pedido:**

```json
POST /orders
{
  "products": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ],
  "address": {
    "number": 235,
    "complement": "Ap 101",
    "postalCode": "89803108"
  },
  "paymentMethod": "PIX"
}
```

**Métodos de pagamento aceitos:** `CREDIT_CARD`, `BANK_TRANSFER`, `CRYPTOCURRENCY`, `GIFT_CARD`, `PIX`, `OTHER`

**Exemplo — cancelar pedido:**

```json
DELETE /orders/{id}
{
  "cancelReason": "Compra duplicada"
}
```

> Cancelamento só é permitido para pedidos com status `CONFIRMED` criados há menos de 90 dias.

## Regras de negócio

- Produto deve ter nome único no sistema
- Descrição do produto: mínimo de 10 caracteres
- Preço do produto deve ser positivo
- Pedidos com pagamento via **PIX** recebem 5% de desconto automaticamente
- Pedidos com status **SENT** ou **CANCELED** não podem ser cancelados
- Pedidos com mais de 90 dias de criação não podem ser cancelados
- O CEP é validado via ViaCEP — cidade, rua e estado são preenchidos automaticamente

## Respostas de erro

| Código | Significado |
|--------|-------------|
| 400 | Requisição inválida |
| 404 | Recurso não encontrado |
| 409 | Conflito (ex: nome de produto duplicado) |
| 422 | Dados inválidos (validação) |

## Crédito

Projeto iniciado durante o programa de bolsas da **Compass UOL** em grupo (equipe Mente Binária). Finalizado, corrigido e evoluído individualmente por [Loude Sime](https://github.com/loudedje).

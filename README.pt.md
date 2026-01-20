# Bookhub

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-green)
![Postgres](https://img.shields.io/badge/PostgreSQL-15-blue)

Sistema de gerenciamento de bibliotecas com Spring Boot, implementando controle de acervos, empréstimos e integração com a API do Google Books.

---

## Visão Geral

API REST para gestão de bibliotecas, incluindo cadastro de livros, controle de usuários com diferentes perfis (READER e LIBRARIAN), gerenciamento de empréstimos com validações de limite e atraso, e importação automática de dados bibliográficos via Google Books API. A autenticação é feita com JWT e Spring Security.

---

## Tecnologias

- Java 21
- Spring Boot
- PostgreSQL
- JWT
- Docker & Docker Compose

---

## Estrutura do Projeto

```
bookhub/
├── src/main/java/com/bookhub/bookhub/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── factory/
│   ├── filter/
│   ├── repository/
│   └── service/
├── src/main/resources/
│   └── application.yml
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## Funcionalidades Principais

- Gestão completa de livros (CRUD, busca por título/autor/ISBN)
- Sistema de autenticação com JWT
- Controle de empréstimos com validações
- Renovação de empréstimos
- Integração com Google Books API para importação de livros
- Dois perfis de usuário (READER e LIBRARIAN) com permissões diferentes
- Documentação com Swagger/OpenAPI

---

## Instalação e Execução

### Com Docker (Recomendado)

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/ArthurDOli/Bookhub.git
   cd bookhub
   ```

2. **Configure as variáveis de ambiente:**
   ```bash
   export JWT_SECRET="sua-chave-secreta"
   export GOOGLE_BOOKS_API_KEY="api-do-google-books"
   ```

3. **Inicie os containers:**
   ```bash
   docker-compose up -d
   ```

A aplicação estará disponível em `http://localhost:8080`

### Sem Docker

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/ArthurDOli/Bookhub.git
   cd bookhub
   ```

2. **Configure as variáveis de ambiente:**
   ```bash
   export JWT_SECRET="sua-chave-secreta"
   export GOOGLE_BOOKS_API_KEY="api-do-google-books"
   export DB_USERNAME="postgres"
   export DB_PASSWORD="sua-senha"
   ```

3. **Execute a aplicação:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

**Nota:** É necessário ter PostgreSQL rodando localmente na porta 5432.

---

## Documentação da API

Acesse a documentação em:
```
http://localhost:8080/swagger-ui/index.html
```

---

## Autenticação

Para testar a API, primeiro crie um usuário:

**POST** `/api/users/register`
```json
{
  "name": "Bibliotecário Teste",
  "email": "exemplo@gmail.com",
  "password": "senha123",
  "role": "LIBRARIAN"
}
```

Depois faça login:

**POST** `/api/auth/login`
```json
{
  "email": "exemplo@gmail.com",
  "password": "senha123"
}
```

Use o token retornado no header `Authorization: Bearer {token}` nas próximas requisições.
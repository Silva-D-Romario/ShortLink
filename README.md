# ShortLink - Sistema de Encurtamento de URLs

Esse será um sistema web completo para encurtar e gerenciar URLs longas, com autenticação segura e interface intuitiva.

## 📋 Sobre o Projeto

ShortLink é uma aplicação que permite aos usuários:
- Encurtar URLs longas em links curtos e memoráveis
- Gerenciar seus links criados
- Rastrear cliques e acessos
- Compartilhar links de forma simplificada
- Autenticação segura de usuários

## 🏗️ Arquitetura

O projeto é dividido em duas partes principais:

### Back-end (`Back/`)
- **Tecnologia**: Java Spring Boot
- **Estrutura**:
  - `auth/` - Módulo de autenticação e autorização
  - `controller/` - Controllers REST API
  - `service/` - Lógica de negócio
  - `model/` - Modelos de dados
  - `repository/` - Camada de persistência
  - `config/` - Configurações da aplicação
  - `security/` - Configurações de segurança
  - `util/` - Utilitários
  - `exception/` - Tratamento de exceções
  - `mapper/` - DTOs e mapeadores
  - `dto/` - Data Transfer Objects

### Front-end (`Front/`)
- **Tecnologia**: React
- Interface de usuário responsiva e moderna

## 🚀 Como Executar

### Pré-requisitos
- Java 11+ (ou versão especificada no projeto)
- Maven 3.6+
- Node.js 14+ e npm (ou yarn)
- Git

### Back-end

```bash
cd Back/app
mvn clean install
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`

### Front-end

```bash
cd Front
npm install
npm start
```

A aplicação estará disponível em `http://localhost:3000`

## 📁 Estrutura do Projeto

```
ShortLink/
├── Back/                    # Back-end Java Spring Boot
│   └── app/
│       ├── src/
│       │   ├── main/java/   # Código-fonte
│       │   └── test/        # Testes
│       ├── pom.xml          # Dependências Maven
│       └── HELP.md          # Ajuda do Maven
├── Front/                   # Front-end React
├── README.md                # Este arquivo
└── .gitignore              # Configuração Git
```

## 🔧 Configuração

### Variáveis de Ambiente

Crie um arquivo `.env` ou configure as variáveis no `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shortlink
spring.datasource.username=root
spring.datasource.password=seu_password
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

## 🧪 Testes

### Back-end
```bash
cd Back/app
mvn test
```

### Front-end
```bash
cd Front
npm test
```

## 📚 API Endpoints

Principais endpoints da API:

- `POST /api/auth/register` - Registrar novo usuário
- `POST /api/auth/login` - Login de usuário
- `POST /api/links` - Criar novo link encurtado
- `GET /api/links` - Listar links do usuário
- `GET /api/links/{id}` - Obter detalhes de um link
- `DELETE /api/links/{id}` - Deletar um link
- `GET /{shortCode}` - Redirecionar para URL original

## 🔐 Segurança

- Autenticação JWT
- Validação de entrada
- CORS configurado
- Proteção contra SQL Injection
- Senhas criptografadas

## 📦 Dependências Principais

### Back-end
- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL Driver
- JWT (JSON Web Tokens)

### Front-end
- React
- React Router
- Axios (ou Fetch API)
- Material-UI ou Bootstrap


---

**Desenvolvido com ❤️ para simplificar o compartilhamento de URLs**

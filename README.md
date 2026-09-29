# ShortLink

Aplicação full stack para criar, organizar e acompanhar links encurtados. O projeto combina uma API REST em Java com uma interface React.

## Funcionalidades

- Criação de URLs curtas com código Base62.
- Redirecionamento para a URL original.
- Listagem paginada dos links do usuário.
- Atualização e exclusão de links.
- Data de expiração e anotações opcionais.
- Registro de cliques, IP e `User-Agent`.
- Estatísticas básicas e detalhadas de acesso.

## Tecnologias

### Backend

- Java 17 e Spring Boot 4.1
- Spring Web MVC, Data JPA e Validation
- H2 para desenvolvimento local
- PostgreSQL e SQLite disponíveis como drivers
- Maven Wrapper

### Frontend

- React 19 e TypeScript
- Vite 8
- Oxc para análise estática

## Executar localmente

### Backend

```bash
cd Back/app
./mvnw spring-boot:run
```

A API ficará disponível em `http://localhost:18080`. O banco H2 será criado em `Back/app/data/shortlink`.

### Frontend

```bash
cd Front
npm install
npm run dev
```

Abra o endereço informado pelo Vite, normalmente `http://localhost:5173`.

## Principais endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/health` | Verificar o funcionamento da API |
| POST | `/api/auth/register` | Criar usuário de desenvolvimento |
| POST | `/api/auth/login` | Autenticar usuário de desenvolvimento |
| POST | `/api/shortlinks/shorten` | Criar um link curto |
| GET | `/api/shortlinks/{shortCode}` | Redirecionar para a URL original |
| GET | `/api/shortlinks/my-links` | Listar links paginados |
| PUT | `/api/shortlinks/{linkId}` | Atualizar um link |
| DELETE | `/api/shortlinks/{linkId}` | Excluir um link |
| GET | `/api/shortlinks/stats/{shortCode}` | Consultar estatísticas básicas |
| GET | `/api/shortlinks/stats/{shortCode}/detailed` | Consultar estatísticas detalhadas |

Exemplo de criação:

```json
{
  "originalUrl": "https://example.com/conteudo",
  "domain": "sl",
  "expiresInDays": 30,
  "notes": "Link de demonstração"
}
```

## Testes e build

```bash
cd Back/app
./mvnw test

cd ../../Front
npm run lint
npm run build
```

## Fluxo de desenvolvimento

- `main`: versão estável de produção.
- `develop`: desenvolvimento, correções e testes antes da publicação.

Cada mudança deve ser registrada em um commit próprio. Após os testes passarem em
`develop`, as alterações podem ser integradas à `main`.

## Estrutura

```text
Back/app/   API Spring Boot
Front/      Aplicação React
data/       Banco local de desenvolvimento
```

## Status do projeto

Projeto em desenvolvimento. A autenticação atual usa usuários e tokens temporários em memória, e as rotas ainda estão liberadas pela configuração de segurança. Antes de uma implantação pública, é necessário implementar autenticação persistente, hash de senhas, autorização por usuário, migrações de banco e configuração por variáveis de ambiente.

# 1ª Etapa — Trabalho Bimestral: CRUD de Cadastro (Marca)

## 1. O trabalho (enunciado)

### Backend

> O grupo deve responder a seguinte atividade, com os repositórios dos projetos (backend).
> Backend: o fluxo deve ser feito de forma completa (entidade → repository — que deve ser criado um método capaz de listar as primeiras 10 ocorrências da entidade desenvolvida em ordem alfabética através do filtro → service → controller).

### Frontend

> O grupo deve responder a seguinte atividade, com os repositórios dos projetos (frontend).
> Frontend: o fluxo deve seguir o mesmo padrão definido em cliente, onde é necessário ser definido o service de comunicação com o backend, validação do formulário e envio.

### O que será cadastrado

**Entidade: Marca** — o cadastro registra uma marca no formato de **empresa** (ex.: razão social/nome e **CNPJ**).

A 1ª etapa exige o **cadastro (INSERT)** no banco de dados + o **método de listagem das 10 primeiras ocorrências em ordem alfabética via filtro**. O trabalho completo é um CRUD, então a estrutura já será preparada para as próximas etapas (editar, excluir, listar), mas o foco de entrega agora é **inserir + listar (top 10 alfabético por filtro)**.

---

## 2. Stack e versões

| Parte | Tecnologia | Versão |
|---|---|---|
| Backend — linguagem | Java | 17 |
| Backend — framework | Spring Boot | 4 |
| Backend — persistência | Spring Data JPA + Bean Validation | conforme Boot 4 |
| Banco de dados | PostgreSQL | conexão local (`localhost`) |
| Frontend — runtime | Node.js | 24 |
| Frontend — framework | Angular | **standalone** (sem NgModules porque o professor não explicou a respeito então nao faz sentido ter) |
| Frontend — componentes | PrimeNG | (estilização como dado em aula)|
| Frontend — layout | Bootstrap | (para a responsividade) |

---

## 3. Arquitetura — visão geral

Duas aplicações independentes que se comunicam por HTTP/JSON:

```
  FRONTEND (Angular)                          BACKEND (Spring Boot)
┌───────────────────────────┐    HTTP/JSON   ┌──────────────────────────────┐
│ Formulário de cadastro    │ ─────────────► │ Controller → Service →       │
│ Service de comunicação    │                │ Repository → PostgreSQL      │
│ + validação + envio       │ ◄───────────── │ (resposta JSON)              │
└───────────────────────────┘                └──────────────────────────────┘
```

---

## 4. BACKEND — estrutura e divisão

Arquitetura em camadas. O fluxo deve ser feito de forma completa, na ordem:
`Entidade → Repository → Service → Controller`.

```
HTTP Request
     │
     ▼
┌──────────────┐     
│  Controller  │  recebe a request, orquestra a chamada (não fala com o banco)
├──────────────┤
│   Service    │  regras de negócio e decisões (não conhece HTTP, não fala com o banco direto)
├──────────────┤
│  Repository  │  acesso a dados + o método da regra de listagem (interface Spring Data JPA)
├──────────────┤
│   Entidade   │  mapeamento da tabela (Marca) com JPA
└──────┬───────┘
       ▼
┌──────────────┐
│  POSTGRESQL  │  tabela: marcas
└──────────────┘
```

> Nenhum controller acessa o repository diretamente — toda chamada passa pelo Service.

### 4.1 Pacotes esperados

| Pacote | Responsabilidade |
|---|---|
| `entity`| Mapeamento JPA da entidade Marca |
| `repository` | Interface Spring Data JPA com o método de listagem |
| `service` | Regras de negócio: salvar, listar com filtro |
| `controller` | Rotas REST (POST/GET) |
| `dto` (definir com o grupo) | Objetos de entrada/saída da API, com validações de formato |

### 4.2 Entidade Marca (definição conceitual)

| Campo | Definição | Observação |
|---|---|---|
| `id` | chave primária, auto incremento | gerado pelo banco |
| `nome` (razão social/nome da empresa) | obrigatório, tamanho limitado | usado na ordenação alfabética e no filtro |
| `cnpj` | obrigatório, formato próprio | único — cadastrar uma marca por CNPJ |

A tabela criada será `marcas`.

Posteriormente implementar campos como cep, endereco, rua, mas após a finalização do básico.

### 4.3 Repository — regra obrigatória da etapa

> "Deve ser criado um **método** capaz de listar as **primeiras 10 ocorrências** da entidade desenvolvida **em ordem alfabética** através do **filtro**."

O método deve cumprir, tudo junto:

1. **Filtro** — recebe um texto e retorna apenas registros cujo nome casa com ele (sem diferenciar maiúsculas de minúsculas).
2. **Limite de 10** — retorna no máximo as 10 primeiras ocorrências.
3. **Ordem alfabética** — ordena por nome de A→Z.

Comportamento definido pelo Service: **se o filtro não vier na requisição, a listagem ainda respeita as regras 2 e 3** (10 primeiras ocorrências alfabéticas de tudo).

### 4.4 Responsabilidade de cada camada

| Camada | Faz nesta etapa | Não faz |
|---|---|---|
| **Controller** | Exibe a rota de inserir (`POST /api/marcas`) e a de listar (`GET /api/marcas?filtro=...`); valida o que chega; retorna status corretos | Acesso a dados ou regra de negócio |
| **Service** | Monta entidade e chama o repository para salvar; aplica a regra de lista top 10 alfabético (normaliza o filtro) | Monta JSON/rota |
| **Repository** | `save()` e o método de listagem das 10 primeiras alfabéticas via filtro | Regras, formatos de rota |
| **Entidade** | Mapeia a tabela `marcas` | — |

### 4.5 Rotas previstas

| Método | Rota | Etapa |
|---|---|---|
| `POST` | `/api/marcas` | **1ª etapa — obrigatório** (inserir no banco) |
| `GET` | `/api/marcas?filtro=` | **1ª etapa — obrigatório** (top 10 alfabético via filtro) |
| `PUT` | `/api/marcas/{id}` | próxima etapa (editar) |
| `DELETE` | `/api/marcas/{id}` | próxima etapa (excluir) |
| `GET` | `/api/marcas/todos` | próxima etapa (listar tudo) |

### 4.6 Checklist do time backend

> Um checklist por pessoa (conforme a divisão da seção 9.1). O item só é marcado depois do PR aprovado pelo validador.

#### #2 Infra/Configurador — branch `feature/config-banco-cors` (Fase 0)

- [ ] Criar o banco `cadastro` no PostgreSQL
- [ ] Preencher o `application.properties` (url/usuario/senha, `ddl-auto`, `show-sql`)
- [ ] Habilitar CORS para o frontend (`http://localhost:4200`)
- [ ] README do grupo com o passo a passo de setup (banco + senha própria de cada máquina)
- [ ] `.\mvnw.cmd clean verify` passando
- [ ] Abrir o PR (primeiro merge da fase 0)

#### #3 Entidade — branch `feature/marca-entidade` (Fase 1)

- [ ] Criar o pacote `entity` dentro de `com.grupo08.cadastro`
- [ ] Entidade Marca: `@Entity`, tabela `marcas`, id auto incremento, `nome`, `cnpj`
- [ ] Validações de obrigatório/tamanho (caso o grupo mantenha validação no backend)
- [ ] Getters e setters (ou Lombok — decisão pendente do grupo)
- [ ] `.\mvnw.cmd compile` sem erros + abrir o PR

#### #4 Repository — branch `feature/marca-repository` (Fase 1, após o merge do #3)

- [ ] `MarcaRepository` (interface Spring Data JPA de `Marca`)
- [ ] Método das **10 primeiras ocorrências em ordem alfabética via filtro** (as 3 regras da seção 4.3)
- [ ] Provar o comportamento com filtro e sem filtro (sem filtro = 10 primeiras alfabéticas de tudo)
- [ ] Abrir o PR

#### #5 Service — branch `feature/marca-service` (Fase 1, depois do #4)

- [ ] `salvar`: dados limpos (trim) + montar a entidade + chamar o repository
- [ ] `listar`: filtro nulo → vazio + chamar o método do repository
- [ ] DTOs de entrada/saída (se confirmado em aula)
- [ ] Abrir o PR

#### #6 Controller — branch `feature/marca-controller` (Fase 1, depois do #5)

- [ ] `POST /api/marcas` → `201 Created` | `GET /api/marcas?filtro=` → `200 OK` / `[]`
- [ ] `@Valid` no POST
- [ ] Teste no Postman com prints no PR: insert + listagem com filtro + sem filtro (vazio = `[]`)
- [ ] Abrir o PR

---

## 5. FRONTEND — estrutura e divisão

Segue o **mesmo padrão definido em cliente**: service de comunicação com o backend → validação do formulário → envio. Componentes **standalone** (sem NgModules).

### 5.1 Fluxo do cadastro

```
Usuário preenche o formulário
     │
     ▼
┌───────────────────────────┐
│ 1. Validar                │  COMPONENTE (standalone) — Reactive Forms
│ nome, cnpj                │  valida a regra de cada campo
│ obrigatório, tamanho e
│ formato CNPJ
│
│ inválido → bloqueia envio
│ e mostra erros em tela
├───────────────────────────┤
│ 2. Enviar (form válido)   │  formulário válido → chama o SERVICE
└─────────┬─────────────────┘
          ▼
┌───────────────────────────┐
│ HttpClient → POST         │  SERVICE — único lugar com HttpClient;
│ /api/marcas               │  monta a chamada
└─────────┬─────────────────┘
          ▼
┌───────────────────────────┐
│ Controller → Service →    │  BACKEND — persiste no PostgreSQL
│ Repository → PostgreSQL   │
└───────────────────────────┘
```

### 5.2 Estrutura de pastas (standalone)

| Pasta/arquivo | Responsabilidade |
|---|---|
| `core/models/marca.model.ts` | Interfaces do que trafega na API (tipagem sem `any`) — espelho do DTO do backend |
| `core/services/marca.service.ts` | Comunicação com o backend (listar/inserir) — **apenas ele** usa `HttpClient` |
| `pages/marca/...cadastro.component.ts` | Formulário: Reactive Forms, validações, feedback visual, chama o service |
| `pages/marca/...lista.component.ts` | (se definido em aula) lista o top 10 alfabético consumindo o service |
| `environments/environment*.ts` | URL base da API configurada em um único lugar |
| `app.config.ts / app.routes.ts` | Configuração standalone e rotas |

### 5.3 Responsabilidades definidas

| Item | Definição |
|---|---|
| **Service de comunicação** | Encapsula `HttpClient`: método para inserir (`POST /api/marcas`) e método para listar com o filtro (`GET /api/marcas?filtro=...`). URLs sempre valendo do `environment`. |
| **Validação do formulário** | Reactive Forms com validators: `nome` obrigatório, mínimo/máximo de caracteres; `cnpj` obrigatório (+ validação de formato/máscara — a definir). Formulário **inválido não tem envio** e exibe mensagem por campo. |
| **Envio** | Monta apenas os dados limpos (trim) e envia via service; botão travado enquanto envia; sucesso/erro sinalizados via PrimeNG Toast; formulário reiniciado após sucesso. |
| **Visual** | Layout em grid do **Bootstrap** (`row`/`col`) + inputs/botões/mensagens do **PrimeNG**. |

### 5.4 Regras de comportamento na tela

1. Campos obrigatórios validados no front **e** no backend (dupla validação — o backend reprova mesmo que o front falhe).
2. Strings com trim antes do envio e antes do cadastro no back.
3. Erros do backend nunca são silenciados — mensagem em tela sempre.
4. Componentes **não** chamam `HttpClient` direto — só o service.
5. Toda resposta tipada com interface no `core/models`.

### 5.5 Checklist do time frontend

> Um checklist por pessoa (conforme a divisão da seção 9.1). O item só é marcado depois do PR aprovado pelo validador.

#### #7 Comunicação — branch `feature/front-service` (Fase 0/2 — pode começar em paralelo)

- [ ] Criar `environments/` (`ng generate environments`) + `apiUrl`: `http://localhost:8080/api`
- [ ] `core/models/marca.model.ts` — espelho do contrato da API (seção 6.2)
- [ ] `core/services/marca.service.ts`: inserir + listar com filtro (único lugar com `HttpClient` — já habilitado no `app.config.ts` na fase 0)
- [ ] `ng build` sem erros + abrir o PR

#### #8 Formulário — branch `feature/front-formulario` (Fase 2, após o merge do #7)

- [ ] Componente standalone de cadastro (`pages/marca/...cadastro`)
- [ ] Reactive Forms + validators: `nome` obrigatório/tamanho; `cnpj` obrigatório/formato
- [ ] Envio bloqueado quando inválido + mensagem por campo
- [ ] Toast de sucesso/erro (PrimeNG) + formulário reiniciado após salvar
- [ ] Layout Bootstrap (`row`/`col`) + inputs/botão PrimeNG
- [ ] Teste ponta a ponta com o backend: cadastrar de verdade + conferir o registro no banco — abrir o PR

---

## 6. Regras de comunicação Frontend ↔ Backend (contrato da API)

> Seção oficial de integração entre as duas partes. Qualquer PR que altere o contrato (nome de campo, tipo, rota) exige alinhamento antes do merge.

### 6.1 Portas e URLs

| Item | Valor |
|---|---|
| Backend | `http://localhost:8080` — rotas prefixadas com `/api` |
| Frontend | `http://localhost:4200` (Angular dev server) |
| Banco | PostgreSQL local — banco `cadastro` (nome a confirmar) |

### 6.2 Regras do JSON

1. Nomes de campos em **camelCase idênticos** no Java e no TypeScript (`id`, `nome`, `cnpj`). Nunca divergir.
2. IDs numéricos (`number` no TS, `Long` no Java).
3. Backend sempre responde **JSON** — nunca HTML; frontend nunca monta comando SQL — só consome a API.

### 6.3 Verbos, rotas e códigos HTTP

| Situação | Request do front | Resposta do back |
|---|---|---|
| Cadastro **com** sucesso | `POST /api/marcas` | **201 Created** + objeto criado |
| Cadastro **sem** sucesso (reprovado) | idem | **400 Bad Request** + mensagem no corpo |
| Listagem | `GET /api/marcas?filtro=texto` | **200 OK** + array |
| Filtro sem nenhum casamento | `GET /api/marcas?filtro=xyz` | **200 OK** + `[]` (**nunca** 404) — tela mostra "Nenhum registro" |
| Backend desligado | qualquer chamada | erro de rede → toast de erro genérico no front |

### 6.4 CORS

O backend habilita a origem do frontend (em desenvolvimento `*` é aceitável; alternativa mais estrita: permitir apenas `http://localhost:4200`).

### 6.5 Checklist de integração (rodar os dois juntos)

1. [ ] PostgreSQL rodando com o banco criado.
2. [ ] Backend no ar respondendo em `:8080`.
3. [ ] Frontend no ar em `:4200`.
4. [ ] Teste no navegador: cadastrar marca → toast de sucesso → confirmar no banco/Postman.
5. [ ] Teste do filtro: cadastrar nomes variados → listar por filtro → conferir o top 10 alfabético.

---

## 7. Divisão do trabalho no GitHub

Repositório único no GitHub compartilhado pelo grupo (backend + frontend). Fluxo por membro:

1. Clonar o repositório do grupo.
2. Criar a própria **branch** a partir da `main` (padrão: `feature/<tarefa>`, ex.: `feature/marca-entidade`).
3. Desenvolver **apenas o próprio escopo** (divisão abaixo).
4. Commits pequenos e descritivos (`feat: …`, `fix: …`, existe uma regra, mas se optar por especificar o que foi feito não tem problema).
5. Abrir **Pull Request** para a `main` descrevendo o que foi feito e como testar.
6. O **validador** revisa o PR (padrão e fluxo em camadas, sem quebrar os outros módulos) e aprova o merge. (Ryquelme responsavel)

**Regras:**
- `push` direto na `main` é proibido.
- Antes do PR, sincronizar com a `main` para não conflitar.


### 7.1 Sugestão de divisão de papéis

| Papel | Escopo |
|---|---|
| Backend 1 | Entidade + Repository (regra do top 10 alfabético) |
| Backend 2 | Service + Controller + configuração (banco, CORS, rotas) e Postman p/ teste |
| Frontend 1 | Models + Service de comunicação |
| Frontend 2 | Formulário: validations, envio, mensagens, visual (Bootstrap + PrimeNG) |
| Coordenador/Integração | Revisão de PRs, contrato da API (seção 6), merge final na `main` |

> Com 8 pessoas, os pares de backend/front podem dividir Marca e detalhes extras dos campos; nome será definido no grupo.

### 7.2 Checklist do validador (Ryquelme)

> Papel #1 da seção 9.1 — fase 0 e todo o fluxo de PRs.

- [x] Scaffold do `back/`: Spring Boot 4 + Java 17, `pom.xml` com JPA, Web, Validation e PostgreSQL (feito na fase 0)
- [x] Scaffold do `front/`: Angular standalone + primeng/primeicons/bootstrap (feito na fase 0)
- [x] `provideHttpClient()` no `app.config.ts` (feito na fase 0)
- [X] Commit inicial de `back/` + `front/` (+ documentação) na `main`
- [] Criar o repositório no GitHub + adicionar os integrantes (collaborators)
- [X] Proteger a `main` (require PR) — regra da seção 7
- [ ] Distribuir os papéis da seção 9.1 (nomes de cada integrante)
- [ ] Revisar e mergear os PRs na ordem das fases (seção 9.2)
- [ ] Conferir o checklist de entrega (seção 8) antes de entregar ao professor

---

## 8. Checklist de entrega (1ª etapa)

### Backend

- [X] Projeto Spring Boot 4 + Java 17 (Maven) no repositório.
- [ ] Arquitetura em camadas completa: entidade → repository → service → controller.
- [ ] Repository com o método das **10 primeiras ocorrências em ordem alfabética via filtro**.
- [ ] `POST /api/marcas` inserindo de fato no **PostgreSQL** (positivo confirmado no banco/Postman).
- [ ] `GET /api/marcas?filtro=` retornando o top 10 alfabético.
- [ ] Configuração do banco no projeto (application.properties).
- [ ] Validações de entrada no backend (obrigatório/tamanho).
- [ ] CORS habilitado para o frontend.
- [ ] README do repo backend: configuração e como subir.

### Frontend

- [ ] Projeto Angular **standalone** (sem NgModules) no repositório (Node 24).
- [ ] Model tipado da Marca espelhando o contrato da API.
- [ ] Service de comunicação com o backend definido (único lugar com `HttpClient`).
- [ ] Formulário com validação (obrigatório/tamanho/formato CNPJ) e envio bloqueado quando inválido.
- [ ] Envio real funcionando: cadastra de fato no banco via backend.
- [ ] Visual: Bootstrap (grid) + PrimeNG (inputs, botão, mensagens/toast).
- [ ] README do repo frontend: instalação e como subir.

### Geral

- [ ] Documentação atualizada refletindo o que foi implementado.
- [ ] PRs validados e aprovados na `main` antes da entrega.
- [ ] Links dos repositórios entregues ao professor.
- [ ] Demonstração possível: **cadastrar uma Marca via tela → confirmar no banco → listar com filtro → top 10 alfabético**.

---

## 9. Divisão proposta — 8 pessoas

Proposta pensada para **máximo paralelismo** (ninguém parado) e **mínimo conflito** (cada pessoa toca arquivos diferentes).

### 9.1 Papéis

| # | Papel | Escopo / entregável | Branch sugerida | Depende de |
|---|---|---|---|---|
| **1** | **Ryquelme — Integração/Validação** | Criar o repo no GitHub, proteger a `main`, base dos 2 projetos (backend via Spring Initializr, front via `ng new` no formato standalone), publicar o contrato da seção 6, revisar e aprovar todos os PRs | — (atua na `main`) |  |
| **2** | **Infra/Configurador** | PostgreSQL (criar banco `cadastro`), `application.properties`, habilitar CORS no backend, README do grupo com setup de ambiente | `feature/config-banco-cors` | Lucas |
| **3** | **Backend — Entidade** | Entidade `Marca` mapeada com JPA + Bean Validation (nome obrigatório/tamanho, CNPJ obrigatório) | `feature/marca-entidade` | Ariel |
| **4** | **Backend — Repository** | Interface `MarcaRepository` + o método das **10 primeiras ocorrências alfabéticas via filtro** (núcleo da exigência da etapa) | `feature/marca-repository` | Ryquelme |
| **5** | **Backend — Service** | Regras: salvar (trim, normalizar filtro nulo→vazio), listar top 10; DTOs de entrada/saída | `feature/marca-service` | A decidir |
| **6** | **Backend — Controller** | Rotas `POST /api/marcas` (201) e `GET /api/marcas?filtro=` (200/`[]`), status codes corretos, teste no Postman com prints no PR | `feature/marca-controller` | A decidir |
| **7** | **Frontend — Comunicação** | `marca.model.ts` (espelho do contrato), `marca.service.ts` (HttpClient), `environment.ts` — **trabalha em paralelo**, só o merge espera o controller existir | `feature/front-service` | Luiz |
| **8** | **Frontend — Formulário** | Tela de cadastro: Reactive Forms + validators (obrigatório, tamanho, formato CNPJ), bloqueio de envio inválido, Toast de feedback, layout Bootstrap + PrimeNG | `feature/front-formulario` | Gabriel |

> Os campos extras (cep, endereço, rua — seção 4.2) ficam **fora desta fase**: entram depois que o básico estiver de pé.

### 9.2 Ordem de merges (cronograma em 4 fases)

1. **Fase 0 — base:** #1 cria a estrutura e coloca o grupo para trabalhar → #2 configura banco/CORS (merge 1º).
2. **Fase 1 — backend em cadeia:** #3 → #4 → #5 → #6 (cada PR mergeia antes do próximo abrir, porque um depende do anterior — nesse trecho ninguém trava mais de uma pessoa).
3. **Fase 2 — frontend:** #7 já trabalhou em paralelo desde a Fase 0 (guiado pelo contrato da seção 6), mergeia; #8 entrega a tela.
4. **Fase 3 — integração:** todos juntos rodando os dois lados (checklist 6.5), Ryquelme valida e confirma a entrega da seção 8.

### 9.3 Por que essa divisão

- **Backend em 4 partes, não em par:** a cadeia da seção 4 é sequencial por natureza — 4 entregas curtas em cadeia, cada uma mexendo em arquivos diferentes, dá conflito de merge quase zero; quem terminar mais rápido pode revisar o PR do colega antes do validador.
- **Frontend independente:** #7 e #8 só precisam do **contrato**, não do código backend pronto — por isso ninguém fica ocioso esperando o backend.
- **O validador (#1) nunca escreve feature**, só valida — conforme registrado na seção 7.
- Se um colega do time de backend estiver mais forte, dá para #3+#4 trabalharem **em par (pair programming)** e liberar uma pessoa adiantada para a tela de listagem (top 10), que é o "se definido em aula" da seção 5.2.

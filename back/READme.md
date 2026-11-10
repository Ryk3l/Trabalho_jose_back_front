# Backend — Cadastro de Marcas

API desenvolvida para o trabalho em grupo utilizando Java,
Spring Boot e PostgreSQL.

A primeira etapa contempla o cadastro de marcas e a consulta
das primeiras 10 ocorrências em ordem alfabética, com filtro por nome.

## Tecnologias

- Java 17
- Spring Boot 4
- Maven
- Spring Data JPA
- Bean Validation
- PostgreSQL

## Requisitos

- JDK 17 ou superior.
- PostgreSQL instalado e em execução.
- Git.
- NetBeans ou outra IDE compatível com Maven.

## Clonar o projeto

```powershell
git clone https://github.com/Ryk3l/Trabalho_jose_back_front.git
cd Trabalho_jose_back_front/back
```

No NetBeans, selecione File → Open Project e abra a pasta `back`,
que contém o arquivo `pom.xml`.

## Configuração do banco de dados

O grupo utiliza o banco `cadastro`, com o usuário `postegres`
e a senha padrão combinada entre os integrantes.

No pgAdmin:

1. Conecte-se ao PostgreSQL.
2. Clique com o botão direito em Databases → Create → Database.
3. Informe `cadastro` no campo Database.
4. Selecione o usuário correspondente no campo Owner.
5. Salve.

Se o usuário utilizado por seus projetos pessoais possuir outra senha,
mantenha suas credenciais e ajuste apenas a conexão local do trabalho.

## Configuração da aplicação

O arquivo de configuração fica em:

```text
src/main/resources/application.properties
```

Configure:

```properties
spring.application.name=cadastro

spring.datasource.url=jdbc:postgresql://localhost:5432/cadastro
spring.datasource.username=postegres
spring.datasource.password=SENHA_PADRAO_DO_GRUPO
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Substitua `SENHA_PADRAO_DO_GRUPO` pela senha combinada.

- `ddl-auto=update`: atualiza a estrutura do banco conforme
  as entidades implementadas.
- `show-sql=true`: exibe os comandos SQL no console.

Essas configurações são destinadas ao desenvolvimento local.

## Executar o backend

Com o PostgreSQL ativo e a conexão configurada, execute dentro
da pasta `back`:

```powershell
.\mvnw.cmd spring-boot:run
```

Endereço padrão:

```text
http://localhost:8080
```

A porta 8080 precisa estar disponível.

## Verificar o projeto

Dentro da pasta `back`, execute:

```powershell
.\mvnw.cmd clean verify
```

Esse comando compila o projeto e executa os testes.

O teste de inicialização depende do PostgreSQL ativo e das
credenciais corretas.

Quando a verificação termina com sucesso, o Maven apresenta:

```text
BUILD SUCCESS
```

## CORS

A classe `CorsConfig` está no pacote:

```text
com.grupo08.cadastro.config
```

Ela permite chamadas do frontend Angular para o backend:

- Origem permitida: `http://localhost:4200`
- Rotas abrangidas: `/api/**`
- Métodos permitidos: GET, POST, PUT, DELETE e OPTIONS
- Cabeçalhos permitidos: todos

Reinicie o backend após alterar essa configuração.

## Organização do backend

| Pacote | Responsabilidade |
|---|---|
| `entity` | Mapeamento das entidades e tabelas |
| `repository` | Acesso aos dados |
| `service` | Regras de negócio |
| `controller` | Rotas HTTP |
| `config` | Configurações da aplicação |
| `dto` | Objetos de entrada e saída, conforme definição do grupo |

Fluxo das requisições:

Controller → Service → Repository → PostgreSQL

## Rotas da primeira etapa

| Método | Rota | Finalidade |
|---|---|---|
| POST | `/api/marcas` | Cadastrar uma marca |
| GET | `/api/marcas?filtro=texto` | Consultar até 10 marcas em ordem alfabética |

A listagem deve:

- Filtrar pelo nome sem diferenciar maiúsculas de minúsculas.
- Retornar no máximo 10 registros.
- Ordenar os resultados pelo nome.
- Retornar as primeiras 10 marcas em ordem alfabética quando
  o filtro não for informado.
- Retornar um array vazio quando não houver resultados.

As rotas estão implementadas em `MarcaController`, passando por
`MarcaService` e `MarcaRepository` até a entidade `Marca` (tabela `marcas`).

### Exemplo de cadastro

Envie `POST http://localhost:8080/api/marcas` com `Content-Type: application/json`:

```json
{
  "nome": "Marca Exemplo",
  "cnpj": "12.345.678/0001-95"
}
```

O retorno é `201 Created`, com `id`, `nome` e `cnpj`. O cadastro recebe
e retorna a própria entidade, sem DTOs, validações ou tratamento de erros
personalizado. O CNPJ é salvo como enviado, sem remover a máscara.
As validações de entrada ficam para a parte responsável do grupo.

O mapeamento básico da tabela define nome com até 150 caracteres e CNPJ
com até 18 caracteres, não nulos, mantendo a unicidade do CNPJ no banco.

### Testes das camadas

```powershell
.\mvnw.cmd "-Dtest=MarcaIntegrationTests" test
```

Esses testes usam H2 em memória, sem alterar o PostgreSQL local, e verificam
cadastro e listagem com e sem filtro.
O teste de inicialização original continua dependendo do PostgreSQL.

## Problemas comuns

### Falha de autenticação

Confira o usuário e a senha configurados no `application.properties`.

### Banco inexistente

Confirme se o banco `cadastro` foi criado.

### Conexão recusada

Confira se o PostgreSQL está em execução na porta 5432.

### Porta 8080 ocupada

Verifique se outra aplicação está utilizando a mesma porta.

### Java incompatível

Confira a versão com:

```powershell
java -version
```

Verifique também o JDK selecionado no NetBeans e a variável `JAVA_HOME`.

### Erro de CORS

Confira se o frontend está em `http://localhost:4200`, se a chamada
utiliza uma rota `/api/**` e se o backend foi reiniciado.

## Contribuição

1. Crie uma branch para sua tarefa.
2. Desenvolva apenas o escopo combinado.
3. Faça commits com mensagens descritivas.
4. Envie a branch ao GitHub.
5. Abra um Pull Request para a branch principal `master`.
6. Aguarde a revisão do validador do grupo.

Branch da configuração de banco e CORS:

```text
feature/config-banco-cors
```

Não faça push direto na branch principal.

# Backend da Farmácia

Este é o backend do app da farmácia (Planck Pharma). Ele cuida de tudo o que o app Flutter precisa por trás da tela: criar conta e entrar, mostrar o catálogo, guardar as fotos dos produtos, registrar pedidos e ajudar quem esqueceu a senha.

É uma API REST feita com Spring Boot. Tudo conversa em JSON.

## O que tem por baixo

Java 21 · Spring Boot 4.1.1 · Spring Security com JWT · Spring Data JPA · MySQL · Spring Mail · Lombok · Gradle

## Como o código está organizado

Uma requisição segue sempre o mesmo caminho: **Controller → Service → Repository → MySQL**. O controller recebe e valida, o service aplica as regras, o repository fala com o banco.

```
src/main/java/org/example/backend_farmacia/
├── config/       segurança, filtro JWT, CORS e arquivos estáticos
├── controller/   as rotas da API
├── dto/          o formato do que entra e sai
├── entity/       as tabelas (User, Product, Order, OrderItem...)
├── exception/    erros de negócio e como viram resposta
├── repository/   acesso ao banco
└── service/      as regras de negócio
```

## Rodando

Você precisa do **JDK 21** e de um **MySQL** ligado.

1. Em `src/main/resources/application.properties`, ajuste o usuário e a senha do banco.
2. Rode `./gradlew bootRun` (no Windows, `gradlew.bat bootRun`) ou execute `BackendFarmaciaApplication` pelo IntelliJ.
3. Quando aparecer `Tomcat started on port 8080`, está no ar em `http://localhost:8080`.

O banco `farmacia` e as tabelas são criados sozinhos na primeira vez.

### O que dá para configurar

| Propriedade | Para quê |
|---|---|
| `spring.datasource.*` | Conexão com o MySQL |
| `jwt.secret` | Chave que assina os tokens. Use um texto longo e aleatório |
| `jwt.expiration-ms` | Quanto tempo o login vale (padrão: 24 h) |
| `app.upload-dir` | Pasta das imagens (padrão: `uploads`) |
| `spring.mail.*` e `app.mail.from` | Envio de e-mail (opcional, veja abaixo) |

Dica: guarde senhas e chaves num `secrets.properties` (fora do Git) e importe com `spring.config.import=optional:file:./secrets.properties`. Lembre de colocar `secrets.properties` e `uploads/` no `.gitignore`.

## Criando o primeiro admin

Todo mundo que se cadastra começa como usuário comum. Para virar admin: cadastre a conta pelo app, rode isto no MySQL e entre de novo (o token antigo não sabe da mudança):

```sql
UPDATE farmacia.users SET role = 'ADMIN' WHERE email = 'seu@email.com';
```

## Quem pode o quê

O login devolve um token JWT, que o app manda em `Authorization: Bearer <token>`.

- **Público:** tudo em `/api/auth/**` e as imagens em `/uploads/**`
- **Logado:** ver produtos e fazer pedidos
- **Só admin:** cadastrar produto e enviar imagem

Sem token válido, ou sem permissão, a resposta é `403`.

## Endpoints

| Método | Rota | Quem | O que faz |
|---|---|---|---|
| `POST` | `/api/auth/register` | público | Cria a conta e já devolve o token |
| `POST` | `/api/auth/login` | público | Entra e devolve o token |
| `POST` | `/api/auth/forgot-password` | público | Manda o código de 6 dígitos por e-mail |
| `POST` | `/api/auth/reset-password` | público | Confere o código e troca a senha |
| `GET` | `/api/products` | logado | Lista os produtos |
| `GET` | `/api/products/{id}` | logado | Busca um produto |
| `POST` | `/api/products` | admin | Cadastra um produto |
| `POST` | `/api/products/upload-image` | admin | Envia a foto (campo `file`) e devolve a `url` |
| `POST` | `/api/orders` | logado | Cria um pedido |

Dois exemplos:

```json
// POST /api/auth/login   →   { "token": "...", "username": "Maria", "email": "..." }
{ "email": "maria@email.com", "password": "123456" }

// POST /api/orders   →   devolve o pedido com os itens e o total
{ "items": [ { "productId": 1, "quantity": 2 } ] }
```

No pedido você não manda o usuário: ele vem do token. No cadastro de produto, `promoPrice` é obrigatório mesmo sem promoção (use o mesmo valor de `price`), e a `image` recebe a `url` devolvida pelo upload.

## Regras que valem saber

- **Pedido é tudo ou nada.** Se um item não existe ou não tem estoque, nada é salvo e o estoque dos outros itens fica como estava.
- **O preço fica gravado no item**, então mudar o preço depois não mexe em pedidos antigos.
- **Produto repetido** (mesmo nome na mesma categoria) é recusado.
- **Redefinição de senha:** o código tem 6 dígitos, vale 15 minutos, só o mais recente funciona e erra-se no máximo 5 vezes. A resposta é a mesma exista o e-mail ou não, para não revelar quem tem conta.
- **Imagens:** só JPG, PNG ou WEBP, até 5 MB. O tipo é conferido pelo conteúdo do arquivo, não pelo nome.

Os erros de negócio vêm como `{ "error": "mensagem" }`: `400` para dados inválidos, `403` sem permissão, `404` não encontrado, `409` conflito (e-mail ou produto já existe, estoque insuficiente) e `413` para imagem grande demais.

## E-mail

Sem `spring.mail.host`, nada é enviado: o código de redefinição aparece **no console** do backend, o que já basta para desenvolver.

Para enviar de verdade, o Brevo funciona bem:

```properties
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=SEU_LOGIN_SMTP@smtp-brevo.com
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
app.mail.from=um-remetente-verificado@seudominio.com
```

Três cuidados: no Brevo o `username` é o login do SMTP e não um e-mail, então o `app.mail.from` precisa ser um remetente que você verificou lá; a chave SMTP vai no `secrets.properties`; e no Gmail só funciona senha de app, nunca a senha normal.

Se aparecer `PKIX path building failed`, o Java não confiou no certificado do servidor (comum com antivírus que inspeciona conexões seguras). Para desenvolver, inicie a JVM com `-Djavax.net.ssl.trustStore=NONE -Djavax.net.ssl.trustStoreType=Windows-ROOT`.

## Antes de publicar

- Trocar o `jwt.secret` por uma chave de verdade
- Remover `app.dev-log-code` e qualquer `ssl.trust`
- Tirar as senhas do `application.properties`
- Trocar `ddl-auto=update` por `validate` e usar migrações (Flyway ou Liquibase)
- Restringir o CORS (hoje aceita qualquer `http://localhost:<porta>`) e usar HTTPS

## O que ainda falta

- Listar pedidos (hoje só dá para criar)
- Editar e excluir produtos
- Invalidar tokens antigos quando a senha muda
- Limitar tentativas nas rotas públicas e escrever testes de verdade (existe só o teste padrão)

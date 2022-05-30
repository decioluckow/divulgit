# divulgit

Existem informações valiosas escondidas nos code reviews do seu time!

Um code review naturalmente agrega muito aprendizado para quem está tendo 
seu código revisado. Porém estas lições acabam sendo do conhecimento apenas do revisor e do revisado.

Este processo como um todo gera uma quantidade enorme de comentários e pedidos de
mudança de código. Nem todos são exatamente novidade ou necessariamente interessantes para serem compartilhado.

Então como saber o que é interessante para que seja compartilhado com o time?

A resposta é simples e muito usada hoje em dia nas redes sociais...

`#hashtag!`

Sem trocar a sua atual ferramenta git (GitHub ou GitLab), passe a destacar o que for 
interessante usando: `#divulgar`, `#interessante`, `#legal`, `#cuidado` ou qualquer outra palavra
usando `#`.

## Como utilizar

O DivulGit não está em cloud disponível para uso, você deve compilá-lo e executá-lo 
dentro de sua infra-estrutura (ou onde quiser).

Por isso faça:

```
git clone git@github.com:decioluckow/divulgit.git
mvn clean install
```

Utilize Java 11 no mínimo.

Para executar, obtenha o jar resultante em `divulgit\divulgit-app\target`

E execute da seguinte forma:

```
java -jar divulgit-app.jar
```

É necessário criar um arquivo `application.properties` na mesma pasta do jar, com o seguinte conteúdo:

```
spring.data.mongodb.host=<host>
spring.data.mongodb.database=<database>
spring.data.mongodb.port=<porta>
```

## Trust Store

O Divulgit possui uma trustStore embarcada já com os certificados para o GitLab e GitHub.
Caso seja necessário adicionar ou atualizar algum certificado, adicione 
as seguintes linhas ao arquivo `application.properties`

```
http.client.ssl.trust-store=file:///trustStore.jks
http.client.ssl.trust-store-password=changeit
```

:warning: Estaremos monitoramento o vencimentos do certificados e atualizando no repositório.

## Thread Executor

O processo de scan dos repositórios git é assíncrono e configurado para trabalhar com 
no mínimo 2 e no máximo 10 threads. Para alterar estes valores, adicione as seguintes propriedades
no arquivo `application.properties`. :warning: Atenção! Cautela ao aumentar os valores para não haver abuso
no uso das APIs.

```
tasks.thread-executor.core-pool-size=2
tasks.thread-executor.max-pool-size=10
```


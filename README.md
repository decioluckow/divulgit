# divulgit

Existem informações valiosas escondidas nos code reviews do seu time!

Um code review naturalmente agrega muito aprendizado para quem está tendo 
seu código revisado. Porém estas lições acabam sendo do conhecimento apenas do revisor e do revisado.

Este processo como um todo gera uma quantidade enorme de comentários e pedidos de
mudança de código. Nem todos são exatamente novidade ou necessariamente interessantes para serem compartilhado.

Então como saber o que é interessante para que seja compartilhado com o time?

A resposta é simples e muito usada hoje em dia nas redes sociais...

`#hashtag!`

Sem trocar a sua atual ferramenta git, passe a destacar o que for 
interessante usando: `#divulgar`, `#interessante`, `#legal`, `#cuidado` ou qualquer outra palavra
usando `#`.

Divulgit trabalha com:

![GitHub](divulgit-app/src/main/resources/static/images/github-200px-logo.png)
&nbsp;&nbsp;&nbsp;&nbsp;
![GitLab](divulgit-app/src/main/resources/static/images/gitlab-200px-logo.png)

![Azure DevOps](divulgit-app/src/main/resources/static/images/azure-devops-200px-logo.png)
&nbsp;&nbsp;&nbsp;&nbsp; 
![Bitbucket](divulgit-app/src/main/resources/static/images/bitbucket-200px-logo.png)

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

logging.level.org.divulgit=INFO
logging.file=logs/diVulgit.log
```
## Thread Executor

O processo de scan dos repositórios git é assíncrono e configurado para trabalhar com 
no mínimo 2 e no máximo 10 threads. Para alterar estes valores, adicione as seguintes propriedades
no arquivo `application.properties`. :warning: Atenção! Cautela ao aumentar os valores para não haver abuso
no uso das APIs.

```
tasks.thread-executor.core-pool-size=2
tasks.thread-executor.max-pool-size=10
```


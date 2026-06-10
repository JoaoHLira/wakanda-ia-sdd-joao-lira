## Sobre o Projeto
O WakandaAI é um sistema desenvolvido para automatizar e otimizar os processos internos da Escola Wakanda. 
O projeto busca garantir escalabilidade operacional, mantendo a qualidade dos serviços prestados e ampliando o engajamento 
dos alunos por meio do uso estratégico da inteligência artificial e de tecnologias modernas.  

### Requisitos
- [Git](https://git-scm.com/downloads)
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Lombok](https://projectlombok.org/download)
- [Docker](https://docs.docker.com/engine/install/)
- [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)  

### Instalação
1. No seu workspace, abra o terminal e clone o repositório do projeto:
    ```bash
    git clone https://github.com/tribos-dev/wakanda-ai.git
    ```

2. Navegue até o diretório do projeto:
    ```bash
   cd ./wakanda-ai
    ```

3. Inicie os containers Docker:
   ```bash
    docker compose -f docker-compose.dev.yml up -d
   ```

## Configuração do AWS CLI para LocalStack

1. Antes de inicializar os recursos do LocalStack, configure o perfil `localstack`:

```bash
aws configure --profile localstack
```
2. Informe os valores abaixo:

AWS Access Key ID [None]:`test`  
AWS Secret Access Key [None]: `test`  
Default region name [None]: `us-east-1`  
Default output format [None]: `json`   
 
3. Rode os comandos abaixo:
    ```bash
    cd ./infra/init   
     ```
   - Para Windows:
      
      ```bash
      ./localstack-win-init.cmd
      ``` 
   - Para Linux:
      ```bash
      sudo chmod +x ./localstack-linux-init.sh
      ./localstack-linux-init.sh
      ```

5. Abra o projeto no IDE de sua preferência e execute-o. (Por favor, não o Eclipse 🙏)

6. Acesse a documentação da API em:  
    [http://localhost:8080/wakanda-ai/api/swagger-ui/index.html](http://localhost:8080/wakanda-ai/api/swagger-ui/index.html)

7. Você pode importar os endpoints para o Postman ou Insomnia usando [este arquivo](./src/main/resources/postman-collection/WakandaAI.postman_collection.json), ou acessando:  
[http://localhost:8080/wakanda-ai/api/v3/api-docs](http://localhost:8080/wakanda-ai/api/v3/api-docs)

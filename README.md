# Kanban Api
## Consiste em três serviços, kanban, chat e notification 

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![gitlab](https://img.shields.io/badge/logo-gitlab-blue?logo=gitlab)

### *Voĉe tambem pode ver os arquivos de implantação dessa aplicação no [gitlab](https://gitlab.com/waly1)*

Este repositório contém a documentação das APIs do Kanban, chat-service, notification-service. As APIs permitem gerenciar usuários, times, boards e cards, chat entre usuarios e envio de notificações um sistema que é uma espécie de Kanban.

## Base URL

    https://waly1-kanban-service.api.waly.dev.br

Endpoints
---------

# kanban
### [Visite a documentação do swagger](https://244walyson.github.io/Kanban-services/)   
se estiver rodando localmente pode acessar ao path `/swagger-ui/index.html`*

# Notification
*Para ver os endpoints do serviço de notificação basta colar está linha na barra de pesquisa da pagina do [swagger](https://244walyson.github.io/Kanban-services/) ou acessando localmente no path `/swagger-ui/index.html`*
```sh
https://raw.githubusercontent.com/244Walyson/Kanban-services/swagger/open-api-notification.json
```
## Chat

### Ultiliza websockets junto ao protocolo STOMP (Simple Text Oriented Messaging Protocol)
O protocolo STOMP é um protocolo de mensagens simples e orientado a texto, projetado para interoperabilidade entre diferentes sistemas de mensagens. Ele fornece uma maneira simples de enviar e receber mensagens entre clientes e servidores por meio de uma conexão WebSocket.

**Ao usar o protocolo STOMP:**

  Os clientes podem se conectar a um servidor WebSocket e subscrever a canais específicos (destinos).  
  Os clientes podem enviar e receber mensagens em tempo real por meio desses canais.  
  As mensagens geralmente são formatadas como texto simples, facilitando a leitura e a interpretação.  

## *para o chat temos os seguintes endpoints:*

### `/connect?token=`

abre uma conexão websocket

## Endpoints do Controlador WebSocket (STOMP)
<details>
<summary><h3><code>/chat</code> (STOMP)</h3></summary>

- **Descrição:** Este endpoint recebe e processa mensagens de chat usando o protocolo STOMP.
- **Funcionalidade:**
  - Os clientes podem enviar mensagens de chat para este endpoint e recebê-las em tempo real.
- **Autorização:** Não requer autorização.

</details>

<details>
<summary><h3><code>/chat/{roomId}</code> (STOMP)</summary></h3></h2>

- **Descrição:** Este endpoint recebe e processa mensagens de chat para uma sala de chat específica usando o protocolo STOMP.
- **Parâmetros de entrada:**
  - `chatMessage`: Objeto JSON representando a mensagem de chat a ser processada.
  - `roomId`: Identificador da sala de chat para a qual a mensagem está sendo enviada.
- **Funcionalidade:**
  - Os clientes podem enviar mensagens de chat para uma sala de chat específica e recebê-las em tempo real.
- **Autorização:** Requer autenticação do usuário.

</details>

<details>
<summary><h3><code>/{roomId}/queue/messages</code> (STOMP)</h3></summary>

- **Descrição:** Neste endpoint o cliente pode se conectar para receber as Mensagens das salas de chat que ele esta participando.
- **Parâmetros de entrada:**
  - `chatMessage`: Objeto JSON representando a mensagem de chat a ser processada.
  - `roomId`: Identificador da sala de chat para a qual a mensagem está sendo enviada.
- **Funcionalidade:**
  - Os clientes recebem mensagens de chat das salas que ele participa.
- **Autorização:** Requer autenticação do usuário.

</details>

<details>
<summary><h3> <code>/{nick}/queue/chats</code> (STOMP) </h3></summary>

- **Descrição:** Neste endpoint o cliente pode se conectar para receber as atualizações das salas de chat que ele esta participando.
- **Parâmetros de entrada:**
  - `chatMessage`: Objeto JSON representando a mensagem de chat a ser processada.
  - `roomId`: Identificador da sala de chat para a qual a mensagem está sendo enviada.
- **Funcionalidade:**
  - Os clientes recebem Atualizações de chat das salas que ele participa quando.
- **Autorização:** Requer autenticação do usuário.

</details>

## [Você também pode encontrar a versão mobile do chat clicando aqui.](https://github.com/244Walyson/chat-mobile)

# Rodando a aplicação localmente com docker
```shell
git clone -b local git@github.com:244Walyson/Kanban-services.git
cd Kanban-services
docker-compose up --build -d
```
Após o container kafka Connect subir instale o conector do mysql
```shell
cd Kubernets-Docker-Configs/kafka-connect
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @mysql.json
cd .. && cd ..
```

## URL Base

A URL base para todas as requisições da API é: 
se estiver rodando localmente:
```bash
http://localhost:8080
```


## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## Contribuição

Contribuições são bem-vindas! Por favor, veja as [diretrizes de contribuição](CONTRIBUTING.md) para mais detalhes.


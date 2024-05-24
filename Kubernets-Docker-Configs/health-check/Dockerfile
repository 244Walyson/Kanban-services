# Use a imagem oficial do Golang como base
FROM golang:1.18 AS builder

# Define o diretório de trabalho
WORKDIR /go/src/app

# Copia todos os arquivos do diretório atual para o diretório de trabalho do contêiner
COPY . .

# Compila o aplicativo Go para Linux
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o app .

# Define o comando de execução padrão
CMD ["./app"]

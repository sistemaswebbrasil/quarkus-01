# Projeto de Estudos Quarkus com Kubernetes: quarkus-01

Este projeto é um estudo de Quarkus integrado com Kubernetes, servindo como back-end para a aplicação Vue disponível em [https://escritorio-btun6w1sv-adriano-faria-alves-s-projects.vercel.app/](https://escritorio-btun6w1sv-adriano-faria-alves-s-projects.vercel.app/).

Repositório do projeto: [https://github.com/sistemaswebbrasil/quarkus-01](https://github.com/sistemaswebbrasil/quarkus-01)

## Guia de Implantação e Desenvolvimento do Projeto Quarkus

Este guia fornece instruções detalhadas para compilar, implantar e gerenciar uma aplicação Quarkus no OpenShift, bem como informações importantes para o desenvolvimento.

## Índice
1. [Configuração do Ambiente](#configuração-do-ambiente)
2. [Compilação e Implantação](#compilação-e-implantação)
3. [Configurações do Projeto](#configurações-do-projeto)
4. [Desenvolvimento](#desenvolvimento)
5. [Gerenciamento de Cluster](#gerenciamento-de-cluster)
6. [Recursos Adicionais](#recursos-adicionais)

## Configuração do Ambiente

### Pré-requisitos
- JDK 21
- Maven 3.8.1+
- Docker
- OpenShift CLI (oc)

### Configuração do Banco de Dados
O projeto utiliza MySQL. Certifique-se de ter um servidor MySQL disponível e atualize as configurações no `application.properties` conforme necessário.

## Compilação e Implantação

### Gerar Nova Imagem

Para construir uma nova imagem, enviá-la para o Docker Hub e atualizar a implantação no OpenShift:

```bash
mvn versions:set -DnewVersion=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)-SNAPSHOT && \
mvn package && \
docker build -f Dockerfile.jvm -t adrianofariaalves/quarkus-01-jvm:latest . && \
docker push adrianofariaalves/quarkus-01-jvm:latest && \
oc set image deployment/quarkus-01-deploy quarkus-01=adrianofariaalves/quarkus-01-jvm:latest && \
oc rollout restart deployment/quarkus-01-deploy
```

### Implantação Direta no OpenShift

Para implantação direta no OpenShift:

```bash
mvn versions:set -DnewVersion=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)-SNAPSHOT && \
mvn install -Dquarkus.openshift.deploy=true
```

### Compilar e Implantar Aplicação

Para compilar a aplicação, gerar uma imagem Docker, enviá-la e atualizar o OpenShift:

```bash
mvn clean package -Dquarkus.container-image.build=true -Dquarkus.container-image.push=true -Dquarkus.kubernetes.deploy=true
```

### Criando Implantação

Para criar uma nova implantação no OpenShift:

```bash
mvn versions:set -DnewVersion=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)-SNAPSHOT && \
mvn package && \
docker build -f Dockerfile.jvm -t adrianofariaalves/quarkus-01-jvm:latest . && \
docker push adrianofariaalves/quarkus-01-jvm:latest && \
cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: quarkus-01-deploy
spec:
  replicas: 1
  selector:
    matchLabels:
      app: quarkus-01
  template:
    metadata:
      labels:
        app: quarkus-01
        env: dev
    spec:
      containers:
      - name: quarkus-01
        image: adrianofariaalves/quarkus-01-jvm:latest
EOF
```

## Configurações do Projeto

### Propriedades Principais
- **Versão do Quarkus**: 3.14.4
- **Versão do Java**: 21
- **Versão do Lombok**: 1.18.30

### Dependências Principais
- Quarkus REST
- Hibernate ORM com Panache
- MySQL Driver
- OpenShift
- Swagger UI

### Configurações de Banco de Dados
```properties
quarkus.datasource.db-kind=mysql
quarkus.datasource.jdbc.url=jdbc:mysql://${db_host:localhost}:3306/${db_name:quarkus-01}?useSSL=false&serverTimezone=UTC
quarkus.datasource.username=${db-username:adriano}
quarkus.datasource.password=${db_password:adriano}
```

### Configurações de CORS
O CORS está habilitado para desenvolvimento local na porta 5173:
```properties
quarkus.http.cors=true
quarkus.http.cors.origins=http://localhost:5173
quarkus.http.cors.methods=GET,PUT,POST,DELETE,PATCH,OPTIONS
quarkus.http.cors.headers=accept,authorization,content-type,x-requested-with
```

## Desenvolvimento

### Executando a Aplicação em Modo de Desenvolvimento
```bash
./mvnw compile quarkus:dev
```

### Acessando o Swagger UI
O Swagger UI está sempre disponível em desenvolvimento:
```
http://localhost:8080/q/swagger-ui
```

### Formatação de Data e Hora
As datas são formatadas como `yyyy-MM-dd HH:mm:ss` e o fuso horário padrão é UTC.

## Gerenciamento de Cluster

### Escalonamento do Cluster

#### Pré-requisitos
- Login no OpenShift Cluster Manager
- Cluster OpenShift Dedicated criado

#### Procedimento
1. Para escalar balanceadores de carga ou armazenamento persistente:
   - Navegue até o OpenShift Cluster Manager e selecione seu cluster
   - Selecione "Editar balanceadores de carga e armazenamento persistente" no menu de ações
   - Escolha o número de balanceadores de carga e capacidade de armazenamento
   - Clique em "Aplicar"

2. Para escalar a contagem de nós:
   - Selecione "Editar contagem de nós" no menu de ações
   - Escolha um pool de máquinas e defina a contagem de nós por zona
   - Clique em "Aplicar"

#### Verificação
Revise a configuração na aba "Visão geral" sob "Detalhes".

### Revogação de Privilégios de Administrador

#### Pré-requisitos
- Login no OpenShift Cluster Manager
- Cluster OpenShift Dedicated criado
- Provedor de identidade GitHub configurado e usuário adicionado
- Privilégios de administrador dedicado concedidos a um usuário

#### Procedimento
1. Navegue até o OpenShift Cluster Manager e selecione seu cluster
2. Clique na aba "Controle de acesso"
3. Em "Funções e Acesso do Cluster", selecione o menu kebab ao lado de um usuário e clique em "Excluir"

#### Verificação
O usuário não deve mais estar listado no grupo "dedicated-admins".

### Revogação de Acesso de Usuário

#### Pré-requisitos
- Cluster OpenShift Dedicated
- Conta de usuário GitHub
- Provedor de identidade GitHub configurado para seu cluster

#### Procedimento
1. Faça login em sua conta do GitHub
2. Remova o usuário de sua organização ou equipe do GitHub conforme apropriado

#### Verificação
O usuário não deve mais conseguir autenticar-se no cluster.

### Exclusão de Cluster

#### Pré-requisitos
- Login no OpenShift Cluster Manager
- Cluster OpenShift Dedicated criado

#### Procedimento
1. No OpenShift Cluster Manager, clique no cluster que deseja excluir
2. Selecione "Excluir cluster" no menu de ações
3. Digite o nome do cluster e clique em "Excluir"

#### Observação
Se estiver excluindo um cluster instalado em um GCP Shared VPC, informe o proprietário do VPC do projeto host para remover as funções de política IAM concedidas à conta de serviço referenciada durante a criação do cluster.

## Recursos Adicionais
- [Documentação do Quarkus](https://quarkus.io/guides/)
- [Quarkus Cheat Sheet](https://lordofthejars.github.io/quarkus-cheat-sheet/)
- [Hibernate ORM com Panache](https://quarkus.io/guides/hibernate-orm-panache)
- [Implantação no OpenShift](https://quarkus.io/guides/deploying-to-openshift)
- [Configurando CORS](https://quarkus.io/guides/http-reference#cors-filter)
- [Ciclo de vida de atualização do OpenShift Dedicated](https://access.redhat.com/documentation/en-us/openshift_dedicated/4/html/introduction_to_openshift_dedicated/ded-life-cycle)
- [Criando um cluster na AWS](https://access.redhat.com/documentation/en-us/openshift_dedicated/4/html/installing_accessing_and_deleting_openshift_dedicated_clusters/creating-a-cluster-on-aws)
- [Criando um cluster no GCP](https://access.redhat.com/documentation/en-us/openshift_dedicated/4/html/installing_accessing_and_deleting_openshift_dedicated_clusters/creating-a-cluster-on-gcp)
- [Atualizações de cluster do OpenShift Dedicated](https://access.redhat.com/documentation/en-us/openshift_dedicated/4/html/upgrading_openshift_dedicated_clusters/index)
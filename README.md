## gera imagem nova
```bash
mvn versions:set -DnextSnapshot=true && mvn package && docker build -f src/main/docker/Dockerfile.jvm -t adrianofariaalves/quarkus-01-jvm:latest . && docker push adrianofariaalves/quarkus-01-jvm:latest && oc set image deployment/quarkus-01-deploy quarkus-01=adrianofariaalves/quarkus-01-jvm:latest && oc rollout restart deployment/quarkus-01-deploy
```

## deploy direto openshift
```bash
mvn versions:set -DnextSnapshot=true && mvn install -Dquarkus.openshift.deploy=true
```



### Build e Deploy da Aplicação

Para construir a aplicação, gerar a imagem Docker, fazer o push e atualizar no OpenShift, execute o seguinte comando:

```bash
mvn clean package -Dquarkus.container-image.build=true -Dquarkus.container-image.push=true -Dquarkus.kubernetes.deploy=true
```

## criando deployment??
```bash
mvn versions:set -DnextSnapshot=true && mvn package && docker build -f src/main/docker/Dockerfile.jvm -t adrianofariaalves/quarkus-01-jvm:latest . && docker push adrianofariaalves/quarkus-01-jvm:latest && cat <<EOF | kubectl apply -f -
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

## Escalonamento do Cluster
Você pode escalonar o número de balanceadores de carga, a capacidade de armazenamento persistente e a contagem de nós para o seu cluster OpenShift Dedicated a partir do OpenShift Cluster Manager.

### Pré-requisitos
- Você fez login no OpenShift Cluster Manager.
- Você criou um cluster OpenShift Dedicated.

### Procedimento
Para escalonar o número de balanceadores de carga ou a capacidade de armazenamento persistente:
1. Navegue até o OpenShift Cluster Manager e selecione seu cluster.
2. Selecione "Edit load balancers and persistent storage" no menu de ações.
3. Selecione o número de balanceadores de carga que deseja escalonar.
4. Selecione a capacidade de armazenamento persistente que deseja escalonar.
5. Clique em "Apply". O escalonamento ocorre automaticamente.

Para escalonar a contagem de nós:
1. Navegue até o OpenShift Cluster Manager e selecione seu cluster.
2. Selecione "Edit node count" no menu de ações.
3. Selecione um pool de máquinas.
4. Selecione a contagem de nós por zona.
5. Clique em "Apply". O escalonamento ocorre automaticamente.

### Verificação
Na aba "Overview" sob o cabeçalho "Details", você pode revisar a configuração do balanceador de carga, detalhes de armazenamento persistente e contagens de nós atual e desejada.

### Recursos Adicionais
- Para informações sobre pools de máquinas, veja "About machine pools".
- Para passos detalhados para habilitar a escalonamento automático para nós de computação no seu cluster, veja "About autoscaling nodes on a cluster".

## Revogação de Privilégios de Administrador
Siga os passos nesta seção para revogar privilégios de administrador dedicado de um usuário.

### Pré-requisitos
- Você fez login no OpenShift Cluster Manager.
- Você criou um cluster OpenShift Dedicated.
- Você configurou um provedor de identidade GitHub para seu cluster e adicionou um usuário do provedor de identidade.
- Você concedeu privilégios de administrador dedicado a um usuário.

### Procedimento
1. Navegue até o OpenShift Cluster Manager e selecione seu cluster.
2. Clique na aba "Access control".
3. Na aba "Cluster Roles and Access", selecione o kebab ao lado de um usuário e clique em "Delete".

### Verificação
Após revogar os privilégios, o usuário não está mais listado como parte do grupo "dedicated-admins" sob "Access control → Cluster Roles and Access" na página do OpenShift Cluster Manager para seu cluster.

## Revogação de Acesso de Usuário
Você pode revogar o acesso ao cluster de um usuário do provedor de identidade removendo-o do seu provedor de identidade configurado.

### Pré-requisitos
- Você tem um cluster OpenShift Dedicated.
- Você tem uma conta de usuário GitHub.
- Você configurou um provedor de identidade GitHub para seu cluster e adicionou um usuário do provedor de identidade.

### Procedimento
1. Navegue até github.com e faça login na sua conta do GitHub.
2. Remova o usuário da sua organização ou equipe do GitHub:
   - Se sua configuração de provedor de identidade usa uma organização GitHub, siga os passos em "Removing a member from your organization" na documentação do GitHub.
   - Se sua configuração de provedor de identidade usa uma equipe dentro de uma organização GitHub, siga os passos em "Removing organization members from a team" na documentação do GitHub.

### Verificação
Após remover o usuário do seu provedor de identidade, o usuário não pode mais autenticar no cluster.

## Exclusão de Cluster
Você pode excluir seu cluster OpenShift Dedicated no Red Hat OpenShift Cluster Manager.

### Pré-requisitos
- Você fez login no OpenShift Cluster Manager.
- Você criou um cluster OpenShift Dedicated.

### Procedimento
1. No OpenShift Cluster Manager, clique no cluster que deseja excluir.
2. Selecione "Delete cluster" no menu de ações.
3. Digite o nome do cluster destacado em negrito e clique em "Delete". A exclusão do cluster ocorre automaticamente.

Se você excluir um cluster que foi instalado em um GCP Shared VPC, informe o proprietário do VPC do projeto host para remover as funções de política IAM concedidas à conta de serviço que foi referenciada durante a criação do cluster.

### Próximos Passos
- Adicionando serviços a um cluster usando o console do OpenShift Cluster Manager
- Sobre pools de máquinas
- Sobre escalonamento automático de nós em um cluster
- Configurando a pilha de monitoramento

### Recursos Adicionais
- Para informações sobre as datas de fim de vida das versões do OpenShift Dedicated, veja "OpenShift Dedicated update life cycle".
- Para mais informações sobre a implantação de clusters OpenShift Dedicated, veja "Creating a cluster on AWS" e "Creating a cluster on GCP".
- Para documentação sobre a atualização do seu cluster, veja "OpenShift Dedicated cluster upgrades".
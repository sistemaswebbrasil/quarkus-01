# quarkus-01

## Gerar imagem 
```bash
docker build -f src/main/docker/Dockerfile.jvm -t adrianofariaalves/quarkus-01-jvm:latest .
```

## Push imagem
```bash
docker push adrianofariaalves/quarkus-01-jvm:latest
```

## Criando o pod?
```bash
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: Pod
metadata:
  name: quarkus-01
spec:
  containers:
  - name: quarkus-01
    image: adrianofariaalves/quarkus-01-jvm:latest
EOF
```

## criando replica set?
```bash
cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: ReplicaSet
metadata:
    name: quarkus-01-rs
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
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
        env:
          - name: QUARKUS_DATASOURCE_DB_KIND
            value: h2
          - name: QUARKUS_DATASOURCE_JDBC_URL
            value: 'jdbc:h2:mem:test;DB_CLOSE_DELAY=-1'
          - name: QUARKUS_DATASOURCE_USERNAME
            value: sa
          - name: QUARKUS_DATASOURCE_PASSWORD
            value: sa
          - name: QUARKUS_DATASOURCE_JDBC_DRIVER
            value: org.h2.Driver
          - name: QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION
            value: drop-and-create
          - name: QUARKUS_HIBERNATE_ORM_LOG_SQL
            value: 'true'
EOF
```

## expor service

```bash
kubectl expose deployment quarkus-01-deploy --port=8080 
oc expose svc quarkus-01-deploy
```

endereço no openshift:
http://quarkus-01-deploy-sistemaswebbrasil-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/hello
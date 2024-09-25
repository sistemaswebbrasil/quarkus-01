# quarkus-01

## Gerar imagem 
```bash
docker build -f src/main/docker/Dockerfile.jvm -t adrianofariaalves/quarkus-01-jvm .
```

## Push imagem
```bash
docker push adrianofariaalves/quarkus-01-jvm
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
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
EOF
```

## expor service

```bash
kubectl expose deployment quarkus-01-deploy --port=8080 
oc expose svc quarkus-01-deploy
```
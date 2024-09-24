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

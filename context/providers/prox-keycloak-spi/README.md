# Build
```shell
mvn clean package
```

# Deploy
```shell
export KEYCLOAK_HOME=/tmp/keycloak
cp ./target/prox-keycloak-spi-13.0.0.jar ${KEYCLOAK_HOME}/standalone/deployments
```
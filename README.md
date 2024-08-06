
# Redis y Spring Boot

- Para levantar la aplicación devemos primero levantar la imagen de redis asi que usamos el docker compose, asegurese de estar en la raiz del proyecto.

```
docker compose up -d
```
- Para poder conectarse a redis de forma grafica recomiendo usar esta herramienta [AnotherRedisDesktopManager](https://github.com/qishibo/AnotherRedisDesktopManager/releases) e instala para tu sistema operativo.

- Luego simplememnte levantamos el proyecto con maven
```
mvn spring-boot:run
```

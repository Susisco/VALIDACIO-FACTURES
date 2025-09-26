# Deploy Backend Spring Boot a Fly.io

## Resumen de pasos para hacer deploy

### 1. Instalación y configuración inicial
```bash
# Instalar Fly CLI (si no está instalado)
# Descargar de: https://fly.io/docs/getting-started/installing-flyctl/

# Autenticarse en Fly.io
fly auth login
```

### 2. Configuración del proyecto

#### Archivo `fly.toml` (ya configurado)
```toml
app = 'validacio-backend'
primary_region = 'cdg'

[build]

[http_service]
  internal_port = 8080
  force_https = true
  auto_stop_machines = true
  auto_start_machines = false
  min_machines_running = 0
  processes = ['app']

[[vm]]
  memory = '1gb'
  cpu_kind = 'shared'
  cpus = 1

[deploy]
  strategy = 'rolling'
```

#### Configuración Spring Boot para Docker (`application-prod.properties`)
```properties
# Configuración para que la app escuche en todas las interfaces
server.address=0.0.0.0
server.port=8080

# Variables de entorno para la base de datos
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}  
spring.datasource.password=${DATABASE_PASSWORD}
```

#### Dockerfile (modificado para producción)
```dockerfile
FROM maven:3.9.5-eclipse-temurin-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Dserver.address=0.0.0.0", "-Dserver.port=8080", "-jar", "app.jar"]
```

### 3. Comandos de deploy

#### Deploy inicial
```bash
cd backend
fly launch
# Seguir las instrucciones, elegir región (cdg para Europa)
```

#### Deploy posteriores (actualizaciones)
```bash
cd backend
fly deploy
```

### 4. Comandos útiles para monitorización

```bash
# Ver estado de la aplicación
fly status

# Ver logs de la aplicación
fly logs

# Ver aplicaciones desplegadas
fly apps list

# Abrir la aplicación en el navegador
fly open
```

### 5. Variables de entorno

```bash
# Configurar variables de entorno (base de datos, etc.)
fly secrets set DATABASE_URL="jdbc:mariadb://host:port/database"
fly secrets set DATABASE_USERNAME="usuario"
fly secrets set DATABASE_PASSWORD="contraseña"

# Ver variables configuradas
fly secrets list
```

### 6. URL de la aplicación

Una vez desplegada, la aplicación estará disponible en:
```
https://validacio-backend.fly.dev/
```

## Notas importantes

- **Costos**: Con la configuración actual (`auto_stop_machines = true`), las máquinas se detienen automáticamente cuando no hay tráfico, minimizando costos.

- **Actualizaciones**: Cada vez que hagas cambios en el código, debes ejecutar `fly deploy` desde la carpeta `backend`.

- **Logs**: Si hay problemas, siempre revisa los logs con `fly logs` para diagnosticar errores.

- **Base de datos**: Asegúrate de que las variables de entorno de la base de datos están correctamente configuradas antes del primer deploy.

## Solución de problemas comunes

### App no accesible (0.0.0.0:8080)
- Verificar que `server.address=0.0.0.0` está en `application-prod.properties`
- Verificar que el Dockerfile tiene los parámetros `-Dserver.address=0.0.0.0 -Dserver.port=8080`
- Comprobar que el perfil `prod` se está activando correctamente

### Deploy falla
- Revisar logs con `fly logs`
- Verificar que no hay errores de compilación en Maven
- Comprobar que todas las variables de entorno están configuradas
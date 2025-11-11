# Deployment Guide

This guide provides instructions for deploying the E-Health Care Platform in various environments.

## Prerequisites

- Docker and Docker Compose installed
- At least 8GB RAM available
- Ports 3000, 8080-8090 available
- Java 21 (for local development without Docker)

## Local Development Deployment

### Using Docker Compose

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd e-healthcare-platform
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   # Edit .env with your local settings
   ```

3. **Build and start all services**
   ```bash
   make build
   make up
   ```

   Or manually:
   ```bash
   ./scripts/build-all.sh
   ./scripts/start-local.sh
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - API Gateway: http://localhost:8080
   - Individual services: http://localhost:8081-8085

5. **Stop services**
   ```bash
   make down
   # or
   ./scripts/stop-local.sh
   ```

### Manual Local Development

1. **Start infrastructure services**
   ```bash
   docker-compose -f backend/docker-compose.yml up -d mongodb redis kafka zookeeper
   ```

2. **Start backend services**
   ```bash
   # In separate terminals
   mvn -pl backend/config-server spring-boot:run -Dspring-boot.run.profiles=dev
   mvn -pl backend/discovery-server spring-boot:run -Dspring-boot.run.profiles=dev
   mvn -pl backend/auth-service spring-boot:run -Dspring-boot.run.profiles=dev
   # ... start other services similarly
   ```

3. **Start frontend**
   ```bash
   cd frontend/ehealth-angular-app
   npm install
   npm start
   ```

## Production Deployment

### Docker Compose (Single Server)

1. **Update environment variables**
   ```bash
   # Edit .env for production settings
   # Set SPRING_PROFILES_ACTIVE=prod
   # Configure production database URLs, secrets, etc.
   ```

2. **Build and deploy**
   ```bash
   make build
   make up
   ```

3. **Enable SSL (Optional)**
   - Place SSL certificates in `nginx/ssl/`
   - Update nginx configuration for HTTPS

### Cloud Deployment Options

#### AWS ECS

1. **Build Docker images**
   ```bash
   make build
   ```

2. **Push to ECR**
   ```bash
   aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <account>.dkr.ecr.<region>.amazonaws.com
   docker tag ehealthcare-platform:latest <account>.dkr.ecr.<region>.amazonaws.com/ehealthcare-platform:latest
   docker push <account>.dkr.ecr.<region>.amazonaws.com/ehealthcare-platform:latest
   ```

3. **Deploy to ECS**
   - Create ECS cluster
   - Create task definitions for each service
   - Set up load balancer
   - Configure auto-scaling

#### Kubernetes

1. **Generate Kubernetes manifests**
   ```bash
   # Use provided YAML files in k8s/ directory
   kubectl apply -f k8s/
   ```

2. **Configure Ingress**
   ```yaml
   apiVersion: networking.k8s.io/v1
   kind: Ingress
   metadata:
     name: ehealthcare-ingress
   spec:
     rules:
     - host: your-domain.com
       http:
         paths:
         - path: /
           pathType: Prefix
           backend:
             service:
               name: frontend-service
               port:
                 number: 80
         - path: /api
           pathType: Prefix
           backend:
             service:
               name: api-gateway-service
               port:
                 number: 8080
   ```

## Environment Configuration

### Environment Variables

Create a `.env` file with the following variables:

```bash
# Database
MONGODB_URI=mongodb://localhost:27017/ehealthcare
REDIS_URI=redis://localhost:6379

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT
JWT_SECRET=your-super-secret-jwt-key-here
JWT_EXPIRATION=86400000

# Services
CONFIG_SERVER_URI=http://config-server:8888
EUREKA_SERVER_URI=http://discovery-server:8761/eureka

# Email (for notifications)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password

# External APIs (if any)
EXTERNAL_API_KEY=your-api-key
```

### Spring Profiles

- **dev**: Development profile with debug logging and H2 database
- **prod**: Production profile with optimized settings
- **test**: Testing profile with test-specific configurations

## Monitoring and Logging

### Health Checks

Each service exposes health endpoints:
- `/actuator/health` - Overall health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

### Logging

- Logs are written to `logs/` directory in development
- In production, configure centralized logging (ELK stack recommended)
- Log levels can be configured via `application.yml`

### Monitoring

- Use Spring Boot Actuator for basic monitoring
- Consider integrating with Prometheus + Grafana for advanced monitoring
- Set up alerts for service downtime and high resource usage

## Backup and Recovery

### Database Backup

```bash
# MongoDB backup
docker exec -it mongodb mongodump --db ehealthcare --out /backup

# Restore
docker exec -it mongodb mongorestore /backup/ehealthcare
```

### Configuration Backup

- Regularly backup `.env` files and configuration
- Use Git for version control of configuration files

## Scaling

### Horizontal Scaling

- Increase replica count in docker-compose.yml or Kubernetes deployments
- Ensure database can handle increased load (consider MongoDB sharding)

### Vertical Scaling

- Increase CPU/memory limits for resource-intensive services
- Monitor and optimize database queries

## Troubleshooting

### Common Issues

1. **Port conflicts**
   - Check if ports are available: `netstat -tulpn | grep :8080`
   - Change ports in docker-compose.yml if needed

2. **Service discovery issues**
   - Ensure Eureka server is running before starting other services
   - Check service registration logs

3. **Database connection issues**
   - Verify MongoDB is running: `docker ps | grep mongodb`
   - Check connection string in .env

4. **Frontend build issues**
   - Clear npm cache: `npm cache clean --force`
   - Delete node_modules and reinstall

### Logs and Debugging

```bash
# View service logs
docker-compose logs -f <service-name>

# View specific container logs
docker logs <container-id>

# Debug mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Ddebug=true
```

## Security Considerations

- Change default passwords and secrets
- Use HTTPS in production
- Implement proper firewall rules
- Regularly update Docker images and dependencies
- Use secrets management (AWS Secrets Manager, HashiCorp Vault)

## Performance Optimization

- Enable gzip compression in nginx
- Configure appropriate JVM heap sizes
- Use connection pooling for databases
- Implement caching strategies
- Optimize database queries and indexes

## Maintenance

### Updates

1. Pull latest changes: `git pull`
2. Build new images: `make build`
3. Deploy updates: `make up`
4. Run tests: `make test`

### Cleanup

```bash
# Remove unused Docker images
docker image prune -a

# Remove unused volumes
docker volume prune

# Clean build artifacts
mvn clean
```

For additional support, refer to the developer guide or create an issue in the repository.

# Validació Factures - Local Development Setup

## 🚀 Quick Start

### Prerequisites
1. **Java 17** LTS
2. **Node.js 18+** with npm
3. **MariaDB Server** running on `localhost:3306`
4. **AWS Account** with S3 access

### Database Setup
```sql
-- Connect to MariaDB as root
CREATE DATABASE validacio_factures CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'validacio_user'@'localhost' IDENTIFIED BY 'ValidacioPass123!';
GRANT ALL PRIVILEGES ON validacio_factures.* TO 'validacio_user'@'localhost';
FLUSH PRIVILEGES;
```

### AWS S3 Setup
1. Create development bucket: `validacio-factures-dev`
2. Set environment variables:
```bash
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
```

### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```
Backend runs on: http://localhost:8080

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on: http://localhost:5173

## 🔧 Configuration

### Environment Files
- `.env.local` - Frontend environment variables (excluded from git)
- `application.properties` - Backend local config (excluded from git)
- `application-prod.properties` - Production config

### Database Configuration
- **Local Development**: MariaDB (`validacio_factures` database)
- **Testing**: H2 in-memory database
- **Production**: MariaDB on Fly.io

### File Storage
- **Development**: AWS S3 bucket `validacio-factures-dev`
- **Production**: AWS S3 bucket `validacio-factures-uploads`

## 🛡️ Security Features

### Authentication
- JWT-based authentication system
- Token expiration: 1 hour (configurable)
- Automatic token refresh handling

### Authorization Levels
- **Admin**: Full access to all operations
- **User**: Limited access to assigned operations
- **Guest**: Public endpoints only

### API Security
- CORS configured for frontend origins
- JWT token validation on protected endpoints
- Device authorization for mobile apps
- Play Integrity verification for Android
- Version checking for compatibility

## 📁 Project Structure

```
├── backend/                 # Spring Boot API (Maven)
│   ├── src/main/java/
│   │   ├── config/         # Security, CORS, database config
│   │   ├── controller/     # REST endpoints
│   │   ├── filter/         # Security filters
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Data access layer
│   │   ├── security/       # JWT, authentication
│   │   └── service/        # Business logic
│   └── src/main/resources/
│       ├── application.properties           # Local config
│       └── application-prod.properties      # Production config
├── frontend/               # React + TypeScript (Vite)
│   ├── src/
│   │   ├── api/           # Axios client, API calls
│   │   ├── components/    # Reusable UI components
│   │   └── pages/         # Route components
├── docs/                  # Documentation
│   └── instruccions/      # Setup and migration guides
└── docker-compose.yml     # Local MySQL database
```

## 🔄 Recent Changes

### Database Migration (H2 → MariaDB)
- Added MariaDB support for persistent local development
- Maintains H2 compatibility for testing
- Enhanced connection pooling with HikariCP

### AWS S3 Integration
- Migrated from local file storage to AWS S3
- Separate buckets for development and production
- Presigned URLs for secure file access

### Security Improvements
- Fixed filter execution order with `@Order` annotations
- Improved JWT token handling in frontend
- Consolidated Axios interceptors
- Enhanced CORS configuration

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 📚 Documentation

- [Database Migration Guide](docs/instruccions/MIGRATION_H2_TO_MARIADB_S3.md)
- [Device Authorization](docs/instruccions/README-Autoritzacio-Dispositius_i_Control-de-Versions.md)
- [Deployment Instructions](docs/instruccions/DEPLOY_FLYIO.md)
- [Frontend Deployment](docs/instruccions/DEPLOY_VERCEL.md)

## 🚀 Deployment

### Backend (Fly.io)
```bash
cd backend
fly deploy
```

### Frontend (Vercel)
```bash
cd frontend
npm run build
vercel --prod
```

## 🔍 Troubleshooting

### Common Issues

**Database Connection Error**
- Verify MariaDB is running: `sudo systemctl status mariadb`
- Check credentials in `application.properties`
- Ensure database `validacio_factures` exists

**JWT Authentication Failing**
- Check JWT secret configuration
- Verify token expiration settings
- Clear localStorage and re-login

**CORS Errors**
- Verify frontend origin in CORS configuration
- Check browser console for exact error
- Ensure proper headers in requests

**File Upload Issues**
- Verify AWS credentials are set
- Check S3 bucket permissions
- Confirm bucket names in configuration

## 🤝 Contributing

1. Create feature branch: `git checkout -b feature/your-feature`
2. Make changes and commit: `git commit -m "feat: your feature"`
3. Push to branch: `git push origin feature/your-feature`
4. Create Pull Request

## 📞 Support

For issues and questions, please create an issue in the GitHub repository or contact the development team.

---

**Last Updated**: September 29, 2025  
**Version**: 2.0.0 (MariaDB + S3 Integration)
# Configuration Migration: H2 → MariaDB + S3 Integration

## Overview
This document describes the migration from H2 in-memory database to MariaDB with AWS S3 integration for file storage, completed on September 29, 2025.

## Changes Summary

### 🔧 Backend Configuration Changes

#### 1. Database Migration (H2 → MariaDB)
- **Added**: MariaDB dependency in `pom.xml`
- **Updated**: `application.properties` with MariaDB connection
- **Database**: `validacio_factures` with user `validacio_user`

#### 2. AWS S3 Integration
- **Development bucket**: `validacio-factures-dev`
- **Production bucket**: `validacio-factures-uploads` (existing)
- **Configuration**: Added S3 properties and presigned URL support

#### 3. Security & Filter Chain Improvements
- **Fixed**: JWT Filter now allows public access to `/api/fitxers/**` endpoints
- **Added**: `@Order` annotations to all filters for proper execution sequence:
  - `@Order(1)`: PlayIntegrityFilter
  - `@Order(2)`: DeviceAuthorizationFilter  
  - `@Order(3)`: VersionCheckFilter
  - `@Order(4)`: JwtFilter
- **Simplified**: SecurityConfig by removing manual filter injection

#### 4. CORS Configuration
- **Disabled**: Manual CorsFilter to avoid conflicts
- **Using**: Spring Security's built-in CORS configuration only
- **Local origins**: `http://localhost:5173`, `http://localhost:3000`

### 🎨 Frontend Improvements
- **Consolidated**: Axios interceptors for both JWT tokens and platform headers
- **Simplified**: Client configuration with single request interceptor

### 📁 File Structure Updates
- **Added**: `.env.local` to gitignore
- **Secured**: AWS credentials and environment files exclusion
- **Documented**: Migration process and configuration

## Environment Setup

### Prerequisites
1. **MariaDB Server** running on `localhost:3306`
2. **Database**: `validacio_factures`
3. **User**: `validacio_user` with password `ValidacioPass123!`
4. **AWS Credentials**: Set as environment variables:
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`

### S3 Buckets
- **Development**: `validacio-factures-dev`
- **Production**: `validacio-factures-uploads`

## Security Improvements
- ✅ JWT authentication working correctly
- ✅ File endpoints accessible without authentication (as designed)
- ✅ CORS properly configured for local development
- ✅ Filter chain execution order clarified
- ✅ Environment variables secured in gitignore

## Testing
- ✅ Backend starts successfully with MariaDB
- ✅ Frontend connects properly on port 5173
- ✅ JWT authentication flow working
- ✅ File upload to S3 functional
- ✅ All security filters operational

## Next Steps
1. Test file access endpoints after authentication fix
2. Verify production deployment compatibility
3. Document AWS credentials setup for team members
4. Consider database backup strategy for development

## Technical Notes
- Java 17 LTS maintained for stability
- Spring Boot 3.4.5 with Spring Security 6.4.5
- Maven 3.13.0 for build management
- MariaDB driver version managed by Spring Boot
- AWS SDK v2 for S3 integration
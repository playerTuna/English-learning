# Hướng dẫn hiện thực JWT cho dự án Spring Boot

## Tổng quan
Dự án đã được tích hợp JWT (JSON Web Token) để xác thực và phân quyền người dùng. JWT được sử dụng để tạo token an toàn cho việc đăng nhập và xác thực các request.

## Cấu trúc thư mục JWT

```
src/main/java/com/example/
├── config/
│   ├── SecurityConfig.java (đã cập nhật)
│   └── JwtAuthenticationFilter.java (mới)
├── security/
│   ├── JwtUtils.java (mới)
│   └── JwtService.java (mới)
├── controller/
│   └── AuthController.java (mới)
└── dto/
    ├── LoginRequestDTO.java (đã có)
    ├── LoginResponseDTO.java (đã có)
    └── RegisterRequestDTO.java (mới)
```

## Các file đã tạo/cập nhật

### 1. Dependencies (pom.xml)
```xml
<!-- JWT dependencies -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

### 2. JWT Configuration (application.properties)
```properties
# JWT Configuration
jwt.secret=${JWT_SECRET:mySecretKey123456789012345678901234567890}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

### 3. Các class chính

#### JwtUtils.java
- Xử lý tạo, validate và extract thông tin từ JWT token
- Sử dụng HMAC SHA-256 để ký token
- Thời gian hết hạn: 24 giờ (có thể cấu hình)

#### JwtService.java
- Implement UserDetailsService
- Xử lý authentication logic
- Load user details từ database

#### JwtAuthenticationFilter.java
- Filter để xử lý JWT token trong mỗi request
- Tự động extract token từ Authorization header
- Set authentication context nếu token hợp lệ

#### AuthController.java
- Endpoints cho authentication:
  - `POST /api/auth/login` - Đăng nhập
  - `POST /api/auth/register` - Đăng ký
  - `POST /api/auth/validate` - Validate token

#### SecurityConfig.java
- Cấu hình Spring Security với JWT
- Định nghĩa các endpoint public/private
- Tích hợp JWT filter vào security chain

## API Endpoints

### 1. Đăng nhập
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "your_username",
    "password": "your_password"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "userId": "123e4567-e89b-12d3-a456-426614174000",
        "username": "your_username",
        "name": "Your Name",
        "role": "user"
    }
}
```

### 2. Đăng ký
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "new_username",
    "password": "new_password",
    "name": "Full Name"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Registration successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "userId": "123e4567-e89b-12d3-a456-426614174000",
        "username": "new_username",
        "name": "Full Name",
        "role": "user"
    }
}
```

### 3. Validate Token
```http
POST /api/auth/validate
Authorization: Bearer your_jwt_token
```

**Response:**
```json
{
    "success": true,
    "message": "Token is valid",
    "data": "username"
}
```

## Cách sử dụng JWT trong các API khác

### 1. Gửi request với JWT token
```http
GET /api/protected-endpoint
Authorization: Bearer your_jwt_token
```

### 2. Trong Controller, lấy thông tin user hiện tại
```java
@GetMapping("/protected")
public ResponseEntity<?> protectedEndpoint(Authentication authentication) {
    String username = authentication.getName();
    // Xử lý logic với username
    return ResponseEntity.ok("Hello " + username);
}
```

## Cấu hình Security

### Endpoints được bảo vệ:
- `/api/auth/**` - Public (không cần token)
- `/api/public/**` - Public (không cần token)
- `/api/admin/**` - Chỉ admin (cần token + role ADMIN)
- `/api/**` - Cần token (authenticated)

### Cách thay đổi cấu hình:
Chỉnh sửa `SecurityConfig.java` trong method `securityFilterChain()`:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/api/public/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/**").authenticated()
    .anyRequest().permitAll())
```

## Environment Variables

Tạo file `.env` trong thư mục root:

```env
# JWT Configuration
JWT_SECRET=your_very_long_secret_key_here_at_least_32_characters
JWT_EXPIRATION=86400000

# Database
DB_URL=jdbc:postgresql://localhost:5432/english_learning
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Server
SERVER_PORT=8081
```

## Testing

### 1. Test đăng ký:
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "name": "Test User"
  }'
```

### 2. Test đăng nhập:
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. Test protected endpoint:
```bash
curl -X GET http://localhost:8081/api/protected-endpoint \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Lưu ý quan trọng

1. **JWT Secret**: Phải đủ dài (ít nhất 32 ký tự) và bảo mật
2. **Token Expiration**: Mặc định 24 giờ, có thể điều chỉnh
3. **HTTPS**: Nên sử dụng HTTPS trong production
4. **Token Storage**: Lưu trữ token an toàn ở client (localStorage, httpOnly cookie)
5. **Logout**: JWT là stateless, logout chỉ cần xóa token ở client

## Troubleshooting

### Lỗi thường gặp:

1. **"Token is invalid or expired"**
   - Kiểm tra token có đúng format không
   - Kiểm tra token có hết hạn không
   - Kiểm tra JWT secret có đúng không

2. **"User not found"**
   - Kiểm tra username có tồn tại trong database không
   - Kiểm tra password có đúng không

3. **"Access denied"**
   - Kiểm tra role của user
   - Kiểm tra endpoint có yêu cầu role đặc biệt không

## Mở rộng

### Thêm Refresh Token:
1. Tạo bảng `refresh_tokens` trong database
2. Implement refresh token logic trong `JwtService`
3. Thêm endpoint `/api/auth/refresh`

### Thêm Role-based Access Control:
1. Tạo các role cụ thể (USER, ADMIN, MODERATOR)
2. Cập nhật `SecurityConfig` với các rule phức tạp hơn
3. Sử dụng `@PreAuthorize` annotation trong controllers


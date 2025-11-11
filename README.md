# Tirisano Mmogo School Management System - Backend

Spring Boot backend API for the Tirisano Mmogo School Management System.

## 🚀 Quick Start

```bash
# Build the project
mvn clean install -s "C:\Users\am120754\settings.xml"

# Run the application
mvn spring-boot:run

# Or run from IDE (IntelliJ IDEA)
# Right-click on main class → Run
```

## ✨ Recent Updates

### New Features (Latest Release)
- **✅ Meeting Approval System**: Parent meeting requests require admin approval before scheduling
  - Parents request meetings with pending status
  - Admins can approve or reject meetings with reasons
  - Status tracking (PENDING, APPROVED, REJECTED, SCHEDULED, COMPLETED, CANCELLED)
  - Rejection reasons displayed to parents
  - Parent and teacher names captured in meeting requests
- **✅ Student Update Validation**: Date of birth restrictions (2011-2019) enforced in update form
- **✅ Fixed Student Update**: Student information updates now properly save to database
- **✅ Inline Document Viewer**: View documents directly in the application without opening new tabs
- **✅ Success Notifications**: Beautiful success modals with auto-redirect for all form submissions
- **✅ Trip Image Upload**: Upload trip images directly from local device (base64 encoding)
- **✅ Trip Status Management**: Put trips on hold or activate them
- **✅ Paid Students Report**: View students who paid for trips, grouped by grade
- **✅ Enhanced Student Display**: Shows parent name instead of ID, birth certificate ID instead of system ID
- **✅ Grade-Based Sorting**: All student lists are automatically sorted by grade (R, 1-7)
- **✅ Fixed Dashboard Stats**: Pending student count now displays correctly
- **✅ Consistent Form Styling**: Unified form field sizes and styling across the application

## 📋 Prerequisites

- Java JDK 21
- Maven 3.8.x or higher
- Firebase project with Firestore enabled
- Firebase service account JSON file

## 🔧 Configuration

### 1. Firebase Setup

**Get Service Account Key:**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project
3. Go to Project Settings → Service Accounts
4. Click "Generate New Private Key"
5. Save the JSON file as `firebase-service-account.json`
6. Place it in `src/main/resources/`

**Get Web API Key:**
1. Go to Project Settings → General
2. Scroll to "Web API Key"
3. Copy the key for `.env` file

### 2. Environment Variables

Create a `.env` file in the project root:

```env
# Firebase Web API Key (for password verification)
FIREBASE_API_KEY=your_actual_api_key_here
```

### 3. Application Configuration

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

firebase:
  config-path: classpath:firebase-service-account.json
  database-url: https://your-project-id-default-rtdb.firebaseio.com/
  api-key: ${FIREBASE_API_KEY:default_key}

cors:
  allowed-origins:
    - http://localhost:5173
    - http://127.0.0.1:5173

logging:
  level:
    com.school.management: DEBUG
```

## 📁 Project Structure

```
school_manager/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/tirisano/mmogo/school/manager/
│       │       ├── SchoolManagerApplication.java  # Main application
│       │       ├── config/                        # Configuration
│       │       │   ├── FirebaseConfig.java        # Firebase setup
│       │       │   ├── SecurityConfig.java        # Spring Security
│       │       │   ├── WebConfig.java             # CORS config
│       │       │   └── JacksonConfig.java         # JSON serialization
│       │       ├── controller/                    # REST endpoints
│       │       │   ├── AuthController.java
│       │       │   ├── StudentController.java
│       │       │   ├── ParentController.java
│       │       │   ├── DocumentController.java
│       │       │   ├── AdminController.java
│       │       │   ├── MeetingController.java
│       │       │   ├── TripController.java
│       │       │   └── PaymentController.java
│       │       ├── dto/                           # Data Transfer Objects
│       │       │   ├── ApiResponse.java
│       │       │   ├── LoginRequest.java
│       │       │   ├── RegisterRequest.java
│       │       │   └── UserDto.java
│       │       ├── enums/                         # Enumerations
│       │       │   ├── UserRole.java
│       │       │   ├── Gender.java
│       │       │   ├── StudentStatus.java
│       │       │   └── DocumentType.java
│       │       ├── model/                         # Domain models
│       │       │   ├── User.java
│       │       │   ├── Parent.java
│       │       │   ├── Student.java
│       │       │   ├── Document.java
│       │       │   ├── Announcement.java
│       │       │   ├── Meeting.java
│       │       │   ├── Trip.java
│       │       │   └── Payment.java
│       │       └── service/                       # Business logic
│       │           ├── AuthService.java
│       │           ├── FirebaseService.java
│       │           ├── StudentService.java
│       │           ├── ParentService.java
│       │           └── DocumentService.java
│       └── resources/
│           ├── application.yml
│           └── firebase-service-account.json
├── .env                                           # Environment variables
├── .env.example                                   # Env template
├── .gitignore
├── pom.xml                                        # Maven config
└── README.md
```

## 🔌 API Endpoints

### Authentication

```
POST   /api/auth/register          Register new user
POST   /api/auth/login             User login (validates password)
POST   /api/auth/forgot-password   Send password reset email
POST   /api/auth/reset-password    Reset user password
GET    /api/auth/user-by-email     Get user by email
```

### Student Management

```
POST   /api/students                          Create student
GET    /api/students                          Get all students
GET    /api/students/{id}                     Get student by ID
PUT    /api/students/{id}                     Update student
DELETE /api/students/{id}                     Delete student
GET    /api/students/parent/{parentId}        Get children of parent
GET    /api/students/pending                  Get pending applications
GET    /api/students/approved                 Get approved students
GET    /api/students/rejected                 Get rejected students
PUT    /api/students/{id}/approve             Approve student
PUT    /api/students/{id}/approve-with-class  Approve and assign class
PUT    /api/students/{id}/reject              Reject student with reason
```

### Parent Management

```
POST /api/parents                              Create parent
GET  /api/parents                              Get all parents
GET  /api/parents/{id}                         Get parent by ID
PUT  /api/parents/{id}                         Update parent
DELETE /api/parents/{id}                       Delete parent
POST /api/parents/{id}/children                Add child
GET  /api/parents/{id}/children                Get parent's children
PUT  /api/parents/{id}/children/{studentId}    Update child
POST /api/parents/{id}/document-requests       Request document
```

### Document Management

```
POST   /api/documents                     Upload document
GET    /api/documents                     Get all documents
GET    /api/documents/{id}                Get document by ID
PUT    /api/documents/{id}                Update document
DELETE /api/documents/{id}                Delete document
GET    /api/documents/student/{id}        Get student documents
GET    /api/documents/parent/{id}         Get parent documents
GET    /api/documents/type/{type}         Get documents by type
GET    /api/documents/unverified          Get unverified documents
PUT    /api/documents/{id}/verify         Verify document (admin)
```

### Announcements

```
GET    /api/admin/announcements       Get all announcements
GET    /api/admin/announcements/{id}  Get announcement by ID
POST   /api/admin/announcements       Create announcement
PUT    /api/admin/announcements/{id}  Update announcement
DELETE /api/admin/announcements/{id}  Delete announcement
```

### Meetings

```
GET    /api/meetings                      Get all meetings
GET    /api/meetings/{id}                 Get meeting by ID
POST   /api/meetings                      Create meeting (admin - auto-approved)
PUT    /api/meetings/{id}                 Update meeting
DELETE /api/meetings/{id}                Delete meeting
GET    /api/meetings/parent/{parentId}    Get parent meetings
POST   /api/meetings/request-one-on-one   Request parent-teacher meeting (pending status)

# Admin Approval Endpoints
GET    /api/meetings/pending              Get pending meeting requests
GET    /api/meetings/approved             Get approved meetings
GET    /api/meetings/rejected             Get rejected meetings
PUT    /api/meetings/{id}/approve         Approve meeting request
PUT    /api/meetings/{id}/reject          Reject meeting with reason (body: { reason })
```

### Trips

```
GET    /api/trips                           Get all trips
GET    /api/trips/{id}                      Get trip by ID
POST   /api/trips                           Create trip
PUT    /api/trips/{id}                      Update trip
DELETE /api/trips/{id}                      Delete trip
POST   /api/trips/{id}/register             Register for trip
DELETE /api/trips/{id}/register/{studentId} Unregister from trip
PUT    /api/trips/{id}/hold                 Put trip on hold
PUT    /api/trips/{id}/activate             Activate trip
PUT    /api/trips/{id}/image                Upload/update trip image
GET    /api/trips/{id}/paid-students        Get paid students grouped by grade
```

### Payments

```
POST /api/payments/mock                     Create mock payment
GET  /api/payments                          Get all payments
GET  /api/payments/{id}                     Get payment by ID
GET  /api/payments/student/{id}             Get student payments
GET  /api/payments/trip/{id}                Get trip payments
PUT  /api/payments/{id}/status              Update payment status
```

## 🗄️ Data Models

### User
```java
- uid: String (Firebase Auth UID)
- email: String (unique)
- fullName: String
- phoneNumber: String
- role: UserRole (ADMIN, PARENT)
- active: boolean
- createdAt: Timestamp
```

### Student
```java
- studentId: String (auto-generated)
- name: String
- surname: String
- gender: Gender (MALE, FEMALE, OTHER)
- dateOfBirth: Timestamp
- birthCertificateId: String (unique)
- nationality: String
- grade: String
- yearOfAdmission: Integer
- previousSchool: String
- parentId: String
- className: String
- teacher: String
- status: StudentStatus (PENDING, APPROVED, REJECTED)
- rejectionReason: String
- createdAt: Timestamp
```

### Document
```java
- documentId: String (auto-generated)
- fileName: String
- fileUrl: String (base64 or storage URL)
- documentType: DocumentType
- studentId: String
- parentId: String
- uploadedBy: String
- uploadedByRole: String
- mimeType: String
- fileSize: Long
- description: String
- uploadedAt: Timestamp
- verified: boolean
- verifiedBy: String
- verifiedAt: Timestamp
```

### Meeting
```java
- meetingId: String (auto-generated)
- title: String (required)
- description: String
- scheduledTime: Timestamp
- teacherId: String
- teacherName: String
- parentId: String
- parentName: String
- type: MeetingType (GROUP_MEETING, ONE_ON_ONE)
- status: MeetingStatus (PENDING, APPROVED, REJECTED, SCHEDULED, COMPLETED, CANCELLED)
- rejectionReason: String (populated if rejected)
- createdAt: Timestamp
```

## 🔐 Security Features

### Authentication & Authorization
- ✅ Firebase Authentication integration
- ✅ Password validation via Firebase REST API
- ✅ Role-based access control (ADMIN, PARENT)
- ✅ JWT token support (via Firebase)
- ✅ Session management

### Data Security
- ✅ Environment variables for sensitive data
- ✅ CORS configuration
- ✅ Input validation
- ✅ File upload validation (size, type)
- ✅ Firebase security rules

### Password Security
- ✅ Password reset via Firebase Auth
- ✅ Password verification on login
- ✅ Secure password storage (Firebase)
- ✅ Email-based password recovery

## 🎨 Frontend Features

### Parent Portal
- ✅ Child application form with:
  - Date of birth validation (2011-2019)
  - Success modal and auto-redirect to "My Children"
  - Document upload during registration
- ✅ Student information updates with proper validation
- ✅ Inline document viewer for all uploaded documents (PDFs, images)
- ✅ Document upload with file validation
- ✅ Trip registration with payment processing
- ✅ View approved children and their documents
- ✅ Meeting request system with:
  - Request one-on-one meetings with teachers
  - View meeting status (PENDING, APPROVED, REJECTED)
  - See rejection reasons if meeting not approved
  - Authorization check (requires at least one approved child)
  - Color-coded status badges

### Admin Portal
- ✅ Dashboard with real-time statistics (pending students, total trips)
- ✅ Student management with:
  - Display parent name instead of parent ID
  - Display birth certificate ID instead of system-generated ID
  - Automatic sorting by grade (R, 1, 2, 3, 4, 5, 6, 7)
  - Inline document viewing
  - Approve/reject student applications with class assignment
- ✅ Trip management with:
  - Local image upload from device (converts to base64)
  - Put trip on hold / Activate trip
  - View paid students grouped by grade
  - Edit trip information
  - Delete trips
- ✅ Meeting management with approval system:
  - View all meetings with filterable tabs (All, Pending, Approved, Rejected)
  - Approve parent meeting requests
  - Reject meetings with reason (e.g., teacher not available)
  - View parent and teacher information for each meeting
  - Status tracking with color-coded badges
  - Create admin meetings (auto-approved)
- ✅ Announcement management

### UI/UX Improvements
- ✅ Success modals with animations for all form submissions
- ✅ Consistent form styling across all components
- ✅ Inline document viewer (no new tabs)
- ✅ Responsive design for mobile and desktop
- ✅ Loading states and error handling
- ✅ File upload with drag-and-drop support

## 🛠️ Development

### Build Commands

```bash
# Clean build
mvn clean

# Compile
mvn compile

# Run tests
mvn test

# Package (creates JAR)
mvn package

# Install to local repository
mvn install

# With custom settings
mvn clean install -s "path/to/settings.xml"
```

### Running the Application

**Development Mode:**
```bash
mvn spring-boot:run
```

**Production Mode:**
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/school_manager-0.0.1-SNAPSHOT.jar
```

**IDE (IntelliJ IDEA):**
1. Import as Maven project
2. Wait for dependencies to download
3. Find `SchoolManagerApplication.java`
4. Right-click → Run 'SchoolManagerApplication'

## 🐛 Troubleshooting

### Common Issues

#### 1. Firebase Initialization Failed
**Problem**: `Failed to initialize Firebase`

**Solution**:
- Verify `firebase-service-account.json` exists in `src/main/resources/`
- Check file has correct Firebase credentials
- Ensure Firestore is enabled in Firebase Console
- Verify Firebase project ID matches

#### 2. Maven Build Fails
**Problem**: `Could not resolve dependencies`

**Solution**:
```bash
# Use corporate settings
mvn clean install -s "C:\Users\am120754\settings.xml"

# Force update
mvn clean install -U

# Clear cache
rm -rf ~/.m2/repository
```

#### 3. Port Already in Use
**Problem**: `Port 8080 is already in use`

**Solution**:
```bash
# Change port in application.yml
server:
  port: 8081

# Or kill process on port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

#### 4. CORS Error
**Problem**: Frontend can't connect

**Solution**:
- Check `WebConfig.java` has correct allowed origins
- Verify frontend URL matches CORS configuration
- Clear browser cache

#### 5. Password Validation Not Working
**Problem**: Can login with any password

**Solution**:
- Restart Spring Boot application
- Verify `.env` file has `FIREBASE_API_KEY`
- Check logs for authentication errors
- Ensure `AuthService.java` has password verification code

## 🧪 Testing

### Manual API Testing

Using curl:
```bash
# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","fullName":"Test User","phoneNumber":"1234567890","role":"PARENT"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Get students (with auth)
curl -X GET http://localhost:8080/api/students \
  -H "Authorization: Bearer <your-token>"
```

Using Postman:
1. Import collection (if provided)
2. Set environment variables
3. Test endpoints sequentially

## 📊 Logging

### Configuration

Logging levels in `application.yml`:
```yaml
logging:
  level:
    com.tirisano.mmogo.school.manager: DEBUG
    org.springframework.web: INFO
    org.springframework.security: INFO
```

### Log Locations
- Console output (default)
- Can configure file logging in `application.yml`

### Important Logs
- ✅ Authentication attempts
- ✅ Password verification results
- ✅ Firebase operations
- ✅ Student CRUD operations
- ✅ Document uploads

## 🚀 Deployment

### Preparing for Production

1. **Update Configuration:**
```yaml
# Change to production database
firebase:
  database-url: https://prod-project-id.firebaseio.com/

# Restrict CORS
cors:
  allowed-origins:
    - https://yourdomain.com
```

2. **Environment Variables:**
```bash
# Set production environment variables
export FIREBASE_API_KEY=prod_key_here
export SPRING_PROFILES_ACTIVE=production
```

3. **Build:**
```bash
mvn clean package -DskipTests
```

4. **Run:**
```bash
java -jar target/school_manager-0.0.1-SNAPSHOT.jar
```

### Deployment Options
- Traditional server (Tomcat, etc.)
- Docker container
- Cloud platforms (AWS, Google Cloud, Azure)
- Heroku

## 📦 Dependencies

### Core
- Spring Boot 3.3.5
- Java 21
- Maven

### Firebase
- Firebase Admin SDK 9.2.0
- Google Auth Library 1.19.0

### Spring
- Spring Web
- Spring Security
- Spring Validation

### Utilities
- Lombok (annotations)
- DotEnv Java (environment variables)
- Jackson (JSON processing)

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Write/update tests
4. Ensure build passes
5. Submit pull request

## 📄 License

MIT License - See LICENSE file for details

---

**Need help?** Check the main project README or backend logs for error details.

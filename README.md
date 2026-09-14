# 🚀 TaskCraft - Component-Based Web App (Java 11 & Spring Boot)

Ứng dụng web quản lý công việc (Task & Workspace Manager) xây dựng trên nền tảng **Java 11, Spring Boot REST API** và giao diện **Web UI chia theo Component** hiện đại.

---

## 🌟 Tính Năng Nổi Bật

- **Backend Architecture (Spring Boot):**
  - Architecture chuẩn lớp (Controller, Service, JPA Repository, Model/Entity, DTO, Global Exception Handler).
  - RESTful API endpoints hỗ trợ đầy đủ thao tác CRUD (Create, Read, Update, Delete, Filter, Stats).
  - Cơ sở dữ liệu **H2 In-Memory Database** tự động lưu trữ và khởi tạo dữ liệu mẫu khi ứng dụng khởi chạy.
  - Tích hợp H2 Web Console tại `/h2-console` để truy vấn SQL trực tiếp.
  - Cấu hình CORS tự động.

- **Frontend Component Architecture:**
  - Giao diện Dark Glassmorphic sang trọng, responsive, mượt mà.
  - Tách bạch các UI Components độc lập:
    - `Navbar`: Thanh điều hướng và nút tạo mới.
    - `StatsCard`: Các thẻ thống kê số liệu công việc realtime.
    - `TaskFilter`: Thanh tìm kiếm live search và bộ lọc (Status, Priority, Category).
    - `TaskCard`: Thẻ hiển thị task, đổi trạng thái 1-click, badge phân biệt priority & status.
    - `TaskModal`: Dialog modal tạo mới & chỉnh sửa task.
    - `ApiService`: Module giao tiếp HTTP Fetch với backend Spring Boot.

---

## 📂 Cấu Trúc Thư Mục Dự Án

```text
java-spring-web-app/
├── pom.xml                                   # Cấu hình Maven & dependencies
├── mvnw.cmd / mvnw                           # Maven Wrapper (chạy không cần cài Maven)
├── .mvn/wrapper/                             # Thư mục chứa Maven wrapper jar
├── .gitignore
├── README.md
└── src/
    └── main/
        ├── java/com/example/taskmanager/
        │   ├── TaskManagerApplication.java    # Class khởi chạy Spring Boot
        │   ├── config/                        # CorsConfig, DataInitializer
        │   ├── controller/                    # TaskController, CategoryController
        │   ├── service/                       # TaskService, CategoryService
        │   ├── repository/                    # TaskRepository, CategoryRepository
        │   ├── model/                         # Task, Category, Status, Priority
        │   ├── dto/                           # TaskDTO, TaskStatsDTO, ApiResponse
        │   └── exception/                     # GlobalExceptionHandler, ResourceNotFoundException
        └── resources/
            ├── application.properties         # Cấu hình Port 8080 & H2 DB
            └── static/                        # Giao diện Web UI (Component-based)
                ├── index.html
                ├── css/styles.css
                └── js/
                    ├── api.js                 # HTTP Client gọi API Spring Boot
                    ├── app.js                 # App Controller điều phối các components
                    └── components/
                        ├── Navbar.js
                        ├── StatsCard.js
                        ├── TaskFilter.js
                        ├── TaskCard.js
                        └── TaskModal.js
```

---

## 🛠️ Hướng Dẫn Khởi Chạy Ứng Dụng

### Yêu cầu môi trường:
- Java JDK 11 hoặc cao hơn.

### Các bước chạy dự án:

1. Mở terminal tại thư mục `java-spring-web-app`:
   ```bash
   cd java-spring-web-app
   ```

2. Chạy ứng dụng bằng Maven Wrapper:
   - **Windows (PowerShell/CMD):**
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```
   - **Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```

3. Mở trình duyệt web truy cập:
   - 🌐 Giao diện chính: [http://localhost:8080](http://localhost:8080)
   - 🗄️ H2 Database Console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
     - JDBC URL: `jdbc:h2:mem:taskdb`
     - User: `sa`
     - Password: (để trống)

---

## 📤 Hướng Dẫn Đưa Repo Lên GitHub Của Bạn

Để đẩy repo này lên tài khoản GitHub của bạn (`phyc11`), làm theo các bước bên dưới:

### Bước 1: Tạo Repository mới trên GitHub
1. Truy cập [https://github.com/new](https://github.com/new).
2. Nhập tên Repository (ví dụ: `java-spring-web-app`).
3. Chọn **Public** (hoặc Private).
4. **Lưu ý:** Không chọn *Add a README file*, *.gitignore* hay *License* (vì dự án đã có sẵn).
5. Nhấn **Create repository**.

### Bước 2: Push code từ máy local lên GitHub
Mở PowerShell/Terminal tại thư mục dự án `java-spring-web-app` và chạy các câu lệnh:

```bash
# 1. Khởi tạo Git local (nếu chưa khởi tạo)
git init

# 2. Add toàn bộ các file
git add .

# 3. Tạo commit đầu tiên
git commit -m "Initial commit: Java Spring Boot REST API with Component-based Web UI"

# 4. Đổi tên branch chính thành main
git branch -M main

# 5. Thêm remote origin GitHub của bạn
git remote add origin https://github.com/phyc11/java-spring-web-app.git

# 6. Push code lên GitHub
git push -u origin main
```

---

## 📡 Danh Sách API Endpoints (Spring Boot REST API)

| HTTP Method | Endpoint | Mô tả |
| :--- | :--- | :--- |
| `GET` | `/api/tasks` | Lấy danh sách task (hỗ trợ filter `status`, `priority`, `categoryId`, `search`) |
| `GET` | `/api/tasks/{id}` | Lấy chi tiết 1 task theo ID |
| `GET` | `/api/tasks/stats` | Thống kê số lượng task (Total, Todo, InProgress, Completed, Urgent) |
| `POST` | `/api/tasks` | Tạo task mới |
| `PUT` | `/api/tasks/{id}` | Cập nhật thông tin task |
| `PATCH` | `/api/tasks/{id}/status` | Đổi nhanh trạng thái task |
| `DELETE` | `/api/tasks/{id}` | Xóa task |
| `GET` | `/api/categories` | Lấy danh sách danh mục |
| `POST` | `/api/categories` | Tạo danh mục mới |

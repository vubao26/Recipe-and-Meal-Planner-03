# Android_UTH_16 — Recipe and Meal Planner

Ứng dụng Android giúp người dùng quản lý công thức nấu ăn, lên kế hoạch bữa ăn theo tuần, và tạo danh sách đi chợ.

## Thành viên nhóm
| Họ tên | MSSV | GitHub username | Phụ trách |
|---|---|---|---|
| Lê Trần Vũ Bảo | 077206008670 | vubao26 | Recipes module |
| Thái Anh Quân | 044207010126 | HaritaNguyen | Planner & Shopping module |

## Kiến trúc
- Mẫu kiến trúc: MVVM + Repository pattern
- Luồng dữ liệu: UI (Jetpack Compose) → ViewModel (StateFlow) → Repository → Room (DAO/Entity) → SQLite (local, on-device)
- Điều hướng: Navigation Compose, 2 luồng chính (Recipes, Meal Planner) qua bottom navigation
- Chi tiết cấu trúc thư mục: xem `Code/README.md`

## Yêu cầu môi trường
- Android Studio Koala (2024.1) trở lên
- JDK 17
- Kotlin 1.9.24, Gradle 8.7, Android Gradle Plugin 8.5.2
- compileSdk / targetSdk 34, minSdk 24

## Cấu hình và hướng dẫn chạy
1. Mở thư mục `Code/` bằng Android Studio (File → Open).
2. Để Gradle sync tự động tải dependencies (cần internet). Nếu Android Studio báo thiếu Gradle wrapper, chọn để nó tự sinh lại rồi commit file đó vào repo.
3. Chạy trên emulator hoặc thiết bị thật có Android 7.0 (API 24) trở lên.
4. Không cần API key hay cấu hình môi trường thêm — toàn bộ dữ liệu lưu local bằng Room.

## Tiến độ Basic Requirements
- [x] Quản lý recipe (CRUD): tên, danh mục, nguyên liệu, cách làm, thời gian, khẩu phần
- [ ] Ảnh cho recipe
- [x] Tìm kiếm & lọc theo nguyên liệu/danh mục
- [x] Scale nguyên liệu theo khẩu phần
- [x] Lên kế hoạch bữa ăn theo tuần
- [x] Shopping list gộp từ meal plan
- [x] Đánh dấu nguyên liệu có sẵn (pantry)
- [x] Lưu công thức yêu thích & xem gần đây
- [ ] Export/share shopping list

## Video demo


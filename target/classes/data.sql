-- Tạo users trước
INSERT INTO users (id, email, password, full_name, phone) VALUES 
(1, 'thanhphong@gmail.com', '$2a$10$jx3PdJ7cZhY7jJk6qKmXp.NV9gEhQoWKXJ4vxUaEteGq4Xg1x.V4K', 'Nguyễn Thanh Phong', '0912345678'),
(100, 'admin@carxe.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MrYVJY8Y2nZmsYFl11nQU6L1sFQ6NQa', 'Admin System', '0987654321'),
(101, 'user1@carxe.com', '$2a$10$jx3PdJ7cZhY7jJk6qKmXp.NV9gEhQoWKXJ4vxUaEteGq4Xg1x.V4K', 'Người dùng 1', '0912345678'),
(102, 'user2@carxe.com', '$2a$10$FVZjgX5fPzjaUQd4Aj3kQ.LJZrMhzkXNwF1GYeWGDtPd0dTwjMHxO', 'Người dùng 2', '0923456789');

-- Thêm roles sau khi đã có users
INSERT INTO user_roles (user_id, role) VALUES 
(1, 'USER'),
(100, 'ADMIN'),
(100, 'USER'), 
(101, 'USER'),
(102, 'USER');

-- Cars data with license plates
INSERT INTO cars (id, model, brand, year, color, license_plate, price_per_day, available, is_featured, description, image_url) VALUES
(1, 'Toyota Vios', 'Toyota', 2022, 'Trắng', '29A-12345', 500000, true, true, 'Xe đời mới, tiết kiệm nhiên liệu', '/images/toyota-vios.jpg'),
(2, 'Honda City', 'Honda', 2021, 'Đen', '30G-67890', 550000, true, true, 'Thiết kế sang trọng, nội thất cao cấp', '/images/honda-city.jpg'),
(3, 'Hyundai Accent', 'Hyundai', 2023, 'Bạc', '51B-54321', 450000, false, false, 'Xe bền bỉ, phù hợp gia đình', '/images/hyundai-accent.jpg'),
(4, 'Ford Ranger', 'Ford', 2020, 'Xanh', '43D-98765', 700000, true, true, 'Xe bán tải mạnh mẽ, địa hình tốt', '/images/ford-ranger.jpg'),
(5, 'VinFast VF 8', 'VinFast', 2023, 'Đỏ', '30A-11223', 800000, true, true, 'Xe điện hiện đại, công nghệ tiên tiến', '/images/vinfast-vf8.jpg');

-- Bookings data - sửa user_id để khớp với users đã có
INSERT INTO bookings (id, user_id, car_id, start_date, end_date, total_price, status) VALUES
(1, 1, 1, '2025-04-10', '2025-04-15', 2500000, 'CONFIRMED'),
(2, 101, 2, '2025-04-12', '2025-04-14', 1100000, 'PENDING'),
(3, 102, 4, '2025-04-20', '2025-04-25', 3500000, 'COMPLETED');

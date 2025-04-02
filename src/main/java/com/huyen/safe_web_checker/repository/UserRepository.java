package com.huyen.safe_web_checker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.huyen.safe_web_checker.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User save(User user);

    // Tìm user bằng username (sử dụng cho đăng nhập)
    Optional<User> findByUsername(String username);

    // Kiểm tra username đã tồn tại chưa (sử dụng khi đăng ký)
    boolean existsByUsername(String username);

    // Tìm user bằng email
    Optional<User> findByEmail(String email);

    // Kiểm tra email đã tồn tại chưa
    boolean existsByEmail(String email);

    // Tìm user bằng username hoặc email
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    // Custom query để đếm số lần quét của user trong ngày
    @Query("SELECT COUNT(ush) FROM UserScanHistory ush WHERE ush.user.id = :userId AND DATE(ush.scanDate) = CURRENT_DATE")
    long countUserScansToday(@Param("userId") Integer userId);

    // Custom query để đếm số lần quét của guest (user = null) trong ngày
    @Query("SELECT COUNT(ush) FROM UserScanHistory ush WHERE ush.user IS NULL AND DATE(ush.scanDate) = CURRENT_DATE")
    long countGuestScansToday();
}

package com.example.demo.service;

import com.example.demo.model.UserPrincipal;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Component
@Service
public class JwtService {
    private static final String SECRET_KEY = "Quan";
    private static final Long EXPIRE_TIME = 100000000L;
    private static Logger logger = LoggerFactory.getLogger(JwtService.class); // Logger truyền vào tên Class của mình

    public String generateAccessToken(Authentication authentication) {  // Hàm sinh ra Token login
        UserPrincipal userPrinciple = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userPrinciple.getUsername())
                .setIssuedAt(new Date()) // Thời gian hiện tại của hệ thống
                .setExpiration(new Date(new Date().getTime() +EXPIRE_TIME*1000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // HS512 là thuật toán mã hóa. signWith để tạo chữ ký điện tử.
                .compact(); // để sinh ra và trả về String.
    }

    public boolean validateJwtToken(String authToken) { // Hàm validate xem có hết hạn không, chữ ký có hợp lệ hay không.
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: {} ", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {}", e);
        }
        return false;
    }

    public String getUserNameFromJwtToken(String token) {    // Từ Token trả về lấy ra username.
        String userName = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody().getSubject();
        return userName;
    }
}

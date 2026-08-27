package com.servicecops.project.config;

import com.servicecops.project.models.database.SystemRoleModel;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.repositories.SystemUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;

@Service
@Data
public class JwtUtility {

    private final SystemUserRepository userRepository;
    //private final SystemRoleRepository roleRepository;

    @Value("${secret}")
    private String secret;

    public static final long JWT_TOKEN_VALIDITY = 12 * 60 * 60;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws Exception {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, username);
    }

    /**
     * @param token    jwt token
     * @param username username used when setting up userDetails, for our case, we use username
     * @return Boolean
     * @implNote Will check for two things, if the token is expired as per eat claim
     * and will also check of the user has already requested for another token, requesting for another token
     * makes all the previously generated token expired.
     */
    private boolean isTokenExpired(String token, String username) throws Exception {
        Optional<SystemUserModel> usersModel = userRepository.findByUsername(username);
        if (usersModel.isEmpty()) {
            throw new IllegalStateException("User not found");
        } else if (extractExpiration(token).before(new Date())) {
            throw new Exception("SESSION EXPIRED");
        } else if (usersModel.get().getLastLoggedInAt() == null) {
            throw new IllegalStateException("INVALID TOKEN");
        } else if (extractIssuedAt(token).after(usersModel.get().getLastLoggedInAt())) {
            throw new Exception("EXPIRED TOKEN USED");
        } else if (!usersModel.get().getIsActive()) {
            throw new Exception("ACCOUNT INACTIVE");
        }
        return false;
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {

        long now = System.currentTimeMillis();
        Timestamp stamp = new Timestamp(now);

        Optional<SystemUserModel> userOptional =
                userRepository.findByUsername(userDetails.getUsername());
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("User not found");
        }
        SystemUserModel user = userOptional.get();
        user.setLastLoggedInAt(stamp);
        userRepository.save(user);

        // getting roles to add to token
        SystemRoleModel role = user.getRole();
        if (role == null ) {
            throw new IllegalArgumentException("Invalid User Role");
        }

        List<String> roleCodes = new ArrayList<>();

            if (role.getRoleCode() != null) {
                roleCodes.add(role.getRoleCode());
            }

        claims.put("roles", roleCodes);

        // adding permissions to token
        List<String> permissions = new ArrayList<>();
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if (!permissions.contains(authority.getAuthority())) {
                permissions.add(authority.getAuthority());
            }
        }
        claims.put("permissions", permissions);

        return Jwts
                .builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(now))
                .expiration(new Date(now + JWT_TOKEN_VALIDITY * 1000))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

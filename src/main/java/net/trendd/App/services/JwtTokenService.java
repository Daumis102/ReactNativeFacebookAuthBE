package net.trendd.App.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenService implements Serializable {

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    private RSAPrivateKey privateKey = null;
    private RSAPublicKey publicKey = null;

    public JwtTokenService(@Value("classpath:certs/${public.key.name}") final Resource publicKeyRes,
                           @Value("classpath:certs/${private.key.name}") final Resource privateKeyRes) throws Exception {
        this.publicKey = readX509PublicKey(new String(publicKeyRes.getInputStream().readAllBytes(),
                                                      StandardCharsets.UTF_8)); // have to read these as stream, since reading as file does not work when packaged as JAR
        this.privateKey = readPKCS8PrivateKey(new String(privateKeyRes.getInputStream().readAllBytes(),
                                                         StandardCharsets.UTF_8));
    }

    //retrieve userId from jwt token
    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload();
    }

    //check if the token has expired
    private Boolean isTokenExpired(Date tokenExpiration) {
        return tokenExpiration.before(new Date());
    }

    //generate token for user
    public String generateToken(String username, Map<String, List<?>> claims) {
        return doGenerateToken(claims, username); // pass whatever
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, List<?>> claims, String subject) {

        return Jwts.builder()
                   .claims(claims)
                   .subject(subject)
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                   .signWith(privateKey)
                   .compact();
    }

    public String refreshToken(String tokenToRefresh) {
        return Jwts.builder()
                   .claims(getAllClaimsFromToken(tokenToRefresh))
                   .subject(getUserIdFromToken(tokenToRefresh))
                   .issuedAt(new Date(System.currentTimeMillis()))
                   .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                   .signWith(privateKey)
                   .compact();
    }

    public String verifyAngGetSubject(String token) {

        Claims claims;
        try {
            claims = getAllClaimsFromToken(token);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }

        if (isTokenExpired(claims.getExpiration())) {
            return null;
        }

        return claims.getSubject();
    }


    private RSAPublicKey readX509PublicKey(String key) throws Exception {
        String publicKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----", "")
                                 .replaceAll("\n", "")
                                 .replaceAll("\r", "")
                                 .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM.getBytes());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private RSAPrivateKey readPKCS8PrivateKey(String key) throws Exception {

        String privateKeyPEM = key.replace("-----BEGIN PRIVATE KEY-----", "")
                                  .replaceAll("\n", "")
                                  .replaceAll("\r", "")
                                  .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM.getBytes());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}

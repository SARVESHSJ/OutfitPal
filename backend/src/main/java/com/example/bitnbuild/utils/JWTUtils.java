package com.example.bitnbuild.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTUtils {
//This class is responsible for handling JWT creation, validation, and extraction of claims
    private static final long EXPIRATION_TIME=1000*60*24*7; //7 days

    //This is a SecretKey object used for signing the JWTs. It is initialized in the constructor using signing algo
    private final SecretKey Key;

    //This constructor is used for Setting Up(initializes) the Secret Key
    public JWTUtils() {
        String secreteString = "qzhJ2Q8uF3CZT1lkrmWMBWy5jRBsSFWjx9UKRhTHKQ8="; // Base64-encoded string
        byte[] keyBytes = Base64.getDecoder().decode(secreteString); // Directly decode the Base64 string into bytes
        this.Key = new SecretKeySpec(keyBytes, "HmacSHA256"); // Generate SecretKey using HMAC-SHA256(signing algorithm.)
    }

    //This method creates a JWT for a given UserDetails object
    //JWT includes the username, issue time, expiration time, and signature.
    public String generateToken(UserDetails userDetails)
    {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
                .signWith(Key) //The token is signed using the secret key
                .compact();
    }
    //This method extracts username(subject) from the token
    public String extractUsername(String token)
    {

        return extractClaims(token, Claims::getSubject);
    }

    //This is a generic method used to extract any claim from the JWT. The type T depends on what claim you want to extract(username,expiration time)
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(Key).build().parseSignedClaims(token).getPayload());

        //Jwts.parser(): Creates a parser that can read and verify JWTs.
        //verifyWith(Key): Verifies the JWT's signature using the same secret key used to sign it.
        //parseSignedClaims(token): Examines the signed JWT and retrieves the payload (which contains the claims).
    }
    //This method validates the token by checking two things
    //1.The username in the token matches the username in the UserDetails.
    //2.The token is not expired (!isTokenExpired(token)).
    public boolean isValid(String token,UserDetails userDetails)
    {
        final String username=extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // Extracts the expiration date from the token.
    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}


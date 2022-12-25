package ma.tarmoun.securityservice.web;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor

public class AuthController {
    private JwtEncoder jwtEncoder ;
    private JwtDecoder jwtDecoder;

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;



    @PostMapping("/token")
    public Map<String , String > jwtToken(String grantType ,String username , String password ,boolean withRefreshToken,String refreshToken){

        String subject ="";
        String scope ="";

        if(grantType.equals(password)){
            Authentication authentication=authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username,password)
            );
            subject= authentication.getName();
            scope= authentication.getAuthorities().stream().map(aut->aut.getAuthority()).collect(Collectors.joining(" "));

        }else if (grantType.equals("refreshToken")){
            Jwt decodedJwt =jwtDecoder.decode(refreshToken);
            subject = decodedJwt.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            scope = authorities.stream().map(auth->auth.getAuthority()).collect(Collectors.joining(" "));
        }


        Map<String ,String > idToken = new HashMap<>() ;

        Instant instant= Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(instant)
                .expiresAt(instant.plus(withRefreshToken? 5:30, ChronoUnit.MINUTES))
                .issuer("security-service")
                .claim("scope" , scope)
                .build();

        String jwtAccessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();

        idToken.put("accessToken" , jwtAccessToken);
        if(withRefreshToken){
            JwtClaimsSet jwtClaimsSetRefresh = JwtClaimsSet.builder()
                    .subject(subject)
                    .issuedAt(instant)
                    .expiresAt(instant.plus(30, ChronoUnit.MINUTES))
                    .issuer("security-service")
                    .build();

            String jwtRefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSetRefresh)).getTokenValue();
            idToken.put("refreshToken",jwtRefreshToken);
        }
        return  idToken ;
    }
}


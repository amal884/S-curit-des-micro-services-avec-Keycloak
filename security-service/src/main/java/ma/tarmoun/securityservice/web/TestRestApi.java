package ma.tarmoun.securityservice.web;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestRestApi {
    @GetMapping("/dataTest")
    // pour acceder a cet methode jje dois avoir tell role
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    public Map<String , Object> dataTest(){
        return Map.of("message" ,"Data test") ;

    }


    @PostMapping("/saveData")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public Map<String , String> saveData(Authentication authentication , String data){
        return Map.of("dataSaved" ,"data") ;

    }
}

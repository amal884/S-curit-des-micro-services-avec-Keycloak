package ma.tarmoun.securityservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
@ConfigurationProperties(prefix = "rsa") // chercher properties qui commance par rsa
public record RsaKeysConfig(RSAPublicKey publicKey , RSAPrivateKey privateKey) {

}

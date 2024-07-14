package com.waly.auth_service.configs;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.waly.auth_service.configs.customgrant.CustomPasswordAuthenticationConverter;
import com.waly.auth_service.configs.customgrant.CustomPasswordAuthenticationProvider;
import com.waly.auth_service.configs.customgrant.CustomUserAuthorities;
import com.waly.auth_service.dtos.AccessToken;
import com.waly.auth_service.entities.User;
import com.waly.auth_service.services.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
public class AuthorizationServerConfig {

  @Value("${security.client-id}")
  private String clientId;
  @Value("${security.client-secret}")
  private String clientSecret;
  @Value("${security.jwt.duration}")
  private Integer jwtDurationSeconds;
  @Value("${security.jwt.kid}")
  private String kid;
  @Value("${security.jwt.private-key}")
  private String privateKey;
  @Value("${security.jwt.public-key}")
  private String publicKey;

  private final UserDetailsService userDetailsService;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
  private static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
  private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
  private static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

  @Getter
  private RSAKey rsaKey;

  public AuthorizationServerConfig(UserDetailsService userDetailsService, UserService userService, PasswordEncoder passwordEncoder) {
    this.userDetailsService = userDetailsService;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  @Bean
  @Order(2)
  public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    // @formatter:off
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
			.tokenEndpoint(tokenEndpoint -> tokenEndpoint
				.accessTokenRequestConverter(new CustomPasswordAuthenticationConverter())
				.authenticationProvider(new CustomPasswordAuthenticationProvider(authorizationService(), tokenGenerator(), userDetailsService, passwordEncoder)));
		http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
		// @formatter:on
    return http.build();
  }

  @Bean
  public OAuth2AuthorizationService authorizationService() {
    return new InMemoryOAuth2AuthorizationService();
  }

  @Bean
  public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
    return new InMemoryOAuth2AuthorizationConsentService();
  }


  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    // @formatter:off
		RegisteredClient registeredClient = RegisteredClient
			.withId(UUID.randomUUID().toString())
			.clientId(clientId)
			.clientSecret(passwordEncoder.encode(clientSecret))
			.scope("read")
			.scope("write")
			.authorizationGrantType(new AuthorizationGrantType("password"))
			.tokenSettings(tokenSettings())
			.clientSettings(clientSettings())
			.build();
		// @formatter:on
    return new InMemoryRegisteredClientRepository(registeredClient);
  }

  @Bean
  public TokenSettings tokenSettings() {
    // @formatter:off
		return TokenSettings.builder()
			.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
			.accessTokenTimeToLive(Duration.ofSeconds(jwtDurationSeconds))
			.build();
		// @formatter:on
  }

  @Bean
  public ClientSettings clientSettings() {
    return ClientSettings.builder().build();
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().build();
  }

  @Bean
  public DelegatingOAuth2TokenGenerator tokenGenerator() {
    NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
    JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
    jwtGenerator.setJwtCustomizer(tokenCustomizer());
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator);
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
    return context -> {
      OAuth2ClientAuthenticationToken principal = context.getPrincipal();
      CustomUserAuthorities user = (CustomUserAuthorities) principal.getDetails();
      User newUser = userService.findByEmail(user.getUsername());
      List<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
      if (context.getTokenType().getValue().equals("access_token")) {
        // @formatter:off
				context.getClaims()
					.claim("authorities", authorities)
					.claim("nick", newUser.getNickname())
          .claim("userId", newUser.getId())
					.claim("username", user.getUsername());
				// @formatter:on
      }
    };
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    this.rsaKey = generateRsa();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
  }

  public AccessToken generateToken(String username, String nickname, List<String> authorities) {
    try {
      var issTime = Date.from(Instant.now());
      var expiration = Date.from(Instant.now().plusSeconds(jwtDurationSeconds));
      var newUsername = username == null ? nickname : username;
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
              .audience(clientId)
              .jwtID(UUID.randomUUID().toString())
              .issuer("http://localhost:8080")
              .issueTime(issTime)
              .notBeforeTime(issTime)
              .expirationTime(expiration)
              .claim("nick", nickname)
              .claim("userId", userService.findByEmail(newUsername).getId())
              .claim("username", newUsername)
              .claim("authorities", authorities)
              .build();
      SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
              .keyID(rsaKey.getKeyID()).build(), claimsSet);
      JWSSigner signer = new RSASSASigner(rsaKey.toRSAPrivateKey());
      signedJWT.sign(signer);
      String token = signedJWT.serialize();
      return new AccessToken(token, "Bearer", jwtDurationSeconds);
    } catch (JOSEException e) {
      log.error("Error generating token", e);
      return null;
    }
  }

  public RSAPublicKey readX509PublicKey() {
    try {
      String publicKeyPEM = publicKey
              .replace(BEGIN_PUBLIC_KEY, "")
              .replace(END_PUBLIC_KEY, "")
              .replaceAll(System.lineSeparator(), "")
              .replaceAll("\\s", "");
      log.info(publicKeyPEM);
      byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
      return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      log.error("Error reading public key", e);
      return null;
    }
  }

  public RSAPrivateKey readPKCS8PrivateKey() {
    try {
      String privateKeyPEM = privateKey
              .replace(BEGIN_PRIVATE_KEY, "")
              .replace(END_PRIVATE_KEY, "")
              .replaceAll(System.lineSeparator(), "")
              .replaceAll("\\s", "");
      log.info(privateKeyPEM);
      byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
      return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      log.error("Error reading private key", e);
      return null;
    }
  }


  private RSAKey generateRsa() {
    RSAPublicKey newPublicKey;
    RSAPrivateKey newPrivateKey;
    newPublicKey = readX509PublicKey();
    newPrivateKey = readPKCS8PrivateKey();
    if (newPublicKey != null && newPrivateKey != null) {
      log.info("RSA key informed, using it");
      return new RSAKey.Builder(newPublicKey).privateKey(newPrivateKey).keyID(kid).build();
    }
    log.info("RSA key not informed, generating new key pair");
    KeyPair newKeyPair = generateRsaKey();
    newPublicKey = (RSAPublicKey) newKeyPair.getPublic();
    newPrivateKey = (RSAPrivateKey) newKeyPair.getPrivate();
    return new RSAKey.Builder(newPublicKey).privateKey(newPrivateKey).keyID(kid).build();
  }

  private KeyPair generateRsaKey() {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048);
      return keyPairGenerator.generateKeyPair();
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    }
  }
}

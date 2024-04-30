package com.waly.kanban.configs;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.waly.kanban.configs.customgrant.CustomPasswordAuthenticationConverter;
import com.waly.kanban.configs.customgrant.CustomPasswordAuthenticationProvider;
import com.waly.kanban.configs.customgrant.CustomUserAuthorities;
import com.waly.kanban.dto.AccessToken;
import com.waly.kanban.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
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

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
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

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;

	@Value("${security.jwt.key-path}")
	private String keyPath;

	private static final String kid = "IkVzdGUtZS1tZXUtS2lkLVBlcnNvbmFsaXplZCI=";

	private static RSAKey rsaKey;

	@Bean
	@Order(2)
	public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {

		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

		// @formatter:off
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
			.tokenEndpoint(tokenEndpoint -> tokenEndpoint
				.accessTokenRequestConverter(new CustomPasswordAuthenticationConverter())
				.authenticationProvider(new CustomPasswordAuthenticationProvider(authorizationService(), tokenGenerator(), userDetailsService, passwordEncoder())));

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
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		// @formatter:off
		RegisteredClient registeredClient = RegisteredClient
			.withId(UUID.randomUUID().toString())
			.clientId(clientId)
			.clientSecret(passwordEncoder().encode(clientSecret))
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
	public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
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
			String nickname = userService.findUserByEmail(user.getUsername()).getNickname();
			List<String> authorities = user.getAuthorities().stream().map(x -> x.getAuthority()).toList();

			if (context.getTokenType().getValue().equals("access_token")) {
				// @formatter:off
				context.getClaims()
					.claim("authorities", authorities)
					.claim("nick", nickname)
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
		RSAKey rsaKey = generateRsa();
		this.rsaKey = rsaKey;
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

	public AccessToken generateToken(String username, String nickname, List<String> authorities) {

		try {
		var issTime = Date.from(Instant.now());
		var expiration = Date.from(Instant.now().plusSeconds(jwtDurationSeconds));
		// Crie uma instância de JWTClaimsSet.Builder
		JWTClaimsSet.Builder claimsSetBuilder = new JWTClaimsSet.Builder();

		var newUsername = username;
		if (username == null) newUsername = nickname;

		// Defina os claims do token
		var jwt = Jwts.builder()
				.setAudience(clientId)
				.setIssuer("http://localhost:9090")
				.setId(UUID.randomUUID().toString()) // jti
				.setIssuedAt(issTime) // iat
				.setNotBefore(issTime) // nbf
				.setExpiration(expiration) // exp
				.claim("nick", nickname)
				.claim("username", newUsername)
				.claim("authorities", authorities)
				.signWith(SignatureAlgorithm.RS256, rsaKey.toRSAPrivateKey())
				.compact();

		return new AccessToken(jwt, "Bearer", jwtDurationSeconds);
		} catch (JOSEException e) {
			log.error("here" + e.getMessage());
			e.printStackTrace();
            throw new RuntimeException(e);
        }
	}




	private void savePrivateKeyToFile(PrivateKey privateKey) {
		try (FileOutputStream fos = new FileOutputStream(keyPath + "private-key.pem")) {

			String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

			fos.write("-----BEGIN RSA PRIVATE KEY-----\n".getBytes());
			fos.write(privateKeyBase64.getBytes());
			fos.write("\n-----END RSA PRIVATE KEY-----\n".getBytes());

			System.out.println("Par de chaves salvo em " + keyPath + "private-key.pem");

			System.out.println("Par de chaves salvo em " + keyPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void savePublicKeyToFile(PublicKey publicKey) {
		try (FileOutputStream fos = new FileOutputStream(keyPath + "public-key.pem")) {

			String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());

			// Escrever as chaves no arquivo
			fos.write("-----BEGIN RSA PUBLIC KEY-----\n".getBytes());
			fos.write(publicKeyBase64.getBytes());
			fos.write("\n-----END RSA PUBLIC KEY-----\n".getBytes());

			System.out.println("Par de chaves salvo em " + keyPath + "public-key.pem");

			System.out.println("Par de chaves salvo em " + keyPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public RSAPublicKey readX509PublicKey() {
		try {
			String key = new String(Files.readAllBytes(Paths.get(keyPath + "public-key.pem")), Charset.defaultCharset());

			String publicKeyPEM = key
					.replace("-----BEGIN RSA PUBLIC KEY-----", "")
					.replaceAll(System.lineSeparator(), "")
					.replace("-----END RSA PUBLIC KEY-----", "");

			byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		}catch (Exception e) {
			return null;
		}
	}

	public RSAPrivateKey readPKCS8PrivateKey() throws Exception {
		String key = new String(Files.readAllBytes(Paths.get(keyPath + "private-key.pem")), Charset.defaultCharset());

		String privateKeyPEM = key
				.replace("-----BEGIN RSA PRIVATE KEY-----", "")
				.replaceAll(System.lineSeparator(), "")
				.replace("-----END RSA PRIVATE KEY-----", "");

		byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}


	private RSAKey generateRsa() {
		RSAPublicKey publicKey;
		RSAPrivateKey privateKey;
		try {
			publicKey = readX509PublicKey();
			privateKey = readPKCS8PrivateKey();
			RSAKey rsaKey1 = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(kid).build();
			if (rsaKey1 != null) {
				log.info("RSA key loaded from file");
				log.info(rsaKey1.toJSONString());
				return rsaKey1;
			}
		} catch (Exception e) {
			log.error("RSA key not found in file");
		}
		KeyPair keyPair = generateRsaKey();
		publicKey = (RSAPublicKey) keyPair.getPublic();
		privateKey = (RSAPrivateKey) keyPair.getPrivate();
		savePublicKeyToFile(publicKey);
		savePrivateKeyToFile(privateKey);
		return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(kid).build();
	}

	private KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	public RSAKey getRsaKey() {
		return rsaKey;
	}
}

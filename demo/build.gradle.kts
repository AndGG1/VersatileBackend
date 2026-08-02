plugins {
	kotlin("jvm") version "2.0.21"
	kotlin("plugin.spring") version "2.0.21"
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.plcoding"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	//Redis Starters
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	//Reactive Streams
//	implementation("org.reactivestreams:reactive-streams")
//	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// Qdrant & gRPC Core
	implementation("io.qdrant:client:1.11.0")
	implementation("io.grpc:grpc-netty-shaded:1.62.2") // Handles native SSL & SNI over HTTP/2 safely
	implementation("io.grpc:grpc-stub:1.62.2")
	implementation("io.grpc:grpc-protobuf:1.62.2")

	// Kotlin & Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.9.0") // <-- Added right here! Provides the .await() function
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.security:spring-security-crypto")
	compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

	// JSON Web Tokens (JJWT)
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	// Test Frameworks & Testcontainers
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:qdrant:1.20.1")
	testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.12.2")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

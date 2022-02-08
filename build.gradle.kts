import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    id("java")
    application
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "uk.wycor.starlines"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val vertxVersion = "4.2.4"
val junitJupiterVersion = "5.8.2"
val neo4jCoreVersion = "3.2.28"
val neo4jDriverVersion = "4.4.3"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

dependencies {
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("org.neo4j.driver:neo4j-java-driver:$neo4jDriverVersion")
    // implementation("org.neo4j:neo4j-ogm-core:$neo4jCoreVersion")

    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    // implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")


    runtimeOnly("org.neo4j:neo4j-ogm-bolt-driver:$neo4jCoreVersion")
    runtimeOnly("org.neo4j:neo4j-ogm-bolt-native-types:$neo4jCoreVersion")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    testCompileOnly("org.projectlombok:lombok:1.18.22")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.22")

    testImplementation("org.neo4j.test:neo4j-harness:$neo4jDriverVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

application {
    mainClass.set("uk.wycor.starlines.web.WebServer")
}
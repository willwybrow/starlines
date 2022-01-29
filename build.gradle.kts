import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    id("java")
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
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

val mainVerticleName = "$group.web.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
    mainClass.set(launcherClassName)
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.22")
    implementation("org.neo4j.driver:neo4j-java-driver:$neo4jDriverVersion")
    implementation("org.neo4j:neo4j-ogm-core:$neo4jCoreVersion")

    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-core:$vertxVersion")
    implementation("io.vertx:vertx-web:$vertxVersion")

    runtimeOnly("org.neo4j:neo4j-ogm-bolt-driver:$neo4jCoreVersion")
    runtimeOnly("org.neo4j:neo4j-ogm-bolt-native-types:$neo4jCoreVersion")

    compileOnly("org.projectlombok:lombok:1.18.22")
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    testCompileOnly("org.projectlombok:lombok:1.18.22")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.22")

    testImplementation("io.vertx:vertx-junit5")
    testImplementation("org.neo4j.test:neo4j-harness:$neo4jDriverVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}

tasks.withType<JavaExec> {
    args = listOf("run", mainVerticleName, /* "--redeploy=$watchForChange",*/ "--launcher-class=$launcherClassName", /*"--on-redeploy=$doOnChange"*/)
}
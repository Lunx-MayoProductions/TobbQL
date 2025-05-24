plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("7.1.2")
    `maven-publish`
}
publishing {
    repositories {
        maven {
            name = "tobbql"
            url = uri("https://repo.lunx-dev.de/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "de.lunx"
            artifactId = "tobbql"
            version = "6003"
            from(components["java"])
        }
    }
}

group = "de.lunx"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("com.sparkjava:spark-core:2.9.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveVersion = ""
}

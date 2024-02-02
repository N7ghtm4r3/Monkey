plugins {
    id("java")
    id("maven-publish")
}

group = "com.tecknobit"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.simplejavamail:simple-java-mail:8.6.2")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.tecknobit.monkey"
                artifactId = "Monkey"
                version = "1.0.0"
                from(components["java"])
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
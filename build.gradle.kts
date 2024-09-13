
plugins {
    id("java")
    id("java-library")
}

group = "me.cambria22118626"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.0.1")
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "me.cambria22118626.Main"
    }
}
tasks.test {
    useJUnitPlatform()
}

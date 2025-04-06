
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
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
}
tasks.jar {
    manifest {
        attributes["Main-Class"] = "me.cambria22118626.Main"
    }
    // Include project code
    from(sourceSets.main.get().output)

    // Include dependencies (external libraries)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
tasks.test {
    useJUnitPlatform()
}

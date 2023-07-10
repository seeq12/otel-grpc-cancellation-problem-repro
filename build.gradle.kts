import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm") version "1.7.21"
    application
    id("com.google.protobuf") version "0.9.3"
}

group = "com.seeq.repro"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val grpcVersion = "1.56.0"
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-services:$grpcVersion")
    implementation("com.google.protobuf:protobuf-kotlin:3.22.2")

    runtimeOnly("io.grpc:grpc-netty:$grpcVersion")

    testImplementation(kotlin("test"))
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.56.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    tasks {
        generateProtoTasks {
            all().forEach {
                it.plugins {
                    id("grpc")
                    id("grpckt")
                }
                it.builtins {
                    id("kotlin")
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.seeq.repro.MainKt")
}
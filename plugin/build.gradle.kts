import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("kotlin")
    id("com.vanniktech.maven.publish")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(gradleApi())
    implementation(project(":lib"))
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))

//    implementation("org.ow2.asm:asm:9.2")
//    implementation("org.ow2.asm:asm-commons:9.2")

    implementation("com.android.tools.build:gradle:7.0.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
}

mavenPublish {
    sonatypeHost = com.vanniktech.maven.publish.SonatypeHost.S01
}

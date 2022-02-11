plugins {
  kotlin("js") version "1.6.10"
}

group = "me.joonsoo"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

fun kotlinw(target: String): String =
  "org.jetbrains.kotlin-wrappers:kotlin-$target"

dependencies {
  testImplementation(kotlin("test"))
  // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-react
  implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.294-kotlin-1.6.10")
  // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-react-dom
  implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:17.0.2-pre.294-kotlin-1.6.10")

  // MUI
  // https://github.com/karakum-team/kotlin-mui-showcase/blob/main/build.gradle.kts
  // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-mui
  implementation("org.jetbrains.kotlin-wrappers:kotlin-mui:5.4.0-pre.294-kotlin-1.6.10")
  // https://mvnrepository.com/artifact/org.jetbrains.kotlin-wrappers/kotlin-mui-icons
  implementation("org.jetbrains.kotlin-wrappers:kotlin-mui-icons:5.3.1-pre.294-kotlin-1.6.10")
  implementation(npm("@emotion/react", "11.7.1"))
  implementation(npm("@emotion/styled", "11.6.0"))
}

kotlin {
  js(IR) {
    binaries.executable()
    browser {
      commonWebpackConfig {
        cssSupport.enabled = true
      }
    }
  }
}

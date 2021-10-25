import org.gradle.jvm.tasks.*

plugins {
    kotlin("js") version "1.4.10"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.3")
}

repositories{
    mavenCentral()
}

kotlin {
    js {
        sourceSets["main"].apply {
            kotlin.srcDir("submodules/jasmine/plugins/com.gridnine.jasmine.web.core/source")
            kotlin.srcDir("submodules/jasmine/plugins/com.gridnine.jasmine.web.standard/source")
            kotlin.srcDir("submodules/jasmine/plugins/com.gridnine.jasmine.web.standard/source-gen")
            kotlin.srcDir("submodules/jasmine/plugins/com.gridnine.jasmine.web.reports/source")
            kotlin.srcDir("submodules/jasmine/plugins/com.gridnine.jasmine.web.reports/source-gen")
            kotlin.srcDir("submodules/jasmine/plugins/com.gridnine.jasmine.web.easyui/source")
            kotlin.srcDir("submodules/jasmine/plugins/com.gridnine.jasmine.web.antd/source")
            kotlin.srcDir("plugins/com.flinty.docsflow.web.core/source")
            kotlin.srcDir("plugins/com.flinty.docsflow.web.core/source-gen")
        }
        browser{
            distribution {
                directory = File(project.rootDir, "temp/js/output/")
            }
        }
    }
}

task("_unzip_war", Copy::class) {
    doFirst{
        println("deleting directory")
        val file = project.file("temp/war/input")
        if(file.exists()){
            file.deleteRecursively()
        }
    }
    shouldRunAfter("build")
    from(zipTree(file("build/dist/lib/docsflow-index.war")))
    into(project.file("temp/war/input"))
}

task("update-index-war", Jar::class){
    dependsOn("_unzip_war")
    destinationDirectory.set(project.file("temp/war/output"))
    from(project.file("temp/war/input"))
    archiveFileName.set("docsflow-index.war")
    doFirst{
        project.file("temp/js/output/coralina-docs-flow.js").copyTo(project.file("temp/war/input/coralina-docs-flow.js"))
        project.file("temp/js/output/coralina-docs-flow.js.map").copyTo(project.file("temp/war/input/coralina-docs-flow.js.map"))
        project.file("temp/war/input/index.html").delete()
        project.file("temp/war/input/index-prod.html").renameTo(project.file("temp/war/input/index.html"))
    }
    doLast{
        project.file("build/dist/lib/docsflow-index.war").delete()
        project.file("temp/war/output/docsflow-index.war").renameTo(project.file("build/dist/lib/docsflow-index.war"))
    }
}
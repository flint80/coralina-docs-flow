import com.gridnine.jasmine.gradle.plugin.jasmine

buildscript {
    dependencies{
        "classpath"(files("submodules/jasmine/lib/spf-1.0.jar"))
        "classpath"(files("gradle/jasmine-gradle-plugin.jar"))
    }
}
plugins {
    id("com.github.node-gradle.node") version("3.1.0")
}


apply<com.gridnine.jasmine.gradle.plugin.JasmineConfigPlugin>()


jasmine {
    kotlinVersion = "1.4.10"
    kotlinCoroutinesJSVersion ="1.4.3"
    libRelativePath = "submodules/jasmine/lib"
    enableWebTasks = true
    plugins("submodules/jasmine/plugins") {
        plugin("com.gridnine.jasmine.common.core")
        plugin("com.gridnine.jasmine.common.spf")
        plugin("com.gridnine.jasmine.common.core.test")
        plugin("com.gridnine.jasmine.server.core")
        plugin("com.gridnine.jasmine.server.db.h2")
        plugin("com.gridnine.jasmine.server.db.postgres")
        plugin("com.gridnine.jasmine.server.core.test")
        plugin("com.gridnine.jasmine.common.standard")
        plugin("com.gridnine.jasmine.server.standard")
        plugin("com.gridnine.jasmine.server.standard.test")
        plugin("com.gridnine.jasmine.common.reports")
        plugin("com.gridnine.jasmine.server.reports")
        plugin("com.gridnine.jasmine.web.core")
        plugin("com.gridnine.jasmine.web.core.test")
        plugin("com.gridnine.jasmine.web.standard")
        plugin("com.gridnine.jasmine.web.reports")
        plugin("com.gridnine.jasmine.web.easyui")
        plugin("com.gridnine.jasmine.web.antd")
    }
    plugins("plugins") {
        plugin("com.flinty.docsflow.common.core")
        plugin("com.flinty.docsflow.server.core")
        plugin("com.flinty.docsflow.server.core.test")
        plugin("com.flinty.docsflow.web.core")
    }
}

repositories{
    mavenCentral()
}

apply<com.gridnine.jasmine.gradle.plugin.JasminePlugin>()


// task("deploy-locally", DeployApplicationTask::class){
//     group = "jenkins"
//     shouldRunAfter("jenkins-dist")
//     host = "localhost"
//     port = 21567
// }


//tasks.create("deploy-test", Deplo)

//project.configurations.create("compile")
//
//dependencies{
//    "compile"(files("submodules/jasmine/lib/spf-1.0.jar"))
//}


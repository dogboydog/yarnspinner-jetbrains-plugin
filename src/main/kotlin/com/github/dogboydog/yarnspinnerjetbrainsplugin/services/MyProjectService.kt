package com.github.dogboydog.yarnspinnerjetbrainsplugin.services

import com.intellij.openapi.project.Project
import com.github.dogboydog.yarnspinnerjetbrainsplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}

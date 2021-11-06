package com.zsqw123.inject.plugin

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.zsqw123.inject.plugin.task.BaseTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileType
import org.gradle.work.InputChanges

class InjectPlugin : Plugin<Project> {
    @Suppress("UnstableApiUsage")
    override fun apply(target: Project) {
        val extension = target.extensions.findByType(AndroidComponentsExtension::class.java) ?: return
        val injectTask = target.tasks.register(Const.taskCatInjectFind, InjectTask::class.java)

        extension.onVariants { variant ->
            variant.artifacts.get(SingleArtifact.APK)
//            it.transformClassesWith(InjectTransform::class.java, InstrumentationScope.ALL) {}
//            it.transformClassesWith(InjectsModifierTransform::class.java, InstrumentationScope.ALL) {}
//                .use(injectTask).wiredWithDirectories(InjectTask::inputDir, InjectTask::outputDir)
//                .toTransform(ArtifactType.SINGLE_DIR_ARTIFACT)
        }

        println("CatInject Plugin Loaded!")
    }
}

abstract class InjectTask : BaseTask() {
    override fun execute(inputChanges: InputChanges) {
        pluginLog(if (inputChanges.isIncremental) "Executing incrementally" else "Executing non-incrementally")

        inputChanges.getFileChanges(inputDir).forEach { fc ->
            if (fc.fileType == FileType.DIRECTORY || fc.fileType == FileType.MISSING) return@forEach
        }


    }
}
/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.kotlin.dsl.tooling.builders.internal

import org.gradle.api.Project
import org.gradle.kotlin.dsl.provider.PrecompiledScriptPluginsSupport
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.kotlin.dsl.tooling.builders.EnclosingSourceSet
import org.gradle.kotlin.dsl.tooling.builders.NoSourceSet
import org.gradle.kotlin.dsl.tooling.builders.findSourceSetOf
import org.gradle.tooling.provider.model.ToolingModelBuilder
import java.io.File


internal
data class DiscoveredScript(
    val script: File,
    val enclosingSourceSet: EnclosingSourceSet?
)


internal
data class DiscoveredKotlinScriptsModel(
    val scripts: List<DiscoveredScript>
)


internal
object DiscoveredKotlinScriptsModelBuilder : ToolingModelBuilder {

    override fun canBuild(modelName: String): Boolean =
        modelName == DiscoveredKotlinScriptsModel::class.qualifiedName

    override fun buildAll(modelName: String, project: Project): DiscoveredKotlinScriptsModel {
        val scripts = buildList {
            // Project Scripts
            if (project.buildFile.isFile && project.buildFile.hasKotlinDslExtension) {
                add(DiscoveredScript(project.buildFile, NoSourceSet))
            }

            // Precompiled Scripts
            if (project.plugins.hasPlugin("org.gradle.kotlin.kotlin-dsl")) {
                val precompiledScriptFiles = project.precompiledScriptPluginsSupport.collectScriptPluginFilesOf(project)
                val precompiledScripts = precompiledScriptFiles.map { DiscoveredScript(it, project.findSourceSetOf(it)) }
                addAll(precompiledScripts)
            }
        }

        return DiscoveredKotlinScriptsModel(scripts)
    }
}


private
val Project.precompiledScriptPluginsSupport
    get() = serviceOf<PrecompiledScriptPluginsSupport>()

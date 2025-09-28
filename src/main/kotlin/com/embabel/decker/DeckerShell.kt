/*
 * Copyright 2024-2025 Embabel Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.embabel.decker

import com.embabel.agent.api.common.autonomy.AgentInvocation
import com.embabel.agent.core.AgentPlatform
import com.embabel.agent.core.ProcessOptions
import com.embabel.agent.core.Verbosity
import com.embabel.agent.domain.io.FileArtifact
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.core.io.ResourceLoader
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.nio.charset.Charset

@ShellComponent("Presentation maker commands")
class DeckerShell(
    private val agentPlatform: AgentPlatform,
    private val resourceLoader: ResourceLoader,
    private val objectMapper: ObjectMapper,
) {
    @ShellMethod("Create a slide deck from a YAML description")
    fun deck(
        @ShellOption(
            defaultValue = "file:/Users/rjohnson/dev/embabel.com/decker/inputs/kotlinconf_presentation.yml",
        )
        file: String,
    ): String {
        val yamlReader = ObjectMapper(YAMLFactory()).registerKotlinModule()

        val presentationRequest = yamlReader.readValue(
            resourceLoader.getResource(file).getContentAsString(Charset.defaultCharset()),
            PresentationRequest::class.java,
        )

        val fileArtifact = AgentInvocation.builder(agentPlatform)
            .options(ProcessOptions(verbosity = Verbosity(showPrompts = true)))
            .build(FileArtifact::class.java)
            .invoke(presentationRequest)

        return "Deck created at ${fileArtifact.file.absolutePath}"
    }
}

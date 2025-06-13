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

import com.embabel.agent.api.common.autonomy.AgentProcessExecution
import com.embabel.agent.core.AgentPlatform
import com.embabel.agent.core.ProcessOptions
import com.embabel.agent.core.Verbosity
import com.embabel.agent.event.logging.personality.severance.LumonColorPalette
import com.embabel.agent.shell.formatProcessOutput
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.core.io.ResourceLoader
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.nio.charset.Charset

@ShellComponent("Presentation maker commands")
class PresentationMakerShell(
    private val agentPlatform: AgentPlatform,
    private val resourceLoader: ResourceLoader,
    private val objectMapper: ObjectMapper,
) {
    @ShellMethod
    fun makePresentation(
        @ShellOption(
            defaultValue = "file:/Users/rjohnson/dev/embabel.com/embabel-agent/embabel-agent-api/src/main/kotlin/com/embabel/examples/dogfood/presentation/kotlinconf_presentation.yml",
        )
        file: String,
    ): String {
        val yamlReader = ObjectMapper(YAMLFactory()).registerKotlinModule()

        val presentationRequest = yamlReader.readValue(
            resourceLoader.getResource(file).getContentAsString(Charset.defaultCharset()),
            PresentationRequest::class.java,
        )

        val agentProcess = agentPlatform.runAgentWithInput(
            agent = agentPlatform.agents().single { it.name == "PresentationMaker" },
            input = presentationRequest,
            processOptions = ProcessOptions(verbosity = Verbosity(showPrompts = true)),
        )

        return formatProcessOutput(
            result = AgentProcessExecution.Companion.fromProcessStatus(
                basis = presentationRequest,
                agentProcess = agentProcess
            ),
            colorPalette = LumonColorPalette,
            objectMapper = objectMapper,
            lineLength = 140,
        ) + "\ndeck is at ${presentationRequest.outputDirectory}/${presentationRequest.outputFile}"
    }
}

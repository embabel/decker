package com.embabel.decker.shell

import com.embabel.agent.core.*
import com.embabel.agent.core.Budget.Companion.DEFAULT_TOKEN_LIMIT
import com.embabel.agent.domain.io.FileArtifact
import com.embabel.decker.PresentationRequest
import com.embabel.decker.data.DataManager
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.core.io.ResourceLoader
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.nio.charset.Charset
import java.nio.file.Path

@ShellComponent("Presentation maker commands")
class DeckerShell(
    private val agentPlatform: AgentPlatform,
    private val resourceLoader: ResourceLoader,
    private val dataManager: DataManager,
    platform: AgentPlatform,
) {

    @ShellMethod("load docs from data/docs")
    fun loadDocs(): String {
        val dir = Path.of(System.getProperty("user.dir"), "data", "docs").toString()
        val directoryParsingResult =
            dataManager.ingestDirectory(dir)
        return "Loaded docs: " + directoryParsingResult
    }

    @ShellMethod("Create a slide deck from a YAML description")
    fun deck(
        @ShellOption(
            defaultValue = "file:/Users/rjohnson/dev/embabel.com/decker/inputs/goto_cph_25.yml",
        )
        file: String,
    ): String {
        val yamlReader = ObjectMapper(YAMLFactory()).registerKotlinModule()

        val presentationRequest = yamlReader.readValue(
            resourceLoader.getResource(file).getContentAsString(Charset.defaultCharset()),
            PresentationRequest::class.java,
        )

        val agentProcess = agentPlatform.createAgentProcess(
            agentPlatform.agents().find { it.name == "Decker" } ?: error("No Decker agent"),
            ProcessOptions(
                verbosity = Verbosity(showPrompts = true),
                budget = Budget(cost = 10.0, tokens = DEFAULT_TOKEN_LIMIT * 10),
            ),
            mapOf("it" to presentationRequest),
        ).run()

        val fileArtifact = agentProcess.last<FileArtifact>() ?: error("No file artifact produced")

        println("Cost: $${agentProcess.cost()}")
        return "Deck created at ${fileArtifact.file.absolutePath}"
    }
}
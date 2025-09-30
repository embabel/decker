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

import com.embabel.agent.api.annotation.*
import com.embabel.agent.api.common.Ai
import com.embabel.agent.api.common.OperationContext
import com.embabel.agent.api.common.create
import com.embabel.agent.api.dsl.parallelMap
import com.embabel.agent.core.CoreToolGroups
import com.embabel.agent.domain.io.FileArtifact
import com.embabel.agent.domain.library.ResearchReport
import com.embabel.agent.domain.library.ResearchTopics
import com.embabel.agent.prompt.persona.Actor
import com.embabel.agent.prompt.persona.RoleGoalBackstory
import com.embabel.agent.rag.HyDE
import com.embabel.agent.rag.tools.RagOptions
import com.embabel.common.ai.model.LlmOptions
import com.embabel.common.ai.model.ModelSelectionCriteria.Companion.byRole
import com.embabel.common.core.types.SimilarityCutoff
import com.embabel.common.core.types.ZeroToOne
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated

data class ResearchResult(
    val topicReports: List<ResearchReport>,
)

@Validated
@ConfigurationProperties(prefix = "decker")
data class DeckerConfig(
    @NestedConfigurationProperty val planner: Actor<RoleGoalBackstory>,
    @NestedConfigurationProperty val researcher: Actor<RoleGoalBackstory>,
    @NestedConfigurationProperty val creator: Actor<RoleGoalBackstory>,
    override val similarityThreshold: ZeroToOne = .6,
    override val topK: Int = 8,
    val concurrencyLevel: Int = 10,
) : SimilarityCutoff {

    fun ragOptions(): RagOptions {
        return RagOptions()
            .withSimilarityThreshold(similarityThreshold)
            .withTopK(topK)
            .withHyDE(HyDE(40))
    }
}


/**
 * Agent that generates slide decks.
 */
@Agent(description = "Presentation maker. Build a presentation on a topic")
class Decker(
    private val slideFormatter: SlideFormatter,
    private val filePersister: FilePersister,
    private val config: DeckerConfig,
) {

    private val logger = LoggerFactory.getLogger(Decker::class.java)

    init {
        logger.info("Decker initialized with config: {}", config)
    }

    @Action
    fun identifyResearchTopics(
        presentationRequest: PresentationRequest,
        ai: Ai
    ): ResearchTopics =
        config.planner.promptRunner(ai)
            // TODO this should be changed to RAG as a reference
            .withRag(config.ragOptions())
            .withReferences(presentationRequest.llmReferences)
            .create(
                """
                Create a list of research topics for a presentation,
                based on the given input:
                ${presentationRequest.brief}
                About the presenter: ${presentationRequest.presenterBio}
                """.trimIndent()
            )

    @Action
    fun researchTopics(
        researchTopics: ResearchTopics,
        presentationRequest: PresentationRequest,
        context: OperationContext,
    ): ResearchResult {
        val topicReports = researchTopics.topics.parallelMap(
            context = context,
            concurrencyLevel = config.concurrencyLevel
        ) {
            config.researcher.promptRunner(context)
                // TODO this should be changed to RAG as a reference
                .withRag(config.ragOptions())
                .withReferences(presentationRequest.llmReferences)
                .withPromptContributor(presentationRequest)
                .create<ResearchReport>(
                    """
            Given the following topic and the goal to create a presentation
            for this audience, create a research report with content of no more than
            ${presentationRequest.researchReportMaxWords} words (excluding links).
            Use web tools to research and the find tools to look
            within the given references.
            Always look for code examples in the project before using the web.
            Topic: ${it.topic}
            Questions:
            ${it.questions.joinToString("\n")}
                """.trimIndent()
                )
        }
        return ResearchResult(
            topicReports = topicReports
        )
    }

    @Action
    fun createDeck(
        presentationRequest: PresentationRequest,
        researchComplete: ResearchResult,
        ai: Ai,
    ): SlideDeck {
        val slideDeck = config.creator.promptRunner(ai)
            .withPromptContributor(presentationRequest)
            .withReferences(presentationRequest.llmReferences)
            // TODO this should be changed to RAG as a reference
            .withRag(config.ragOptions())
            .withTemplate("create_deck")
            .createObject(
                SlideDeck::class.java,
                mapOf(
                    "presentationRequest" to presentationRequest,
                    "topicReports" to researchComplete.topicReports,
                )
            )
        filePersister.saveFile(
            directory = presentationRequest.outputDirectory,
            fileName = presentationRequest.rawOutputFile(),
            content = slideDeck.deck,
        )
        return slideDeck
    }

    /**
     * We use outputBindings -> @RequireNameMatch to perform a series of steps here
     */
    @Action(outputBinding = "withDiagrams", cost = 1.0)
    fun expandDigraphs(
        slideDeck: SlideDeck,
        presentationRequest: PresentationRequest,
    ): SlideDeck {
        val diagramExpander = DotCliDigraphExpander(
            directory = presentationRequest.outputDirectory,
        )
        val withDigraphs = slideDeck.expandDigraphs(diagramExpander)
        filePersister.saveFile(
            directory = presentationRequest.outputDirectory,
            fileName = presentationRequest.withDiagramsOutputFile(),
            content = withDigraphs.deck,
        )
        return slideDeck
    }

    @Action(outputBinding = "withDiagrams")
    fun loadWithDigraphs(
        presentationRequest: PresentationRequest,
    ): SlideDeck? {
        return filePersister.loadFile(
            directory = presentationRequest.outputDirectory,
            fileName = presentationRequest.withDiagramsOutputFile(),
        )?.let {
            SlideDeck(it)
        }
    }

    @Action(outputBinding = "withIllustrations")
    fun addIllustrations(
        @RequireNameMatch withDiagrams: SlideDeck,
        presentationRequest: PresentationRequest,
        context: OperationContext,
    ): SlideDeck {
        val deckWithIllustrations = if (!presentationRequest.autoIllustrate) {
            logger.info("Not auto illustrating")
            withDiagrams
        } else {
            logger.info("Asking LLM to add illustrations to this resource")

            val illustrator = context.ai()
                .withLlm(
                    LlmOptions(byRole("illustrator")).withTemperature(.3)
                )
                .withTools(CoreToolGroups.WEB)
            val newSlides = context.parallelMap(
                items = withDiagrams.slides(),
                maxConcurrency = config.concurrencyLevel
            ) { slide ->
                val newContent = illustrator.generateText(
                    """
                Take the following slide in MARP format.
                The content is inside <slide> tags.
                Overall objective: ${presentationRequest.brief}

                If the slide contains an important point, try to add an image to it
                DO NOT DO THIS FOR EVERY SLIDE--only where it may make an impact
                Check that the image is available.
                Format it so that you don't make the image too big.
                Put the image on the right.
                Make no other changes.
                Do not perform any web research besides seeking images.
                Return nothing but the amended slide content (the content between <slide></slide>).
                Do not include <slide> tags
                Do not ask any questions.
                If you don't think an image is needed, return the slide unchanged.

                <slide>
                ${slide.content}
                </slide>
            """.trimIndent()
                )
                Slide(
                    number = slide.number,
                    content = newContent,
                )
            }
            var dwi = withDiagrams
            for (slide in newSlides) {
                dwi = dwi.replaceSlide(slide, slide.content)
            }
            dwi
        }

        logger.info(
            "Saving final MARP markdown to {}/{}",
            presentationRequest.outputDirectory,
            presentationRequest.outputFile,
        )
        filePersister.saveFile(
            directory = presentationRequest.outputDirectory,
            fileName = presentationRequest.outputFile,
            content = deckWithIllustrations.deck,
        )
        return withDiagrams
    }

    @AchievesGoal(
        description = "Create a presentation based on research reports",
        export = Export(remote = true),
    )
    @Action
    fun convertToSlides(
        presentationRequest: PresentationRequest,
        @RequireNameMatch withIllustrations: SlideDeck,
    ): FileArtifact {
        val htmlFile = slideFormatter.createHtmlSlides(
            directory = presentationRequest.outputDirectory,
            markdownFilename = presentationRequest.outputFile,
        )
        return FileArtifact(
            directory = presentationRequest.outputDirectory,
            outputFile = htmlFile,
        )
    }

}

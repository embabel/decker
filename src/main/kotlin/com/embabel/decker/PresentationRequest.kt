package com.embabel.decker

import com.embabel.agent.api.common.LlmReference
import com.embabel.agent.prompt.persona.CoStar
import com.embabel.common.ai.prompt.PromptContributor

/**
 * @param brief the content of the presentation. Can be short
 * or detailed
 * @param autoIllustrate ask the LLM to provide illustrations. Not yet dependable
 */
data class PresentationRequest(
    val slideCount: Int,
    val presenterBio: String,
    val brief: String,
    val references: List<ReferenceSpec>,
    val outputDirectory: String = "/Users/rjohnson/Documents",
    val outputFile: String = "presentation.md",
    val header: String,
    val images: Map<String, ImageInfo> = emptyMap(),
    val autoIllustrate: Boolean = false,
    //val slidesToInclude: String,
    val coStar: CoStar,
) : PromptContributor by coStar {

    fun references(): List<LlmReference> =
        references.map { it.reference() }


    /**
     * File name for interim artifact with raw deck
     */
    fun rawOutputFile(): String {
        return outputFile.replace(".md", ".raw.md")
    }

    fun withDiagramsOutputFile(): String {
        return outputFile.replace(".md", ".withDiagrams.md")
    }
}

data class ImageInfo(val url: String, val useWhen: String)


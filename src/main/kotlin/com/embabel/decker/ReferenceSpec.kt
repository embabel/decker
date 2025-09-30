package com.embabel.decker

import com.embabel.agent.api.common.LlmReference
import com.embabel.coding.tools.git.RepositoryReferenceProvider
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Serializable reference
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = GitHubRepository::class, name = "github"),
    JsonSubTypes.Type(value = WebPage::class, name = "webpage")
)
sealed interface ReferenceSpec {
    fun reference(): LlmReference
}

data class GitHubRepository(
    val url: String,
    val description: String = "GitHub repository at $url",
) : ReferenceSpec {
    override fun reference(): LlmReference =
        RepositoryReferenceProvider.create().cloneRepository(url = url, description = description)
}

data class WebPage(
    val url: String,
    override val description: String = "Web page at $url",
) : ReferenceSpec, LlmReference {
    override fun reference(): LlmReference = this

    override fun notes(): String {
        return "Refer to this web page: use the fetch tool"
    }

    override val name: String
        get() = url
}
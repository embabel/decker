package com.embabel.decker.config

import com.embabel.agent.rag.RagService
import com.embabel.agent.rag.ingestion.ContentChunker
import com.embabel.agent.rag.lucene.LuceneRagFacetProvider
import com.embabel.agent.rag.support.FacetedRagService
import com.embabel.common.ai.model.DefaultModelSelectionCriteria
import com.embabel.common.ai.model.ModelProvider
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.io.path.Path

@Configuration
class RagConfiguration {

    private val logger = LoggerFactory.getLogger(RagConfiguration::class.java)

    @Bean
    fun luceneRagFacetProvider(modelProvider: ModelProvider): LuceneRagFacetProvider {
        val embeddingService = modelProvider.getEmbeddingService(DefaultModelSelectionCriteria)
        logger.info(
            "Using embedding service {} with dimensions {}",
            embeddingService.name,
            embeddingService.model.dimensions()
        )
        val lucene = LuceneRagFacetProvider(
            "docs",
            embeddingService.model,
            0.5,
            ContentChunker.DefaultConfig(),
            Path("./lucene-index")
        )
        lucene.loadExistingChunksFromDisk()
        return lucene
    }

    @Bean
    fun ragService(facetProviders: List<LuceneRagFacetProvider>): RagService {
        return FacetedRagService(
            name = "Information about Embabel",
            facets = emptyList(),
            facetProviders = facetProviders,
        )
    }
}

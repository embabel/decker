package com.embabel.decker.config

import com.embabel.agent.rag.ingestion.ContentChunker
import com.embabel.agent.rag.lucene.LuceneSearchOperations
import com.embabel.agent.rag.service.RagService
import com.embabel.agent.rag.service.support.FacetedRagService
import com.embabel.agent.rag.service.support.RagFacetProvider
import com.embabel.common.ai.model.DefaultModelSelectionCriteria
import com.embabel.common.ai.model.ModelProvider
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Path

@Configuration
class RagConfiguration {

    private val logger = LoggerFactory.getLogger(RagConfiguration::class.java)

    @Bean
    fun luceneSearchOperations(modelProvider: ModelProvider): LuceneSearchOperations {
        val embeddingService = modelProvider.getEmbeddingService(DefaultModelSelectionCriteria)
        logger.info(
            "Using embedding service {} with dimensions {}",
            embeddingService.name,
            embeddingService.dimensions,
        )
        return LuceneSearchOperations.builder()
            .withName("docs")
            .withEmbeddingService(embeddingService)
            .withChunkerConfig(ContentChunker.Config())
            .withIndexPath(Path.of("./lucene-index"))
            .buildAndLoadChunks()
    }

    @Bean
    fun ragService(facetProviders: List<RagFacetProvider>): RagService {
        return FacetedRagService(
            name = "Information about Embabel",
            facets = emptyList(),
            facetProviders = facetProviders,
        )
    }
}

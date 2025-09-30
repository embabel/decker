package com.embabel.decker.config;

import com.embabel.agent.rag.RagService;
import com.embabel.agent.rag.ingestion.ContentChunker;
import com.embabel.agent.rag.lucene.LuceneRagFacetProvider;
import com.embabel.agent.rag.support.FacetedRagService;
import com.embabel.common.ai.model.DefaultModelSelectionCriteria;
import com.embabel.common.ai.model.ModelProvider;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.util.List;


@Configuration
class RagConfiguration {

    @Bean
    LuceneRagFacetProvider luceneRagFacetProvider(ModelProvider modelProvider) {
        var embeddingService = modelProvider.getEmbeddingService(DefaultModelSelectionCriteria.INSTANCE);
        LoggerFactory.getLogger(RagConfiguration.class).info(
                "Using embedding service {} with dimensions {}",
                embeddingService.getName(),
                embeddingService.getModel().dimensions()
        );
        var lucene = new LuceneRagFacetProvider(
                "docs",
                embeddingService.getModel(),
                0.5,
                new ContentChunker.DefaultConfig(),
                Paths.get("./lucene-index")
        );
        lucene.loadExistingChunksFromDisk();
        return lucene;
    }

    @Bean
    RagService ragService(List<LuceneRagFacetProvider> facetProviders) {
        return new FacetedRagService(List.of(), facetProviders);
    }
}

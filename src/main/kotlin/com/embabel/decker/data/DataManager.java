package com.embabel.decker.data;

import com.embabel.agent.api.common.LlmReference;
import com.embabel.agent.identity.User;
import com.embabel.agent.rag.WritableContentElementRepository;
import com.embabel.agent.rag.ingestion.DirectoryParsingResult;
import com.embabel.agent.rag.ingestion.HierarchicalContentReader;
import com.embabel.agent.rag.tools.RagOptions;
import com.embabel.agent.tools.file.FileTools;
import com.embabel.coding.tools.api.ApiReference;
import com.embabel.coding.tools.git.RepositoryReferenceProvider;
import com.embabel.coding.tools.jvm.ClassGraphApiReferenceExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Exposes the guide configuration and the loaded references
 */
@Service
public class DataManager {

    private final Logger logger = LoggerFactory.getLogger(DataManager.class);
    private final List<LlmReference> references = new LinkedList<>();
    private final WritableContentElementRepository store;

    public DataManager(
            WritableContentElementRepository store
    ) {
        this.store = store;
//        this.guideConfig = guideConfig;
        var embabelAgentApiReference = new ApiReference(
                "Embabel Agent API: Core",
                new ClassGraphApiReferenceExtractor().fromProjectClasspath(
                        "embabel-agent",
                        Set.of("com.embabel.agent", "com.embabel.common"),
                        Set.of()),
                100);
        references.add(embabelAgentApiReference);

        // TODO this could be data driven
        addGithubReference("https://github.com/embabel/embabel-agent-examples.git", "Embabel examples repo");
        addGithubReference("https://github.com/embabel/embabel-agent.git", "Embabel agent implementation repo: Look to check code under embabel-agent-api");
    }

    private void addGithubReference(@NonNull String repoUrl, @NonNull String description) {
        try {
            var examplesReference = RepositoryReferenceProvider.create()
                    .cloneRepository(repoUrl, description);
            references.add(examplesReference);
            logger.info("Loaded Github repo {} for tool access", repoUrl);
        } catch (Throwable t) {
            // Allows working offline
            logger.warn("Failed to load Github repo {} for tool access", repoUrl);
        }
    }

//    @NonNull
//    public LaunchpadConfig config() {
//        return guideConfig;
//    }

    @NonNull
    public List<LlmReference> referencesForUser(@Nullable User user) {
        return Collections.unmodifiableList(references);
    }

    public void provisionDatabase() {
        store.provision();
    }

    //    @Transactional(readOnly = true)
    public int count() {
        return store.count();
    }

    /**
     * Read all files under this directory
     *
     * @param dir absolute path
     */
    public DirectoryParsingResult ingestDirectory(String dir) {
        store.provision();

        var ft = FileTools.readOnly(dir);

        var directoryParsingResult = new HierarchicalContentReader()
                .parseFromDirectory(ft);
        for (var root : directoryParsingResult.getContentRoots()) {
            logger.info("Parsed root: {} with {} descendants", root.getTitle(), root.descendants().size());
            store.writeContent(root);
        }
        return directoryParsingResult;
    }

    public RagOptions ragOptions() {
        // TODO parameterize this
        return new RagOptions();
//                .withSimilarityThreshold(guideConfig.similarityThreshold())
//                .withTopK(guideConfig.topK());
    }

}

package com.embabel.decker.data

import com.embabel.agent.rag.WritableContentElementRepository
import com.embabel.agent.rag.ingestion.DirectoryParsingResult
import com.embabel.agent.rag.ingestion.HierarchicalContentReader
import com.embabel.agent.tools.file.FileTools
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Exposes data
 */
@Service
class DataManager(
    private val store: WritableContentElementRepository,
) {
    private val logger = LoggerFactory.getLogger(DataManager::class.java)

    fun provisionDatabase() {
        store.provision()
    }

    fun count(): Int = store.count()

    /**
     * Read all files under this directory
     *
     * @param dir absolute path
     */
    fun ingestDirectory(dir: String): DirectoryParsingResult {
        store.provision()

        val ft = FileTools.readOnly(dir)

        val directoryParsingResult = HierarchicalContentReader()
            .parseFromDirectory(ft)
        for (root in directoryParsingResult.contentRoots) {
            logger.info("Parsed root: {} with {} descendants", root.title, root.descendants().size)
            store.writeContent(root)
        }
        return directoryParsingResult
    }
}
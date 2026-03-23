package com.embabel.decker.data

import com.embabel.agent.rag.ingestion.DirectoryParsingResult
import com.embabel.agent.rag.ingestion.TikaHierarchicalContentReader
import com.embabel.agent.rag.lucene.LuceneSearchOperations
import com.embabel.agent.tools.file.FileTools
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Exposes data
 */
@Service
class DataManager(
    private val store: LuceneSearchOperations,
) {
    private val logger = LoggerFactory.getLogger(DataManager::class.java)

    fun provisionDatabase() {
        store.provision()
    }

    fun count(): Int = store.info().chunkCount

    /**
     * Read all files under this directory
     *
     * @param dir absolute path
     */
    fun ingestDirectory(dir: String): DirectoryParsingResult {
        store.provision()

        val ft = FileTools.readOnly(dir)

        val directoryParsingResult = TikaHierarchicalContentReader()
            .parseFromDirectory(ft)
        for (root in directoryParsingResult.contentRoots) {
            logger.info("Parsed root: {} with {} descendants", root.title, root.descendants().count())
            store.writeAndChunkDocument(root)
        }
        return directoryParsingResult
    }
}

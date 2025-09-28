package com.embabel.decker

import com.embabel.agent.tools.file.DefaultFileReadLog
import com.embabel.agent.tools.file.FileReadLog
import com.embabel.agent.tools.file.FileReadTools
import com.embabel.agent.tools.file.WellKnownFileContentTransformers
import com.embabel.common.util.StringTransformer

/**
 * Readonly access to a project on the local filesystem.
 */
class Project(override val root: String) : FileReadTools, SymbolSearch,
    FileReadLog by DefaultFileReadLog() {

    override val fileContentTransformers: List<StringTransformer> =
        listOf(WellKnownFileContentTransformers.removeApacheLicenseHeader)
}
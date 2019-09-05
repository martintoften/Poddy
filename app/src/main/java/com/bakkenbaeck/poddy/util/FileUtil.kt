package com.bakkenbaeck.poddy.util

import java.io.File

fun createNewFile(parent: File, fileName: String): File {
    val file = File(parent, fileName)

    if (!file.exists()) {
        file.createNewFile()
    } else {
        file.delete()
        file.createNewFile()
    }

    return file
}

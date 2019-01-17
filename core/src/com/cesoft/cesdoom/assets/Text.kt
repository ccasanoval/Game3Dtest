package com.cesoft.cesdoom.assets

import com.badlogic.gdx.files.FileHandle

class Text {

    var string: String? = null

    constructor() {

        this.string = String("".toByteArray())

    }

    constructor(data: ByteArray) {

        this.string = String(data)

    }

    constructor(string: String) {

        this.string = String(string.toByteArray())

    }

    constructor(file: FileHandle) {

        this.string = String(file.readBytes())

    }

    constructor(text: Text) {

        this.string = String(text.string!!.toByteArray())

    }

    fun clear() {
        this.string = String("".toByteArray())
    }

}
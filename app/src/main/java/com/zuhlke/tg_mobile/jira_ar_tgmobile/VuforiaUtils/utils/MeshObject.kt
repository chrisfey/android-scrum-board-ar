/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.zuhlke.tg_mobile.jira_ar_tgmobile.VuforiaUtils.utils

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


abstract class MeshObject {

    enum class BUFFER_TYPE {
        BUFFER_TYPE_VERTEX, BUFFER_TYPE_TEXTURE_COORD, BUFFER_TYPE_NORMALS, BUFFER_TYPE_INDICES
    }


    val vertices: Buffer
        get() = getBuffer(BUFFER_TYPE.BUFFER_TYPE_VERTEX)


    val texCoords: Buffer
        get() = getBuffer(BUFFER_TYPE.BUFFER_TYPE_TEXTURE_COORD)


    val normals: Buffer
        get() = getBuffer(BUFFER_TYPE.BUFFER_TYPE_NORMALS)


    val indices: Buffer
        get() = getBuffer(BUFFER_TYPE.BUFFER_TYPE_INDICES)


    protected fun fillBuffer(array: DoubleArray): Buffer {
        // Convert to floats because OpenGL doesn't work on doubles, and manually
        // casting each input value would take too much time.
        // Each float takes 4 bytes
        val bb = ByteBuffer.allocateDirect(4 * array.size)
        bb.order(ByteOrder.LITTLE_ENDIAN)
        for (d in array)
            bb.putFloat(d.toFloat())
        bb.rewind()

        return bb

    }


    protected fun fillBuffer(array: FloatArray): Buffer {
        // Each float takes 4 bytes
        val bb = ByteBuffer.allocateDirect(4 * array.size)
        bb.order(ByteOrder.LITTLE_ENDIAN)
        for (d in array)
            bb.putFloat(d)
        bb.rewind()

        return bb

    }


    protected fun fillBuffer(array: ShortArray): Buffer {
        // Each short takes 2 bytes
        val bb = ByteBuffer.allocateDirect(2 * array.size)
        bb.order(ByteOrder.LITTLE_ENDIAN)
        for (s in array)
            bb.putShort(s)
        bb.rewind()

        return bb

    }


    abstract fun getBuffer(bufferType: BUFFER_TYPE): Buffer


    abstract val numObjectVertex: Int


    abstract val numObjectIndex: Int

}

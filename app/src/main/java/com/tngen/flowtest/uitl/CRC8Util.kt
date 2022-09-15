package com.tngen.flowtest.uitl

import java.util.zip.Checksum

class CRC8 : Checksum {
    private var crc = 0
    override fun update(input: ByteArray, offset: Int, len: Int) {
        for (i in 0 until len) {
            update(input[offset + i])
        }
    }

    fun update(input: ByteArray) {
        update(input, 0, input.size)
    }

    private fun update(b: Byte) {
        crc = crc xor b.toInt()
        for (j in 0..7) {
            crc = if (crc and 0x80 != 0) {
                crc shl 1 xor poly
            } else {
                crc shl 1
            }
        }
        crc = crc and 0xFF
    }

    override fun update(b: Int) {
        update(b.toByte())
    }

    override fun getValue(): Long {
        return (crc and 0xFF).toLong()
    }

    override fun reset() {
        crc = 0
    }

    companion object {
        private const val poly = 0xA7

        /**
         * Test
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val crc = CRC8()
            crc.reset()
            crc.update("test".toByteArray())
            println("112=" + crc.value)
            crc.reset()
            crc.update("hello world".toByteArray())
            println("244=" + crc.value)
        }
    }
}
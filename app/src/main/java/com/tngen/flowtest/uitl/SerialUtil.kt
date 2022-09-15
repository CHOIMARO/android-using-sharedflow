package com.tngen.flowtest.uitl

import org.json.JSONObject

class SerialUtil {
    companion object {
        var dataByte = byteArrayOf()
        var serialFlag: Int = 0
        var json: JSONObject? = null


        @JvmStatic fun addDataString(byte: Byte) {
            val esc: Char = 27.toChar()

            dataByte += byte
            val pos = dataByte.indexOf("3C".toInt(16).toByte())
            if(serialFlag == 0 && pos > 0) {
                dataByte = dataByte.copyOfRange(pos, dataByte.size)
            }

            if(String.format("%02X", byte) == "3C") {
                serialFlag +=1
            }
            if(String.format("%02X", byte) == "3E") {
                serialFlag -=1
            }
//            val c1 = countOccurrences(s, '{')
//            val c2 = countOccurrences(s, '}')
//            val pos = dataString.indexOf("{")
//            if(serialFlag==0 && pos>0) {
//                dataString = dataString.substring(pos)
//            }

            if (serialFlag < 0) clearDataString()
            if (dataByte.size > 16384) clearDataString()
        }
        @JvmStatic fun clearDataString() {
            dataByte = byteArrayOf()
            serialFlag = 0
        }
        @JvmStatic fun isSerialReceived() : Boolean {
            val crc = CRC8()
            if (serialFlag == 0) {
                val ss = dataByte
                if(ss.size >= 4) {
                    if(String.format("%02X", ss[1]).equals("0B")) {
                        if(String.format("%02X", ss[3]).equals("72") || String.format("%02X", ss[3]).equals("63") || String.format("%02X", ss[3]).equals("61")) { //센서 데이터 수신 값 전달 Or 센서 캘리브레이션 설정 후 첫 메세지
                            if(ss.size >= 15) {
                                val recvData = ss.copyOfRange(0, 14)
                                crc.reset()
                                crc.update(recvData)
                                val value = crc.value

                                if(String.format("%02X", value) == String.format("%02X", ss[ss.lastIndex])) {
                                    dataByte = dataByte.copyOfRange(0,15)
                                    return true
                                }else{
                                    dataByte = byteArrayOf()
                                }
                            }
                        }
                    }else if (String.format("%02X", ss[1]).equals("02")) {
                        if (String.format("%02X", ss[3]).equals("63")) { //센서 캘리브레이션 설정 응답
                            if(ss.size >= 6) {
                                val recvData = ss.copyOfRange(0,5)
                                crc.reset()
                                crc.update(recvData)
                                val value = crc.value

                                if(String.format("%02X",value) == String.format("%02X", ss[ss.lastIndex])) {
                                    dataByte = dataByte.copyOfRange(0,6)
                                    return true
                                }else{
                                    dataByte = byteArrayOf()
                                }

                            }
                        } else if (String.format("%02X", ss[3]).equals("61")) { //알람 기준치 설정 응답
                            if(ss.size >= 6) {
                                val recvData = ss.copyOfRange(0,5)
                                crc.reset()
                                crc.update(recvData)
                                val value = crc.value

                                if(String.format("%02X", value) == String.format("%02X", ss[ss.lastIndex])) {
                                    dataByte = dataByte.copyOfRange(0,6)
                                    return true
                                }else{
                                    dataByte = byteArrayOf()
                                }
                            }
                        }
                    }

                }
            }
            return false
        }
    }
}
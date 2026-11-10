package com.github.jing332.script.simple.ext

import cn.hutool.core.codec.Base64
import cn.hutool.core.util.HexUtil
import cn.hutool.crypto.symmetric.SymmetricCrypto
import com.github.jing332.common.DateFormatConst.dateFormat
import com.github.jing332.common.utils.EncoderUtils
import com.github.jing332.common.utils.MD5Utils
import com.github.jing332.script.annotation.ScriptInterface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SimpleTimeZone

interface JsCrypto {
    @ScriptInterface
    fun md5Encode(str: String): String {
        return MD5Utils.md5Encode(str)
    }

    @ScriptInterface
    fun md5Encode16(str: String): String {
        return MD5Utils.md5Encode16(str)
    }

    //****************** Symmetric encryption/decryption ************************//

    /**
     * Use it like this in JS
     * java.createSymmetricCrypto(transformation, key, iv).decrypt(data)
     * java.createSymmetricCrypto(transformation, key, iv).decryptStr(data)

     * java.createSymmetricCrypto(transformation, key, iv).encrypt(data)
     * java.createSymmetricCrypto(transformation, key, iv).encryptBase64(data)
     * java.createSymmetricCrypto(transformation, key, iv).encryptHex(data)
     */

    /* When calling SymmetricCrypto, a random key is used if key is null */
    @ScriptInterface
    fun createSymmetricCrypto(
        transformation: String,
        key: ByteArray?,
        iv: ByteArray?
    ): SymmetricCrypto {
        val symmetricCrypto = SymmetricCrypto(transformation, key)
        return if (iv != null && iv.isNotEmpty()) symmetricCrypto.setIv(iv) else symmetricCrypto
    }

    @ScriptInterface
    fun createSymmetricCrypto(
        transformation: String,
        key: ByteArray
    ): SymmetricCrypto = createSymmetricCrypto(transformation, key, null)

    @ScriptInterface
    fun createSymmetricCrypto(
        transformation: String,
        key: String
    ): SymmetricCrypto = createSymmetricCrypto(transformation, key, null)

    @ScriptInterface
    fun createSymmetricCrypto(
        transformation: String,
        key: String,
        iv: String?
    ): SymmetricCrypto =
        createSymmetricCrypto(transformation, key.encodeToByteArray(), iv?.encodeToByteArray())

    @ScriptInterface
    fun base64DecodeToBytes(str: String): ByteArray {
        return Base64.decode(str)
    }
    @ScriptInterface
    fun base64DecodeToBytes(bytes: ByteArray): ByteArray {
        return Base64.decode(bytes)
    }

    @ScriptInterface
    fun base64Decode(str: String, flags: Int): String {
        return EncoderUtils.base64Decode(str, flags)
    }
    @ScriptInterface
    fun base64DecodeToByteArray(str: String?): ByteArray? {
        if (str.isNullOrBlank()) {
            return null
        }
        return EncoderUtils.base64DecodeToByteArray(str, 0)
    }
    @ScriptInterface
    fun base64DecodeToByteArray(str: String?, flags: Int): ByteArray? {
        if (str.isNullOrBlank()) {
            return null
        }
        return EncoderUtils.base64DecodeToByteArray(str, flags)
    }
    @ScriptInterface
    fun base64Encode(str: String): String? {
        return EncoderUtils.base64Encode(str, 2)
    }
    @ScriptInterface
    fun base64Encode(str: String, flags: Int): String? {
        return EncoderUtils.base64Encode(str, flags)
    }
    @ScriptInterface
    fun base64Encode(src: ByteArray): String? {
        return EncoderUtils.base64Encode(src)
    }
    @ScriptInterface
    fun base64Encode(src: ByteArray, flags: Int = android.util.Base64.NO_WRAP): String? {
        return EncoderUtils.base64Encode(src, flags)
    }


    /* Decode a hex string to a byte array */
    @ScriptInterface
    fun hexDecodeToByteArray(hex: String): ByteArray? {
        return HexUtil.decodeHex(hex)
    }

    /* Decode a hex string to a UTF-8 string */
    @ScriptInterface
    fun hexDecodeToString(hex: String): String? {
        return HexUtil.decodeHexStr(hex)
    }

    @ScriptInterface
    /* Encode UTF-8 to a hex string */
    fun hexEncodeToString(utf8: String): String? {
        return HexUtil.encodeHexStr(utf8)
    }

    /**
     * Format a time
     */
    @ScriptInterface
    fun timeFormatUTC(time: Long, format: String, sh: Int): String? {
        val utc = SimpleTimeZone(sh, "UTC")
        return SimpleDateFormat(format, Locale.getDefault()).run {
            timeZone = utc
            format(Date(time))
        }
    }

    /**
     * Time formatting
     */
    @ScriptInterface
    fun timeFormat(time: Long): String {
        return dateFormat.format(Date(time))
    }

    /**
     * Convert UTF-8 encoding to GBK encoding
     */
    @ScriptInterface
    fun utf8ToGbk(str: String): String {
        val utf8 = String(str.toByteArray(charset("UTF-8")))
        val unicode = String(utf8.toByteArray(), charset("UTF-8"))
        return String(unicode.toByteArray(charset("GBK")))
    }

    /*  fun base64Decode(str: String, flags: Int): String {
          return EncoderUtils.base64Decode(str, flags)
      }

      fun base64DecodeToByteArray(str: String?): ByteArray? {
          if (str.isNullOrBlank()) {
              return null
          }
          return EncoderUtils.base64DecodeToByteArray(str, 0)
      }

      fun base64DecodeToByteArray(str: String?, flags: Int): ByteArray? {
          if (str.isNullOrBlank()) {
              return null
          }
          return EncoderUtils.base64DecodeToByteArray(str, flags)
      }

      fun base64Encode(str: String): String? {
          return EncoderUtils.base64Encode(str, 2)
      }

      fun base64Encode(str: String, flags: Int): String? {
          return EncoderUtils.base64Encode(str, flags)
      }*/

}
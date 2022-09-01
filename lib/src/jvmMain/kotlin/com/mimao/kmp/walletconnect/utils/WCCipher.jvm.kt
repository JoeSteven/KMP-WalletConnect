package com.mimao.kmp.walletconnect.utils

import com.mimao.kmp.walletconnect.entity.WCEncryptedPayload
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.toByteString
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.engines.AESEngine
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PKCS7Padding
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import java.security.SecureRandom

internal actual object WCCipher {
    actual fun decrypt(payload: String, key: String): String{
        val payload = JSON.decodeFromString(WCEncryptedPayload.serializer(), payload)
        val padding = PKCS7Padding()
        val aes = PaddedBufferedBlockCipher(
            CBCBlockCipher(AESEngine()),
            padding
        )
        val ivAndKey = ParametersWithIV(
            KeyParameter(key.decodeHex().toByteArray()),
            payload.iv.decodeHex().toByteArray()
        )
        aes.init(false, ivAndKey)

        val encryptedData = payload.data.decodeHex().toByteArray()
        val minSize = aes.getOutputSize(encryptedData.size)
        val outBuf = ByteArray(minSize)
        var len = aes.processBytes(encryptedData, 0, encryptedData.size, outBuf, 0)
        len += aes.doFinal(outBuf, len)
        return outBuf.copyOf(len).decodeToString()
    }

    actual fun encrypt(payload: String, key: String): WCEncryptedPayload {
        val payloadBytes = payload.toByteArray()
        val hexKey = key.decodeHex().toByteArray()
        val iv = createRandomBytes(16)

        val padding = PKCS7Padding()
        val aes = PaddedBufferedBlockCipher(
            CBCBlockCipher(AESEngine()),
            padding
        )
        aes.init(true, ParametersWithIV(KeyParameter(hexKey), iv))

        val minSize = aes.getOutputSize(payloadBytes.size)
        val outBuf = ByteArray(minSize)
        val length1 = aes.processBytes(payloadBytes, 0, payloadBytes.size, outBuf, 0)
        aes.doFinal(outBuf, length1)


        val hmac = HMac(SHA256Digest())
        hmac.init(KeyParameter(hexKey))

        val hmacResult = ByteArray(hmac.macSize)
        hmac.update(outBuf, 0, outBuf.size)
        hmac.update(iv, 0, iv.size)
        hmac.doFinal(hmacResult, 0)
        return WCEncryptedPayload(
            data = outBuf.toByteString().hex(),
            hmac = hmacResult.toByteString().hex(),
            iv = iv.toByteString().hex()
        )
    }

    private fun createRandomBytes(i: Int) = ByteArray(i).also { SecureRandom().nextBytes(it) }

}
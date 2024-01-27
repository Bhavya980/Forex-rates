package forex.util

import forex.config.AppConfig.TokenConfig

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}
import scala.util.{Failure, Success, Try}

class TokenEncryption(tokenConfig: TokenConfig) {

  // Key and IV should be kept secure, and we may want to use a key management system in a real-world scenario.
  private val secretKey = tokenConfig.secretKey
  private val initializationVector: Array[Byte] = hexStringToByteArray(tokenConfig.initializationVector)

  def encrypt(plainText: String): String = {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES")
    val ivParameterSpec = new IvParameterSpec(initializationVector)

    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
    val encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"))

    Base64.getEncoder.encodeToString(encryptedBytes)
  }

  private def hexStringToByteArray(s: String): Array[Byte] = {
    (for (i <- 0 until s.length by 2) yield {
      Integer.parseInt(s.substring(i, i + 2), 16).toByte
    }).toArray
  }

  def decrypt(encryptedText: String): Either[String, String] = {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES")
    val ivParameterSpec = new IvParameterSpec(initializationVector)

    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
    Try {
      val decodedBytes = Base64.getDecoder.decode(encryptedText)
      val decryptedBytes = cipher.doFinal(decodedBytes)

      new String(decryptedBytes, "UTF-8")
    } match {
      case Failure(_) => Left("Invalid token")
      case Success(value) =>Right(value)
    }
  }

}

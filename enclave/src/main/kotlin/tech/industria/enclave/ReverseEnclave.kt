package tech.industria.enclave

import com.r3.conclave.enclave.Enclave
import com.r3.conclave.mail.EnclaveMail

/**
 * Simply reverses the bytes that are passed in.
 */
class ReverseEnclave : Enclave() {

    // We store the previous result to showcase that the enclave internals can be examined in a mock test.
    var previousResult: ByteArray? = null

    override fun receiveFromUntrustedHost(bytes: ByteArray): ByteArray? {
        // This is used for host->enclave calls so we don't have to think about authentication.
        val input = String(bytes)
        if (input.length < 3 || input.length > 300) {
            throw IllegalArgumentException("The input string should be between 3 and 3000 symbols!")
        }
        val result =
            reverse(input).toByteArray()
        previousResult = result
        return result
    }

    override fun receiveMail(id: Long, routingHint: String?, mail: EnclaveMail) {
        // This is used when the host delivers a message from the client.
        // First, decode mail body as a String.
        val stringToReverse = String(mail.bodyAsBytes)
        // Reverse it and re-encode to UTF-8 to send back.
        val reversedEncodedString = reverse(stringToReverse).toByteArray()
        // Check the client that sent the mail set things op so we can reply.
        val sender = mail.authenticatedSender
            ?: throw IllegalArgumentException("Mail sent to this enclave must be authenticated so we can reply.")
        // Create and send back the mail with the same topic as the sender used.
        val reply = createMail(sender, reversedEncodedString)
        reply.topic = mail.topic
        postMail(reply, routingHint)
    }

    private fun reverse(input: String): String {
        return input.reversed()
    }

}
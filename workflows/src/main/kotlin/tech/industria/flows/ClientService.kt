package tech.industria.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.conclave.client.EnclaveConstraint
import com.r3.conclave.common.EnclaveInstanceInfo
import com.r3.conclave.mail.Curve25519KeyPairGenerator
import com.r3.conclave.mail.MutableMail
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import java.nio.charset.StandardCharsets
import java.util.*

@CordaService
class ClientService(private val serviceHub: AppServiceHub) : SingletonSerializeAsToken() {

    @Suspendable
    fun isTrue(attestationBytes: ByteArray): EnclaveInstanceInfo {
        val attestation = EnclaveInstanceInfo.deserialize(attestationBytes)
        EnclaveConstraint.parse("S:4924CA3A9C8241A3C0AA1A24A407AA86401D2B79FA9FF84932DA798A942166D4 PROD:1 SEC:INSECURE")
            .check(attestation)
        return attestation
    }

    @Suspendable
    fun generateMail(word: String, attestation: EnclaveInstanceInfo): ByteArray {
        val myKey = Curve25519KeyPairGenerator().generateKeyPair()
        val mail: MutableMail = attestation.createMail(word.toByteArray(StandardCharsets.UTF_8))
        mail.privateKey = myKey.private
        mail.topic = UUID.randomUUID().toString()
        return mail.encrypt()
    }
}
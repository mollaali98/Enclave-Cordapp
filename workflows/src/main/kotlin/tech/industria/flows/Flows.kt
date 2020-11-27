package tech.industria.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.conclave.client.EnclaveConstraint
import com.r3.conclave.common.EnclaveInstanceInfo
import com.r3.conclave.mail.Curve25519KeyPairGenerator
import com.r3.conclave.mail.MutableMail
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import java.nio.charset.StandardCharsets
import java.util.*

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator(
    val word: String,
    val otherParty: Party
) : FlowLogic<String>() {
    override val progressTracker = ProgressTracker()

    @Suspendable
    override fun call(): String {
//        val attestationBytes = session.receive<ByteArray>().unwrap { it }
        val attestationBytes = subFlow(GetAttestation(otherParty))
        val attestation = EnclaveInstanceInfo.deserialize(attestationBytes)
        EnclaveConstraint.parse("S:4924CA3A9C8241A3C0AA1A24A407AA86401D2B79FA9FF84932DA798A942166D4 PROD:1 SEC:INSECURE")
            .check(attestation)
        val myKey = Curve25519KeyPairGenerator().generateKeyPair()
        val mail: MutableMail = attestation.createMail(word.toByteArray(StandardCharsets.UTF_8))
        mail.privateKey = myKey.private
        mail.topic = UUID.randomUUID().toString()
        val encryptedMail = mail.encrypt()
//        session.send(encryptedMail)
//        val receivedMail = session.receive<ByteArray>().unwrap { it }
        val receivedMail = subFlow(SendMail(otherParty, encryptedMail))
        val reply = attestation.decryptMail(receivedMail, myKey.private)
        return String(reply.bodyAsBytes)
    }
}

//@InitiatedBy(Initiator::class)
//class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
//    @Suspendable
//    override fun call() {
//        // Responder flow logic goes here.
//        val hostService = serviceHub.cordaService(HostService::class.java)
//        counterpartySession.send(hostService.getAttestationBytes())
//        val attestationBytes = counterpartySession.receive<ByteArray>().unwrap { it }
//        val result = hostService.deliverMail(attestationBytes)
////        counterpartySession.send(result)
//    }
//}

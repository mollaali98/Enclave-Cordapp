package tech.industria.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.conclave.client.EnclaveConstraint
import com.r3.conclave.common.EnclaveInstanceInfo
import com.r3.conclave.mail.Curve25519KeyPairGenerator
import com.r3.conclave.mail.MutableMail
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
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
        val session = initiateFlow(otherParty)
        val attestation = subFlow(GetAttestation(session))
//        val clientService =  serviceHub.cordaService(ClientService::class.java)
//        val attestation = clientService.isTrue(attestationBytes).also { println("attestation: $it") }

//        val attestation = EnclaveInstanceInfo.deserialize(attestationBytes).also {
//            print("attestation: ")
//            println(it)
//        }
//        EnclaveConstraint.parse("S:4924CA3A9C8241A3C0AA1A24A407AA86401D2B79FA9FF84932DA798A942166D4 PROD:1 SEC:INSECURE")
//            .check(attestation)
//        val myKey = Curve25519KeyPairGenerator().generateKeyPair()
//        val mail: MutableMail = attestation.createMail(word.toByteArray(StandardCharsets.UTF_8))
//        mail.privateKey = myKey.private
//        mail.topic = UUID.randomUUID().toString()
//        val encryptedMail = clientService.generateMail("Batman", attestation)
//        session.send(encryptedMail)
//        val receivedMail = session.receive<ByteArray>().unwrap { it }
//        val receivedMail = subFlow(SendMail(session, encryptedMail))
        session.receive<ByteArray>().unwrap{it}.also { println(it) }
        println("Sending mail payload")
        session.sendAndReceive<String>("Here").unwrap { it }.also { println(it) }
        println("-------------------------------------------------------------------------------------------")
//        val reply = attestation.decryptMail(receivedMail, myKey.private)
//        return String(reply.bodyAsBytes)
        return ""
    }
}

@InitiatedBy(Initiator::class)
class Responder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        // Responder flow logic goes here.
        subFlow(ReturnAttestationResponder(counterpartySession))
        counterpartySession.send("My attestation".toByteArray())
        counterpartySession.receive<String>().unwrap { it }.also {
            print("mail: ")
            println(it)
        }
        counterpartySession.send("There")
//        subFlow(ReceiveMail(counterpartySession))
    }
}

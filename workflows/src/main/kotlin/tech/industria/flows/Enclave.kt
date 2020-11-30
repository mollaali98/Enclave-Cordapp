package tech.industria.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.conclave.client.EnclaveConstraint
import com.r3.conclave.common.EnclaveInstanceInfo
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.utilities.unwrap
import java.util.*


class GetAttestation(
    private val session: FlowSession
) : FlowLogic<EnclaveInstanceInfo>() {
    @Suspendable
    override fun call(): EnclaveInstanceInfo {
        return session.receive<String>().unwrap {
            val attestation = EnclaveInstanceInfo.deserialize(Base64.getDecoder().decode(it)).also {
                print("attestation: ")
                println(it)
            }
            EnclaveConstraint.parse("S:4924CA3A9C8241A3C0AA1A24A407AA86401D2B79FA9FF84932DA798A942166D4 PROD:1 SEC:INSECURE")
                .check(attestation)
            attestation
        }
    }

}

class ReturnAttestationResponder(private val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val hostService = serviceHub.cordaService(HostService::class.java)
        counterpartySession.send(Base64.getEncoder().encodeToString(hostService.getAttestationBytes()))
    }
}


class SendMail(
    private val session: FlowSession,
    private val mail: ByteArray
) : FlowLogic<ByteArray>() {
    @Suspendable
    override fun call(): ByteArray {
        return session.sendAndReceive<ByteArray>(mail).unwrap { it }
//        return "".toByteArray()
    }
}

class ReceiveMail(private val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val hostService = serviceHub.cordaService(HostService::class.java)
        val mail = counterpartySession.receive<ByteArray>().unwrap { it }
        val result = hostService.deliverMail(mail)
        counterpartySession.send(result)
    }
}

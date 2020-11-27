package tech.industria.flows

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.utilities.unwrap


@InitiatingFlow
class GetAttestation(
    val otherParty: Party
) : FlowLogic<ByteArray>() {
    @Suspendable
    override fun call(): ByteArray {
        val session = initiateFlow(otherParty)
        return session.receive<ByteArray>().unwrap { it }
    }

}

@InitiatedBy(GetAttestation::class)
class GetAttestationResponder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val hostService = serviceHub.cordaService(HostService::class.java)
        counterpartySession.send(hostService.getAttestationBytes())
    }
}

@InitiatingFlow
@StartableByRPC
class SendMail(
    val otherParty: Party,
    val mail: ByteArray
) : FlowLogic<ByteArray>() {
    @Suspendable
    override fun call(): ByteArray {
        val session = initiateFlow(otherParty)
        return session.sendAndReceive<ByteArray>(mail).unwrap { it }
    }
}


@InitiatedBy(SendMail::class)
class SendMailResponder(val counterpartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val hostService = serviceHub.cordaService(HostService::class.java)
        val mail = counterpartySession.receive<ByteArray>().unwrap { it }
        val result = hostService.deliverMail(mail)
        counterpartySession.send(result)
    }
}

package tech.industria.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.conclave.common.EnclaveInstanceInfo
import com.r3.conclave.host.AttestationParameters
import com.r3.conclave.host.EnclaveHost
import com.r3.conclave.host.EnclaveLoadException
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import java.util.*


@CordaService
class HostService(private val serviceHub: AppServiceHub) : SingletonSerializeAsToken() {

    private val className = "tech.industria.enclave.ReverseEnclave"
    private lateinit var enclave: EnclaveHost
    private var id: Long = 1
    val mailPool = mutableMapOf<String?, ByteArray>()

    init {
        try {
            EnclaveHost.checkPlatformSupportsEnclaves(true)
            println("This platform supports enclaves in simulation, debug and release mode.")
        } catch (e: EnclaveLoadException) {
            println("This platform does not support hardware enclaves: " + e.message)
        }
        try {
            println("init config")
            enclave = EnclaveHost.load(className)
            enclave.start(AttestationParameters.DCAP(), object : EnclaveHost.MailCallbacks {
                override fun postMail(encryptedBytes: ByteArray, routingHint: String?) {
                    mailPool[routingHint] = encryptedBytes
                }
            })
        } catch (e: Exception) {
            error(e)
        }
    }

    @Suspendable
    fun test(): String {
        return "Test"
    }

    @Suspendable
    private fun getAttestation(): EnclaveInstanceInfo {
        return enclave.enclaveInstanceInfo
    }

    @Suspendable
    fun getAttestationBytes(): ByteArray {
        return getAttestation().serialize()
    }

    @Suspendable
    fun deliverMail(mailBytes: ByteArray): ByteArray {
        println(id)
        val routingHint = UUID.randomUUID().toString()
        enclave.deliverMail(id++, mailBytes, routingHint)
        val result = mailPool[routingHint] ?: error("Mail pool is not contain $routingHint")
        mailPool.remove(routingHint)
        return result
    }
}
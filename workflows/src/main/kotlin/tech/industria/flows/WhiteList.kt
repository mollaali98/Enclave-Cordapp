package tech.industria.flows

import com.r3.conclave.common.EnclaveInstanceInfo
import net.corda.core.serialization.SerializationWhitelist

class WhiteList : SerializationWhitelist {
    override val whitelist: List<Class<*>>
        get() = listOf(
            EnclaveInstanceInfo::class.java
        )
}
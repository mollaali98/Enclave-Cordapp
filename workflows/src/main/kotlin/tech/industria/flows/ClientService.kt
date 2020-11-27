package tech.industria.flows

import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken

@CordaService
class ClientService(private val serviceHub: AppServiceHub) : SingletonSerializeAsToken() {

}
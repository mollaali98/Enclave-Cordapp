package tech.industria.states

import tech.industria.contracts.IndustriaContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty

// *********
// * State *
// *********
@BelongsToContract(IndustriaContract::class)
data class TemplateState(val data: String, override val participants: List<AbstractParty> = listOf()) : ContractState

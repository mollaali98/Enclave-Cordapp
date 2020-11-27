package tech.industria

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.corda.core.utilities.getOrThrow
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestCordapp
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import tech.industria.flows.GetAttestationResponder
import tech.industria.flows.Initiator
import tech.industria.flows.SendMailResponder


class FlowTests {
    private val network = MockNetwork(
        MockNetworkParameters(
            cordappsForAllNodes = listOf(
                TestCordapp.findCordapp("tech.industria.contracts"),
                TestCordapp.findCordapp("tech.industria.flows")
            )
        )
    )
    lateinit var nodeB: StartedMockNode
    lateinit var nodeA: StartedMockNode
    private val mapper =
        ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerKotlinModule()


    @Before
    fun setup() {
        network.runNetwork()
        nodeB = network.createNode()
        nodeA = network.createNode()
        nodeA.registerInitiatedFlow(SendMailResponder::class.java)
        network.runNetwork()
        nodeA.registerInitiatedFlow(GetAttestationResponder::class.java)
        network.runNetwork()
        nodeB.registerInitiatedFlow(GetAttestationResponder::class.java)
        network.runNetwork()
        nodeB.registerInitiatedFlow(SendMailResponder::class.java)
        network.runNetwork()
    }

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `dummy test`() {
        val result = nodeA.startFlow(Initiator(word = "Batman", otherParty = nodeB.info.legalIdentities.first()))
        network.runNetwork()
        assertEquals(result.getOrThrow(), "namtaB")
    }
}
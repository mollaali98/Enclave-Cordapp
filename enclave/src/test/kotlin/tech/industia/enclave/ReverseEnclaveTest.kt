package tech.industia.enclave

import com.r3.conclave.host.EnclaveLoadException
import com.r3.conclave.testing.MockHost
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import tech.industria.enclave.ReverseEnclave

class ReverseEnclaveTest {

    @Test
    @Throws(EnclaveLoadException::class)
    fun `reverse numbers should successfully`() {
        val mockHost = MockHost.loadMock(ReverseEnclave::class.java)
        mockHost.start(null, null)
        val reverseEnclave = mockHost.enclave
        Assertions.assertNull(reverseEnclave.previousResult)
        val response = mockHost.callEnclave("1234".toByteArray())
        Assertions.assertNotNull(response)
        Assertions.assertEquals("4321", String(response!!))
        Assertions.assertEquals("4321", String(reverseEnclave.previousResult!!))
    }

    @Test
    fun `reverse whole sentence should successfully`() {
        val mockHost = MockHost.loadMock(ReverseEnclave::class.java)
        mockHost.start(null, null)
        val reverseEnclave = mockHost.enclave
        Assertions.assertNull(reverseEnclave.previousResult)
        val response = mockHost.callEnclave("What the Heck Is Cryptoart?".toByteArray())
        Assertions.assertNotNull(response)
        Assertions.assertEquals("?traotpyrC sI kceH eht tahW", String(response!!))
        Assertions.assertEquals("?traotpyrC sI kceH eht tahW", String(reverseEnclave.previousResult!!))
    }

    @Test
    fun `reverse too short input should fail`() {
        val mockHost = MockHost.loadMock(ReverseEnclave::class.java)
        mockHost.start(null, null)
        val reverseEnclave = mockHost.enclave
        Assertions.assertNull(reverseEnclave.previousResult)
        Assertions.assertThrows(IllegalArgumentException::class.java) { mockHost.callEnclave("12".toByteArray()) }
    }

    @Test
    fun `reverse too long input should fail`() {
        val mockHost = MockHost.loadMock(ReverseEnclave::class.java)
        mockHost.start(null, null)
        val reverseEnclave = mockHost.enclave
        Assertions.assertNull(reverseEnclave.previousResult)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            mockHost.callEnclave(
                """Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc, quis gravida magna mi a libero. Fusce vulputate eleifend sapien. Vestibulum purus quam, scelerisque ut, mollis sed, nonummy id, metus. Nullam accumsan lorem in dui. Cras ultricies mi eu turpis hendrerit fringilla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; In ac dui quis mi consectetuer lacinia. Nam pretium turpis et arcu. Duis arcu tortor, suscipit eget, imperdiet nec, imperdiet iaculis, ipsum. Sed aliquam ultrices mauris. Integer ante arcu, accumsan a, consectetuer eget, posuere ut, mauris. Praesent adipiscing. Phasellus ullamcorper ipsum rutrum nunc. Nunc nonummy metus. Vestibulum volutpat pretium libero. Cras id dui. Aenean ut""".toByteArray()
            )
        }
    }
}
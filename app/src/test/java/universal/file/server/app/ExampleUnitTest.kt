package universal.file.server.app

import universal.file.server.app.utils.NetworkHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun `virtual and tunnel interfaces are correctly identified`() {
    assertTrue(NetworkHelper.isExcludedInterface("tun0"))
    assertTrue(NetworkHelper.isExcludedInterface("tun1"))
    assertTrue(NetworkHelper.isExcludedInterface("tap0"))
    assertTrue(NetworkHelper.isExcludedInterface("wg0"))
    assertTrue(NetworkHelper.isExcludedInterface("dummy0"))
    assertTrue(NetworkHelper.isExcludedInterface("p2p0"))
    assertTrue(NetworkHelper.isExcludedInterface("virbr0"))
    assertTrue(NetworkHelper.isExcludedInterface("vboxnet0"))
  }

  @Test
  fun `supported network interfaces are not excluded`() {
    assertFalse(NetworkHelper.isExcludedInterface("wlan0"))
    assertFalse(NetworkHelper.isExcludedInterface("ap0"))
    assertFalse(NetworkHelper.isExcludedInterface("rndis0"))
    assertFalse(NetworkHelper.isExcludedInterface("swlan0"))
  }
}

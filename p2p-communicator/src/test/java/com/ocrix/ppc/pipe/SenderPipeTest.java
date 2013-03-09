/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ocrix.ppc.pipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.protocol.PipeAdvertisement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
//import org.junit.Rule;
import org.junit.Test;
//import org.junit.rules.TemporaryFolder;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.pipe.PipeFactory;
import com.ocrix.ppc.service.impl.PPCServicePipeFactoryImpl;

public class SenderPipeTest {
	// @Rule
	// public static TemporaryFolder tempStore = new TemporaryFolder();

	private static NetworkManager testManager;
	private static PeerGroup peerGroup;
	private static PipeAdvertisement pipeAdvertisement;
	protected static InputPipe inputPipe;
	private static OutputPipe outputPipe;
	private static File file = null;

	@BeforeClass
	public static void setUp() throws Exception {
		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
				java.util.logging.Level.OFF.toString());

		file = new File(VerificationConstants.TARGET + "/"
				+ SenderPipeTest.class.getName());

		/* Creates a NetworkManager */
		testManager = new NetworkManager(ConfigMode.ADHOC,
				SenderPipeTest.class.getSimpleName(), file.toURI());
		testManager.registerShutdownHook();
		peerGroup = testManager.startNetwork();

		PPCServicePipeFactoryImpl pfs = new PPCServicePipeFactoryImpl();
		pipeAdvertisement = pfs.createUnicastPipe(peerGroup,
				SenderPipeTest.class.getSimpleName()).getPipeAdv();

		PipeFactory pf = new PipeFactory();

		inputPipe = peerGroup.getPipeService().createInputPipe(
				pipeAdvertisement);

		outputPipe = pf.createOutputPipe(peerGroup, pipeAdvertisement);
	}

	@Test
	public void shallCreateOutputPipe() {
		assertNotNull(outputPipe);
		assertEquals(VerificationConstants.VP_PIPE_UNICAST_OUTPUT_PIPE,
				outputPipe.getType());
		assertEquals(SenderPipeTest.class.getSimpleName(), outputPipe.getName());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		testManager.stopNetwork();
		if (file != null && file.exists()) {
			file.delete();
		}
		// tempStore.delete();
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ SenderPipeTest.class.getSimpleName()));
	}

}

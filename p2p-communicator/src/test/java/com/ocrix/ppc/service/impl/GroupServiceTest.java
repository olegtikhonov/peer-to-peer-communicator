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

package com.ocrix.ppc.service.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.service.impl.GroupService;

public class GroupServiceTest {
//	@Rule
//	public static TemporaryFolder tempStore = new TemporaryFolder();
	private static GroupService groupService;
	private static NetworkManager manager;
	private static File file = null;
	
	@BeforeClass
	public static void setUp() throws IOException, PeerGroupException {
		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY, java.util.logging.Level.OFF.toString());
		
		file = new File(VerificationConstants.TARGET + "/" + GroupServiceTest.class.getSimpleName());
		
		manager = new NetworkManager(ConfigMode.ADHOC,
                                     GroupServiceTest.class.getSimpleName(), 
                                     file.toURI());
		manager.startNetwork();
		groupService = new GroupService(); 
	}

	@Test
	public void shallCreate() throws Exception{
		PeerGroup peerGroup = groupService.create(VerificationConstants.CUST_GROUP_NAME, manager.getNetPeerGroup());
		assertNotNull(peerGroup);
		assertEquals(VerificationConstants.CUST_GROUP_NAME, peerGroup.getPeerGroupName());
		assertEquals(VerificationConstants.MIA, peerGroup.getAllPurposePeerGroupImplAdvertisement().getAdvType());
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		manager.stopNetwork();
		
		if(file != null && file.exists()){
			file.delete();
		}
		
//		tempStore.delete();
	}
}

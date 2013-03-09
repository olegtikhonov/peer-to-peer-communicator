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

package com.ocrix.ppc.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.type.NetTopologyType;

public class TestNetTopologyType {

	protected void setUp() throws Exception {

	}

	@Test
	public void values() {
		for (NetTopologyType ntt : NetTopologyType.values()) {
			assertTrue(ntt != null);
		}
	}

	@Test
	public void size() {
		assertEquals(VerificationConstants.NET_TOPOLOGY_TYPE_SIZE,
				NetTopologyType.values().length);
	}

	protected void tearDown() throws Exception {
	}
}

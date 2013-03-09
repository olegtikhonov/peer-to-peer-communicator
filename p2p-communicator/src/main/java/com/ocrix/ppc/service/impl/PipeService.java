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

import java.io.IOException;

import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;

import com.ocrix.ppc.pipe.PipeFactory;
import com.ocrix.ppc.service.PPCService;
import com.ocrix.ppc.type.PipeType;

/**
 * Is responsible for presenting pipe service
 */
public class PipeService implements PPCService<PipeAdvertisement> {
	/* class members */
	private PeerGroup peerGroup = null;

	/**
	 * Creates a pipe service
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 * @param pipeType
	 *            {@link PipeType}
	 */
	public PipeService(PeerGroup peerGroup, PipeType pipeType) {
		setPeerGroup(peerGroup);
	}

	// @Override
	public PipeAdvertisement create(String instanceName, PipeAdvertisement env)
			throws IOException {
		PipeFactory pipeFactory = new PipeFactory();
		pipeFactory.createPropagatePipe(peerGroup, instanceName);
		return null;
	}

	/**
	 * Sets up a {@link PeerGroup}
	 * 
	 * @param peerGroup
	 *            a peer group where a peer is bound
	 */
	private void setPeerGroup(PeerGroup peerGroup) {
		this.peerGroup = peerGroup;
	}

	/**
	 * Attains a {@link PeerGroup}
	 * 
	 * @return a peer group.
	 */
	protected PeerGroup getPeerGroup() {
		return peerGroup;
	}
}

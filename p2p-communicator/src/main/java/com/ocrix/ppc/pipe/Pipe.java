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

import java.io.IOException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.PipeAdvertisement;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.type.PipeType;

/**
 * Defines an abstract pipe class
 */
public abstract class Pipe {
	/* Class members */
	private PeerGroup peerGroup = null;
	private String pipeName = null;

	/**
	 * Creates a pipe of provided type and publish it
	 * 
	 * @param pipeType
	 *            @see {@link PipeType}
	 * 
	 * @return {@link PipeAdvertisement}
	 * 
	 * @throws IOException
	 */
	protected abstract PipeAdvertisement createPipe() throws IOException;

	/**
	 * Returns a type of created pipe
	 * 
	 * @return {@link PipeType}
	 */
	public abstract PipeType getPipeType();

	/**
	 * Serves as super constructor
	 * 
	 * @param peerGroup
	 *            a peer group
	 * @param pipeName
	 *            a name to be given for pipe
	 * 
	 * @throws IOException
	 */
	public Pipe(PeerGroup peerGroup, String pipeName) throws IOException {
		// Logger off - JXTA logger off
		System.setProperty("net.jxta.logging.Logging", "OFF");
		System.setProperty("net.jxta.level", "OFF");

		setPeerGroup(peerGroup);
		setPipeName(pipeName);
	}

	/**
	 * Sets a peer group
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 */
	private void setPeerGroup(PeerGroup peerGroup) {
		Validator.validateObjNotNull(peerGroup);
		this.peerGroup = peerGroup;
	}

	/**
	 * Returns a peer group associated with that pipe
	 * 
	 * @return {@link PeerGroup}
	 */
	public PeerGroup getPeerGroup() {
		return peerGroup;
	}

	/**
	 * Sets a pipe's name
	 * 
	 * @param pipeName
	 *            a name to be given for pipe
	 */
	private void setPipeName(String pipeName) {
		Validator.validateString(pipeName);
		this.pipeName = pipeName;
	}

	/**
	 * Returns a pipe's name
	 * 
	 * @return pape's name
	 */
	public String getPipeName() {
		return pipeName;
	}
}

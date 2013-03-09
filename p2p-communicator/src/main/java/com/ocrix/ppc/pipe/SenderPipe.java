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
import net.jxta.pipe.OutputPipe;
import net.jxta.protocol.PipeAdvertisement;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.type.DefaultParameter;

/**
 * Creates Output pipe for sender using There is a constraint - it depends on
 * input pipe. Output pipe will wait until input pipe will be resolved.
 */
public class SenderPipe {

	/**
	 * Creates an output pipe with 0 timeout, that means will waits infinitely
	 * for the resolving.
	 * 
	 * @param pipeAdvertisement
	 *            - the advertisement of the pipe being resolved.
	 * @param peerGroup
	 *            - a peer group where pipe will be resolved
	 * @return OutputPipe, which defines the interface for sending messages
	 * 
	 * @throws IOException
	 *             - If the pipe cannot be created or failed to resolve within
	 *             the specified time.
	 */
	public OutputPipe createSenderPipe(PeerGroup peerGroup,
			PipeAdvertisement pipeAdvertisement) throws IOException {
		validateParameters(peerGroup, pipeAdvertisement);
		return peerGroup.getPipeService().createOutputPipe(pipeAdvertisement,
				DefaultParameter.WAIT_TIME_PIPE_CREATION.getCode());
	}

	/**
	 * Validates Server pipe parameters
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 * @param pipeAdvertisement
	 *            {@link PipeAdvertisement}
	 */
	private void validateParameters(PeerGroup peerGroup,
			PipeAdvertisement pipeAdvertisement) {
		Validator.validatePeerGroup(peerGroup);
		Validator.validateObjNotNull(pipeAdvertisement);
	}
}

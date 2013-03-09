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
import java.net.URI;

import com.ocrix.ppc.type.PipeType;

import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

/**
 * Defines <b>asynchronous unicast pipe</b> i.e unidirectional pipe.
 * <b>Unidirectional</b> means that the data is moving only from the output end
 * to the input end, in other words, from the sender to the receiver end points.
 * <b>Asynchronous</b> means that the sending endpoint can send data at any time
 * and that receiving point can read it at any time. <b>Unicast</b> means that
 * the communication is limited only from point-to-point. Uses TLS 1.0
 * (Transport Layer Security)
 * 
 * 
 */
public class UnicastSecurePipe extends Pipe {
	/* Class members */
	private PipeAdvertisement pipeAdv = null;

	/**
	 * Constructs UnicastSecurePipe
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 * @param pipeName
	 *            a pipe name to be given
	 * 
	 * @throws IOException
	 */
	public UnicastSecurePipe(PeerGroup peerGroup, String pipeName)
			throws IOException {
		super(peerGroup, pipeName);
		setPipeAdv(createPipe());
	}

	/**
	 * Returns a type of the created pipe
	 * 
	 * @return {@link PipeType}
	 */
	public PipeType getPipeType() {
		return PipeType.UNICAST_SECURE;
	}

	/**
	 * Returns a UnicastSecurePipe advertisement
	 * 
	 * @return {@link PipeAdvertisement}
	 */
	public PipeAdvertisement getPipeAdv() {
		return pipeAdv;
	}

	@Override
	protected PipeAdvertisement createPipe() throws IOException {

		/*
		 * If you are going to use this pipe, please see @UnicastPipe pipeID
		 * creation
		 */
		PipeID pipeID = PipeID.create(URI.create(IDFactory.newPipeID(
				getPeerGroup().getPeerGroupID()).toString()));
		PipeAdvertisement pipeAdv = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		pipeAdv.setName(getPipeName());
		pipeAdv.setPipeID(pipeID);
		pipeAdv.setType(PipeService.UnicastSecureType);
		getPeerGroup().getDiscoveryService().publish(pipeAdv);
		return pipeAdv;
	}

	/**
	 * Sets a pipe advertisement
	 * 
	 * @param pipeAdv
	 */
	private void setPipeAdv(PipeAdvertisement pipeAdv) {
		this.pipeAdv = pipeAdv;
	}
}

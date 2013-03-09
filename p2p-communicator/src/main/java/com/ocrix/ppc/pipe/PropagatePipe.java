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
 * Defines <b>asynchronous propagate pipe</b> i.e one-to-many pipe.
 * <b>One-to-many</b> means that this pipe provides one-to-many peer
 * communication.
 */
public class PropagatePipe extends Pipe {
	/* Class members */
	private PipeAdvertisement pipeAdv = null;

	/**
	 * Creates a new instance of the PropagatePipe
	 * 
	 * @param peerGroup
	 *            a peer group where a pipe will be associated with
	 * @param pipeName
	 *            a pipe name to be given
	 * 
	 * @throws IOException
	 */
	public PropagatePipe(PeerGroup peerGroup, String pipeName)
			throws IOException {
		super(peerGroup, pipeName);
		setPipeAdv(createPipe());
	}

	@Override
	protected PipeAdvertisement createPipe() throws IOException {
		PipeID pipeID = PipeID.create(URI.create(IDFactory.newPipeID(
				getPeerGroup().getPeerGroupID()).toString()));
		PipeAdvertisement pipeAdv = (PipeAdvertisement) AdvertisementFactory
				.newAdvertisement(PipeAdvertisement.getAdvertisementType());
		pipeAdv.setName(getPipeName());
		pipeAdv.setPipeID(pipeID);
		pipeAdv.setType(PipeService.PropagateType);
		getPeerGroup().getDiscoveryService().publish(pipeAdv);
		return pipeAdv;
	}

	@Override
	public PipeType getPipeType() {
		return PipeType.PROPAGATE;
	}

	/**
	 * Sets a pipe advertisement
	 * 
	 * @param pipeAdv
	 *            {@link PipeAdvertisement}
	 */
	private void setPipeAdv(PipeAdvertisement pipeAdv) {
		this.pipeAdv = pipeAdv;
	}

	/**
	 * Returns a pipe advertisement
	 * 
	 * @return {@link PipeAdvertisement}
	 */
	public PipeAdvertisement getPipeAdv() {
		return pipeAdv;
	}
}

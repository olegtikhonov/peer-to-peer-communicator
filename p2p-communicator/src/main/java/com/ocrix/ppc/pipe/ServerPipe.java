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
import net.jxta.util.JxtaServerPipe;
import net.jxta.util.ServerPipeAcceptListener;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.type.PipeType;

/**
 * Intended for server's pipe creation
 */
public class ServerPipe extends Pipe {
	/* Class member */
	private ServerPipeAcceptListener listener = null;
	private JxtaServerPipe serverPipe = null;
	private PipeAdvertisement serverPipeAdv = null;

	// ------------ END CLASS MEMBERS ------------

	/**
	 * Creates a ServerPipe
	 * 
	 * @param peerGroup
	 * @param listener
	 * @param pipeName
	 * @throws IOException
	 */
	public ServerPipe(PeerGroup peerGroup, ServerPipeAcceptListener listener,
			String pipeName) throws IOException {
		super(peerGroup, pipeName);
		setListener(listener);
		setServerPipeAdv(createPipe());
	}

	@Override
	public PipeType getPipeType() {
		return PipeType.SERVER_PIPE;
	}

	/**
	 * Attains a {@link PipeAdvertisement}
	 * 
	 * @return
	 */
	public PipeAdvertisement getServerPipeAdv() {
		return serverPipeAdv;
	}

	/**
	 * Returns a {@link JxtaServerPipe}
	 * 
	 * @return JxtaServerPipe
	 */
	public JxtaServerPipe getServerPipe() {
		return serverPipe;
	}

	@Override
	protected PipeAdvertisement createPipe() throws IOException {
		PipeFactory factory = new PipeFactory();
		PipeAdvertisement pipeAd = factory.createUnicastPipe(getPeerGroup(),
				getPipeName()).getPipeAdv();

		if (getListener() == null) {
			setServerPipe(new JxtaServerPipe(getPeerGroup(), pipeAd));
		} else {
			setServerPipe(new JxtaServerPipe(getPeerGroup(), pipeAd,
					getListener()));
		}

		/* Publishes it */
		getPeerGroup().getDiscoveryService().publish(
				getServerPipe().getPipeAdv());

		return getServerPipe().getPipeAdv();
	}

	/**
	 * Gets a ServerPipeAcceptListener. A server pipe listener which stores a
	 * queue of incoming connections, to be synchronously pulled off the queue
	 * at a pace determined by the client. If more connections are received than
	 * the specified backlog, new connections beyond that point will be
	 * discarded until connections are accepted from the head of the queue. The
	 * backlog default of <b>50</b>, timeout defaults to <b>60 seconds</b>, i.e.
	 * blocking.
	 * 
	 * @return {@link ServerPipeAcceptListener}
	 */
	private ServerPipeAcceptListener getListener() {
		return listener;
	}

	/**
	 * Sets up a {@link ServerPipeAcceptListener}
	 * 
	 * @param listener
	 */
	private void setListener(ServerPipeAcceptListener listener) {
		Validator.validateObjNotNull(listener);
		this.listener = listener;
	}

	/**
	 * Sets up a {@link JxtaServerPipe}
	 * 
	 * @param serverPipe
	 */
	private void setServerPipe(JxtaServerPipe serverPipe) {
		this.serverPipe = serverPipe;
	}

	/**
	 * Sets up {@link PipeAdvertisement}
	 * 
	 * @param serverPipeAdv
	 */
	private void setServerPipeAdv(PipeAdvertisement serverPipeAdv) {
		this.serverPipeAdv = serverPipeAdv;
	}
}

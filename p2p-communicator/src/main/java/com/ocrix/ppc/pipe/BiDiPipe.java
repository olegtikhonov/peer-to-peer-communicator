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
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.type.PipeType;

/**
 * Intended for bi-directional pipe creation
 */
public class BiDiPipe extends Pipe {
	/* Class members */
	private JxtaBiDiPipe bidiPipe = null;
	private PipeAdvertisement serverPipeAdv = null;
	private PipeAdvertisement bidiPipeAdv = null;
	private PipeMsgListener listener = null;
	private int timeout = 60000;

	/**
	 * Creates a BiDiPipe and publish it
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 * @param serverPipeAdv
	 *            {@link PipeAdvertisement}
	 * @param pipeName
	 *            a name to be given for the pipe
	 * @param listener
	 *            {@link PipeMsgListener}
	 * @param timeout
	 *            The number of milliseconds within which the JxtaBiDiPipe must
	 *            be successfully created. An exception will be thrown if the
	 *            pipe cannot be created in the allotted time. A timeout value
	 *            of <b>0</b> (zero) specifies an infinite timeout. Default is 1
	 *            minute.
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public BiDiPipe(PeerGroup peerGroup, PipeAdvertisement serverPipeAdv,
			String pipeName, PipeMsgListener listener, int timeout)
			throws IOException {
		super(peerGroup, pipeName);
		setServerPipeAdv(serverPipeAdv);
		setListener(listener);
		setTimeout(timeout);
		setBidiPipeAdv(createPipe());
	}

	/**
	 * Creates a BiDiPipe and publish it
	 * 
	 * @param peerGroup
	 *            a peer group that pipe will be associated wit
	 * @param serverPipeAdv
	 *            on that pipe it will be connected
	 * @param pipeName
	 *            a pipe name to be given
	 * @param listener
	 *            for responses
	 * 
	 * @throws IOException
	 *             if an IO error occurs
	 */
	public BiDiPipe(PeerGroup peerGroup, PipeAdvertisement serverPipeAdv,
			String pipeName, PipeMsgListener listener) throws IOException {
		super(peerGroup, pipeName);
		setServerPipeAdv(serverPipeAdv);
		setListener(listener);
		setBidiPipeAdv(createPipe());
	}

	@Override
	protected PipeAdvertisement createPipe() throws IOException {

		if (getPeerGroup() != null && getServerPipeAdv() != null
				&& getListener() != null) {
			setBidiPipe(new JxtaBiDiPipe(getPeerGroup(), getServerPipeAdv(),
					getTimeout(), getListener()));
			getBidiPipe().setReliable(true);
		} else
			System.out.println("OBJECTS ARE NULL");

		return getBidiPipe().getPipeAdvertisement();
	}

	@Override
	public PipeType getPipeType() {
		return PipeType.BIDI_PIPE;
	}

	/**
	 * Returns a created bidi pipe or null
	 * 
	 * @return {@link JxtaBiDiPipe}
	 */
	public JxtaBiDiPipe getBidiPipe() {
		return bidiPipe;
	}

	/**
	 * Returns a bidi pipe advertisement
	 * 
	 * @return {@link PipeAdvertisement}
	 */
	public PipeAdvertisement getBidiPipeAdv() {
		return bidiPipeAdv;
	}

	// =====================
	// PRIVATE METHODS
	// =====================

	/**
	 * Returns a server pipe advertisement
	 */
	private PipeAdvertisement getServerPipeAdv() {
		return serverPipeAdv;
	}

	/**
	 * Sets up a server pipe advertisement
	 * 
	 * @param serverPipeAdv
	 *            {@link PipeAdvertisement}
	 */
	private void setServerPipeAdv(PipeAdvertisement serverPipeAdv) {
		Validator.validateObjNotNull(serverPipeAdv);
		this.serverPipeAdv = serverPipeAdv;
	}

	/**
	 * Attains a {@link PipeMsgListener}
	 * 
	 * @return a pipe message listener
	 */
	private PipeMsgListener getListener() {
		return listener;
	}

	/**
	 * Sets up a {@link PipeMsgListener}
	 * 
	 * @param listener
	 *            a pipe a message listener
	 */
	private void setListener(PipeMsgListener listener) {
		Validator.validateObjNotNull(listener);
		this.listener = listener;
	}

	/**
	 * Attains a timeout
	 * 
	 * @return - {@link Integer}
	 */
	private int getTimeout() {
		return timeout;
	}

	/**
	 * Sets up a timeout
	 * 
	 * @param timeout
	 *            {@link Integer}
	 */
	private void setTimeout(int timeout) {
		Validator.validateInt(timeout);
		this.timeout = timeout;
	}

	/**
	 * Sets up a {@link JxtaBiDiPipe}
	 * 
	 * @param bidiPipe
	 *            a jxta bi-directional pipe
	 */
	private void setBidiPipe(JxtaBiDiPipe bidiPipe) {
		Validator.validateObjNotNull(bidiPipe);
		this.bidiPipe = bidiPipe;
	}

	/**
	 * Sets up a bi-directional pipe advertisement
	 * 
	 * @param bidiPipeAdv
	 *            {@link PipeAdvertisement}
	 */
	private void setBidiPipeAdv(PipeAdvertisement bidiPipeAdv) {
		Validator.validateObjNotNull(bidiPipeAdv);
		this.bidiPipeAdv = bidiPipeAdv;
	}
}

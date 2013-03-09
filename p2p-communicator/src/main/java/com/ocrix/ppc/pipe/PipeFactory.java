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
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.ServerPipeAcceptListener;

/**
 * Intended for pipe creation as the following:
 * <ul>
 * <li>{@link UnicastPipe}</li>
 * </ul>
 * <ul>
 * <li>{@link UnicastSecurePipe}</li>
 * </ul>
 * <ul>
 * <li>{@link PropagatePipe}</li>
 * </ul>
 * <ul>
 * <li>{@link ServerPipe}</li>
 * </ul>
 * <ul>
 * <li>{@link BiDiPipe}</li>
 * </ul>
 * <ul>
 * <li>{@link OutputPipe}</li>
 * </ul>
 */
public class PipeFactory {

	public PipeFactory() {
	}

	/**
	 * Creates unicast pipe
	 * 
	 * @param peerGroup
	 *            a peer group to be associated with
	 * @param pipeName
	 *            a peer name to be given
	 * 
	 * @return {@link UnicastPipe}
	 * 
	 * @throws IOException
	 */
	public UnicastPipe createUnicastPipe(PeerGroup peerGroup, String pipeName)
			throws IOException {
		return new UnicastPipe(peerGroup, pipeName);
	}

	/**
	 * Creates a new instance of the asynchronous unicast secured pipe
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 * @param pipeName
	 *            a name to be given for pipe
	 * 
	 * @return {@link UnicastSecurePipe}
	 * 
	 * @throws IOException
	 */
	public UnicastSecurePipe createUnicastSecurePipe(PeerGroup peerGroup,
			String pipeName) throws IOException {
		return new UnicastSecurePipe(peerGroup, pipeName);
	}

	/**
	 * Creates an instance of the asynchronous propagate pipe
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 * @param pipeName
	 *            a name to be given for pipe
	 * 
	 * @return {@link PropagatePipe}
	 * 
	 * @throws IOException
	 */
	public PropagatePipe createPropagatePipe(PeerGroup peerGroup,
			String pipeName) throws IOException {
		return new PropagatePipe(peerGroup, pipeName);
	}

	/**
	 * Creates an instance of the ServerPipe. Its asynchronicity depends on an
	 * implementation of ServerPipeAcceptListener. It can be either synchronous
	 * or asynchronous.
	 * 
	 * @param peerGroup
	 * @param listener
	 * @param pipeName
	 * @return
	 * @throws IOException
	 */
	public ServerPipe createServerPipe(PeerGroup peerGroup,
			ServerPipeAcceptListener listener, String pipeName)
			throws IOException {
		return new ServerPipe(peerGroup, listener, pipeName);
	}

	/**
	 * Creates an instance of the BiDiPie
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 * @param serverPipeAdv
	 *            a server pipe advertisement, for BiDi there is a need to
	 *            define server pipe first.
	 * @param pipeName
	 *            a name to be given for the created pipe
	 * @param listener
	 *            that listens for incoming messages
	 * 
	 * @return {@link BiDiPipe}
	 * 
	 * @throws IOException
	 */
	public BiDiPipe createBiDiPipe(PeerGroup peerGroup,
			PipeAdvertisement serverPipeAdv, String pipeName,
			PipeMsgListener listener) throws IOException {
		return new BiDiPipe(peerGroup, serverPipeAdv, pipeName, listener);
	}

	/**
	 * Creates a new instance of the BiDi pipe
	 * 
	 * @param peerGroup
	 *            - a peer group where a bi-directional pipe will be associated
	 *            with
	 * @param serverPipeAdv
	 *            - a server pipe advertisement, for BiDi there is a need to
	 *            define server pipe first.
	 * @param pipeName
	 *            - a name to be given for the created pipe
	 * @param listener
	 *            - that listens for incoming messages
	 * @param timeout
	 *            - the number of milliseconds within which the JxtaBiDiPipe
	 *            must be successfully created. A timeout value of 0 (zero)
	 *            specifies an infinite timeout.
	 * 
	 * @return a bi-directional pipe
	 * 
	 * @throws IOException
	 *             - an exception will be thrown if the pipe cannot be created
	 *             in the allotted time.
	 */
	public BiDiPipe createBiDiPipe(PeerGroup peerGroup,
			PipeAdvertisement serverPipeAdv, String pipeName,
			PipeMsgListener listener, int timeout) throws IOException {
		return new BiDiPipe(peerGroup, serverPipeAdv, pipeName, listener,
				timeout);
	}

	/**
	 * Creates an output pipe that Sender peer will use.
	 * 
	 * @param peerGroup
	 *            - a peer group where an output pipe will be associated with
	 * @param pipeAdv
	 *            - a pipe advertisement
	 * 
	 * @return Output pipe
	 * 
	 * @throws IOException
	 *             - if a creation of output pipe failed
	 */
	public OutputPipe createOutputPipe(PeerGroup peerGroup,
			PipeAdvertisement pipeAdv) throws IOException {
		SenderPipe senderPipe = new SenderPipe();
		return senderPipe.createSenderPipe(peerGroup, pipeAdv);
	}
}

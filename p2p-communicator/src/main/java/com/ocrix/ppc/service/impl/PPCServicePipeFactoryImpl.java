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
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.QueuingServerPipeAcceptor;

import com.ocrix.ppc.pipe.BiDiPipe;
import com.ocrix.ppc.pipe.PipeFactory;
import com.ocrix.ppc.pipe.PropagatePipe;
import com.ocrix.ppc.pipe.ServerPipe;
import com.ocrix.ppc.pipe.UnicastPipe;
import com.ocrix.ppc.pipe.UnicastSecurePipe;
import com.ocrix.ppc.service.PPCServicePipeFactory;

/**
 * {@link PPCServicePipeFactory} implementation
 */
public class PPCServicePipeFactoryImpl implements PPCServicePipeFactory {
	/* Class members */
	private PipeFactory pipeFactory = null;

	/**
	 * Default constructor, creates {@link PipeFactory}
	 */
	public PPCServicePipeFactoryImpl() {
		init();
	}

	/**
	 * Initializes the {@link PipeFactory}
	 */
	public void init() {
		pipeFactory = new PipeFactory();
	}

	// @Override
	public UnicastPipe createUnicastPipe(PeerGroup peerGroup, String pipeName)
			throws IOException {
		return pipeFactory.createUnicastPipe(peerGroup, pipeName);
	}

	// @Override
	public UnicastSecurePipe createUnicastSecurePipe(PeerGroup peerGroup,
			String pipeName) throws IOException {
		return pipeFactory.createUnicastSecurePipe(peerGroup, pipeName);
	}

	// @Override
	public PropagatePipe createPropagatePipe(PeerGroup peerGroup,
			String pipeName) throws IOException {
		return pipeFactory.createPropagatePipe(peerGroup, pipeName);
	}

	// @Override
	public ServerPipe createServerPipe(PeerGroup peerGroup,
			QueuingServerPipeAcceptor serverAcceptorListener, String pipeName)
			throws IOException {
		return pipeFactory.createServerPipe(peerGroup, serverAcceptorListener,
				pipeName);
	}

	// @Override
	public BiDiPipe createBiDiPipe(PeerGroup peerGroup,
			PipeAdvertisement serverPipeAdv, String pipeName,
			PipeMsgListener listener) throws IOException {
		return pipeFactory.createBiDiPipe(peerGroup, serverPipeAdv, pipeName,
				listener);
	}

	// @Override
	public BiDiPipe createBiDiPipe(PeerGroup peerGroup,
			PipeAdvertisement serverPipeAdv, String pipeName,
			PipeMsgListener listener, int timeout) throws IOException {
		return pipeFactory.createBiDiPipe(peerGroup, serverPipeAdv, pipeName,
				listener, timeout);
	}
}

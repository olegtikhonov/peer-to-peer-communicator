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
import org.apache.log4j.Logger;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.peer.BiDiPeer;
import com.ocrix.ppc.peer.ReceiverPeer;
import com.ocrix.ppc.peer.SenderPeer;
import com.ocrix.ppc.service.PPCServicePeerFactory;

/**
 * Implementation of the {@link PPCServicePeerFactory}
 */
public class PPCServicePeerFactoryImpl implements PPCServicePeerFactory {
	/* class members */
	private Logger logger = Logger.getLogger(PPCServicePeerFactoryImpl.class);

	/* ---- end ---- */

	public void init() {

	}

	public BiDiPeer createBiDiPeer(String peerName) throws PPCException {
		BiDiPeer biDiPeer = null;
		try {
			biDiPeer = new BiDiPeer(peerName, logger, Boolean.valueOf(PPCUtils
					.getValue("jxta.logger.debug")),
					PPCUtils.getValue("thread.pool.type"), null);
			biDiPeer.setBufferSize(Integer.valueOf(PPCUtils
					.getValue("message.queue.size")));
			biDiPeer.setRetry(Integer.valueOf(PPCUtils.getValue("retry")));
		} catch (IOException e) {
			logger.error(e);
			throw new PPCException(e);
		}

		return biDiPeer;
	}

	// @Override
	public ReceiverPeer createReceiverPeer(String peerName) throws PPCException {
		ReceiverPeer rp = null;
		try {
			rp = new ReceiverPeer(peerName, logger, Boolean.valueOf(PPCUtils
					.getValue("jxta.logger.debug")));
		} catch (IOException e) {
			logger.error(e);
			throw new PPCException(e);
		}
		return rp;
	}

	// @Override
	public SenderPeer createSenderPeer(String peerName) throws PPCException {

		SenderPeer senderPeer;
		try {
			senderPeer = new SenderPeer(peerName, logger,
					Boolean.valueOf(PPCUtils.getValue("jxta.logger.debug")),
					PPCUtils.getValue("thread.pool.type"), null);
			senderPeer.setBufferSize(Integer.valueOf(PPCUtils
					.getValue("message.queue.size")));
			senderPeer.setRetry(Integer.valueOf(PPCUtils.getValue("retry")));
		} catch (IOException e) {
			logger.error(e);
			throw new PPCException(e);
		}

		return senderPeer;
	}

	/**
	 * Attains a {@link java.util.logging.Logger}
	 * 
	 * @return a logger.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Sets up a {@link Logger}
	 * 
	 * @param loggerApi
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}

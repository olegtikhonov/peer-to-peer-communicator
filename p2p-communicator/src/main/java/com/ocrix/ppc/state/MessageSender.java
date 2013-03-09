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

package com.ocrix.ppc.state;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.service.impl.PPCServiceDiscoveryFactoryImpl;
import com.ocrix.ppc.type.DefaultParameter;

@Deprecated
public class MessageSender implements Runnable {
	private AtomicReference<NetworkManager> manager;
	private Message message;
	private PipeAdvertisement pipeAdvertisement = null;
	private PipeService ps = null;
	private AtomicReference<PipeDiscovery> pipeDiscovery = null;
	private Logger logger = Logger.getLogger(MessageSender.class);

	/**
	 * Constructs a {@link MessageSender}.
	 * 
	 * @param manager
	 *            a network manager.
	 * @param message
	 *            to be set.
	 * @param pipeAdvertisement
	 *            a type of specific pipe.
	 */
	public MessageSender(NetworkManager manager, Message message,
			PipeAdvertisement pipeAdvertisement) {
		this.manager = new AtomicReference<NetworkManager>(manager);
		this.message = message;
		this.pipeAdvertisement = pipeAdvertisement;
		ps = this.manager.get().getNetPeerGroup().getPipeService();
	}

	/**
	 * Constructs a message sender.
	 * 
	 * @param manager
	 *            a network manager.
	 * @param backlog
	 * @param pipeAdvertisement
	 */
	public MessageSender(NetworkManager manager,
			BlockingQueue<Message> backlog, PipeAdvertisement pipeAdvertisement) {
		this.manager = new AtomicReference<NetworkManager>(manager);

		if (backlog != null && !backlog.isEmpty()) {
			try {
				this.message = (Message) backlog.take();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}

		this.pipeAdvertisement = pipeAdvertisement;
		ps = this.manager.get().getNetPeerGroup().getPipeService();
	}

	/**
	 * Constructs a message sender.
	 * 
	 * @param backlog
	 *            to be set.
	 * @param manager
	 *            a network manager.
	 * @param pipeDiscovery
	 *            to be set.
	 */
	public MessageSender(BlockingQueue<Message> backlog,
 			NetworkManager manager, PipeDiscovery pipeDiscovery) {
		this.manager = new AtomicReference<NetworkManager>(manager);

		if (backlog != null && !backlog.isEmpty()) {
			try {
				this.message = (Message) backlog.take();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}

		if (pipeDiscovery != null){
			this.pipeDiscovery = new AtomicReference<PipeDiscovery>(
					pipeDiscovery);
		}
	}

	public synchronized boolean start() throws IOException {
		boolean isReceived = false;
		try {

			if (pipeAdvertisement == null) {
				waitForFindingPipe(DefaultParameter.TRIAL_NUMBER.getCode());
			}

			if (pipeAdvertisement != null) {
				OutputPipe pipe = ps.createOutputPipe(pipeAdvertisement, 20000);
				isReceived = pipe.send(this.message);
			} else { // tried to find the destination, however did not succeed
						// TODO: recovering policy
				throw new PPCException("Tried [ "
						+ DefaultParameter.TRIAL_NUMBER.getCode()
						+ " times to find " + message.getDestination()
						+ ", but all in vain");
			}

		} catch (Exception e) {
			logger.error(MessageSender.class.getName() + e.getMessage());
		}
		return isReceived;
	}

	/**
	 * Waits for finding pipe.
	 * 
	 * @param numberOfTrials
	 * 
	 * @throws IOException
	 */
	private void waitForFindingPipe(int numberOfTrials) throws IOException {
		boolean isFound = false;
		int countDown = numberOfTrials;

		PPCServiceDiscoveryFactoryImpl dfs = new PPCServiceDiscoveryFactoryImpl();
		PipeDiscovery pipeDiscovery = dfs.createPipeDiscovery(manager.get());

		Validator.validateObjNotNull(pipeDiscovery);

		while (!isFound && numberOfTrials-- > 0) {
			List<PipeAdvertisement> findings;
			try {
				findings = pipeDiscovery.lookup();
				if (findings != null && findings.size() > 0) {
					for (PipeAdvertisement ps : findings) {
						if (ps.toString().contains(message.getDestination())) {
							pipeAdvertisement = ps;
							isFound = true;
							break;
						}
					}
				} else {
					manager.get().waitForRendezvousConnection(
							DefaultParameter.TWO_SEC.getCode());
					logger.info(". ");
				}
			} catch (PPCException e) {
				logger.error(e.getMessage());
			}
		}
		logger.debug("\nIs found a " + message.getDestination() + " ? "
				+ isFound + ", number of iterations been waiting " + countDown);
	}

	protected PipeAdvertisement getFoundPipe(String pipeName, Message message)
			throws PPCException, IOException {
		PipeAdvertisement pipeAdvertisement = null;
		List<PipeAdvertisement> findings = pipeDiscovery.get().lookup();
		for (PipeAdvertisement ps : findings) {
			if (ps.toString().contains(message.getDestination()))
				pipeAdvertisement = ps;
			break;
		}
		return pipeAdvertisement;
	}

	// @Override
	public void run() {
		try {
			start();
		} catch (IOException e) {
			logger.error(e);
		}
	}
}

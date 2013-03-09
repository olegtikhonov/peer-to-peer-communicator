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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.OutputPipe;
import net.jxta.protocol.PipeAdvertisement;

import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.pipe.PipeFactory;
import com.ocrix.ppc.type.DefaultParameter;
import com.ocrix.ppc.type.Tag;

/**
 * Implements an {@link IMessageSender}.
 */
public class MessageSenderJavaThread implements IMessageSender, Runnable {
	/* Class members */
	private AtomicReference<Message> message = null;
	private PeerGroup peerGroup = null;
	private AtomicReference<PipeAdvertisement> pipeAdvertisement = null;
	private AtomicReference<PipeDiscovery> pipeDiscovery = null;
	private AtomicReference<PipeFactory> pipeFactory = null;
	private AtomicBoolean isStopped = new AtomicBoolean(false);
	private AtomicInteger retry = new AtomicInteger();
	private static final Logger logger = Logger
			.getLogger(MessageSenderJavaThread.class);

	/* End of class members declaration */

	/**
	 * Constructor
	 * 
	 * @param backlog
	 *            - a queue that contains messages to be sent
	 * @param peerGroup
	 *            a {@link PeerGroup}
	 * @param pipeDiscovery
	 *            a {@link PipeDiscovery}
	 */
	public MessageSenderJavaThread(BlockingQueue<Message> backlog,
			PeerGroup peerGroup, PipeDiscovery pipeDiscovery)
			throws PPCException {
		takeMessage(backlog);
		setPeerGroup(peerGroup);
		setPipeDiscovery(pipeDiscovery);
		setPipeFactory(new PipeFactory());
		pipeAdvertisement = new AtomicReference<PipeAdvertisement>();
	}

	/**
	 * Creates a message sender java thread.
	 * 
	 * @param backlog
	 *            a size of queue.
	 * @param peerGroup
	 *            a group a peer is participate to.
	 * @param pipeDiscovery
	 * @param retry
	 * 
	 * @throws PPCException
	 */
	public MessageSenderJavaThread(BlockingQueue<Message> backlog,
			PeerGroup peerGroup, PipeDiscovery pipeDiscovery, int retry)
			throws PPCException {
		takeMessage(backlog);
		setPeerGroup(peerGroup);
		setPipeDiscovery(pipeDiscovery);
		setPipeFactory(new PipeFactory());
		pipeAdvertisement = new AtomicReference<PipeAdvertisement>();
		setRetry(retry);
	}

	// @Override
	public void run() {
		Thread.currentThread().setName(
				Tag.BIDI_THREAD_PREFIX.getValue() + System.currentTimeMillis());
		try {
			if (!Thread.currentThread().isInterrupted())
				start();
		} catch (Exception e) {
			logger.error(MessageSenderJavaThread.class.getName() + " "
					+ e.getMessage());
		}
	}

	// @Override
	public void setMessage(Message message) {

		if (message != null) {
			this.message = new AtomicReference<Message>(message);
		}

	}

	/**
	 * Attains a {@link AtomicReference}
	 * 
	 * @return a message atomic
	 */
	public AtomicReference<Message> getMessage() {
		return message;
	}

	/**
	 * Changes a flag indicating either stop or not stop a running thread.
	 * 
	 * @param isStopped
	 */
	public void setIsStopped(boolean isStopped) {
		this.isStopped.set(isStopped);
	}

	/**
	 * Looks up a pipeAdvertisement
	 * 
	 * @param message
	 *            a message to be sent
	 * 
	 * @return a {@link PipeAdvertisement}
	 * 
	 * @throws PPCException
	 *             - if discovery service is null
	 * @throws IOException
	 */
	private PipeAdvertisement getFoundPipe(Message message)
			throws PPCException, IOException {

		PipeAdvertisement pipeAdvertisement = null;
		List<PipeAdvertisement> findings = pipeDiscovery.get().lookup();
		for (PipeAdvertisement ps : findings) {
			if (ps.toString().contains(message.getDestination())) {
				pipeAdvertisement = ps;
			}

			break;
		}

		return pipeAdvertisement;
	}

	/**
	 * Waits and tries to find a pipe
	 * 
	 * @param numberOfTrials
	 *            - indicates how many times to try
	 */
	private synchronized void waitForFindingPipe(int numberOfTrials) {
		boolean isFound = false;
		try {
			pipeAdvertisement.set(getFoundPipe(message.get()));
			if (pipeAdvertisement.get() == null) {
				while (!Thread.currentThread().isInterrupted()
						&& !isStopped.get() && !isFound && numberOfTrials-- > 0) {
					PPCUtils.sleep(DefaultParameter.TWO_SEC.getCode());
					pipeAdvertisement.set(getFoundPipe(message.get()));
					if (pipeAdvertisement.get() != null)
						isFound = true;
				}
			}
		} catch (PPCException e) {
			logger.error(this.getClass().getName() + " " + e.getMessage());
		} catch (IOException e) {
			logger.error(this.getClass().getName() + " " + e.getMessage());
		}
	}

	/**
	 * Takes a message from the queue
	 * 
	 * @param backlog
	 *            a {@link BlockingQueue}
	 */
	private void takeMessage(BlockingQueue<Message> backlog) {
		if (!Thread.currentThread().isInterrupted() && backlog != null
				&& !backlog.isEmpty()) {
			try {
				Message message = backlog.take();
				setMessage(message);
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	/**
	 * Starts a sending message process.
	 * 
	 * @return boolean indicating if a messages was send, <true> means that
	 *         message sent successfully
	 * 
	 * @throws IOException
	 *             - if could not create a {@link OutputPipe}
	 */
	private synchronized boolean start() throws IOException {
		boolean isReceived = false;
		try {
			if (!Thread.currentThread().isInterrupted()
					&& pipeAdvertisement.get() == null) {
				waitForFindingPipe(getRetry());
			}
			if (!Thread.currentThread().isInterrupted()
					&& pipeAdvertisement.get() != null) {
				OutputPipe pipe = pipeFactory.get().createOutputPipe(
						getPeerGroup(), pipeAdvertisement.get());
				
				if (pipe == null) {
					logger.debug("OUTPUT PIPE IS NULL");
				}
				try {
					isReceived = pipe.send(this.message.get());
				} catch (Exception e) {
					logger.error(this.getClass().getName() + " "
							+ e.getMessage());
				}

			} else if (!Thread.currentThread().isInterrupted()) { // tried to
																	// find the
																	// destination,
																	// however
																	// did not
																	// succeed
				throw new PPCException("Tried [ " + getRetry()
						+ "] times to find " + message.get().getDestination()
						+ ", but all in vain");
			}

		} catch (Exception e) {
			System.out
					.println(this.getClass().getName() + " " + e.getMessage());
		}
		return isReceived;
	}

	/**
	 * Sets up a pipe factory
	 * 
	 * @param factory
	 *            a {@link PipeFactory}
	 */
	private void setPipeFactory(PipeFactory factory) {
		pipeFactory = new AtomicReference<PipeFactory>(factory);
	}

	/**
	 * Sets up a {@link PipeDiscovery}
	 * 
	 * @param discovery
	 *            a pipe discovery
	 * 
	 * @throws PPCException
	 *             - if a pipe discovery is null
	 */
	private void setPipeDiscovery(PipeDiscovery discovery) throws PPCException {
		if (discovery != null)
			this.pipeDiscovery = new AtomicReference<PipeDiscovery>(discovery);
		else
			throw new PPCException("Pipe discovery is null");
	}

	/**
	 * Sets up a {@link PeerGroup}
	 * 
	 * @param peerGroup
	 */
	private void setPeerGroup(PeerGroup peerGroup) {
		this.peerGroup = peerGroup;
	}

	/**
	 * Attains a {@link PeerGroup}
	 * 
	 * @return
	 */
	private PeerGroup getPeerGroup() {
		return peerGroup;
	}

	/**
	 * Gets a flag for stopping the threads.
	 * 
	 * @return
	 */
	protected AtomicBoolean getIsStopped() {
		return isStopped;
	}

	/**
	 * Sets retry,a number of trials to send message.
	 * 
	 * @param retry
	 */
	public void setRetry(int retry) {
		this.retry = new AtomicInteger(new Integer(retry));
	}

	/**
	 * Gets a retry.
	 * 
	 * @return a number of trials to send message.
	 */
	private int getRetry() {
		return retry.get();
	}
}

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
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import net.jxta.discovery.DiscoveryService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.JxtaBiDiPipe;

import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.pipe.PipeFactory;
import com.ocrix.ppc.type.DefaultParameter;
import com.ocrix.ppc.type.Tag;

/**
 * Sends a message. Look up for the destination, if it's up then sends a
 * message, if it's not - waits and looks up again for a while.
 * 
 * If a message did not reach its destination, it retransmits.
 */
public class BiDiMessageSender implements Runnable {
	/* Class members */
	private Message message = null;
	private PipeFactory pipeFactory = null;
	private PeerGroup peerGroup = null;
	private PipeDiscovery pipeDiscovery = null;
	private PipeMsgListener listener = null;
	/* Uses in WAS environment */
	// private AtomicBoolean isStopped = new AtomicBoolean(false);

	private boolean isStopped = false;

	/* A number of times for message sending trials */
	// private AtomicInteger retry = new AtomicInteger();
	private int retry = 0;

	private volatile JxtaBiDiPipe pipe;
	private Logger logger = Logger.getLogger(BiDiMessageSender.class);

	/* ---------- end of class members --------- */

	/**
	 * Creates an instance of {@link BiDiMessageSender}
	 * 
	 * @param messageToBeSent
	 *            - a {@link Message} to be sent.
	 * @param pipeFactory
	 *            - a {@link PipeFactory} is uses for creating a pipe.
	 * @param peerGroup
	 *            - a {@link PeerGroup}.
	 * @param pipeDiscovery
	 *            - a {@link PipeDiscovery}.
	 * @param listener
	 *            - a {@link PipeMsgListener}.
	 */
	public BiDiMessageSender(Message messageToBeSent, PipeFactory pipeFactory,
			PeerGroup peerGroup, PipeDiscovery pipeDiscovery,
			PipeMsgListener listener) {
		/* Sets up a message to be sent */
		setMessage(messageToBeSent);

		initialize(pipeFactory, peerGroup, pipeDiscovery, listener);
	}

	/**
	 * Creates an instance of {@link BiDiMessageSender}.
	 * 
	 * @param backLog
	 *            - a message buffer, where messages are accumulated.
	 * @param pipeFactory
	 *            - a {@link PipeFactory}.
	 * @param peerGroup
	 *            - a {@link PeerGroup}.
	 * @param pipeDiscovery
	 *            - a {@link PipeDiscovery}.
	 * @param listener
	 *            - a {@link PipeMsgListener}.
	 */
	public BiDiMessageSender(BlockingQueue<Message> backLog,
			PipeFactory pipeFactory, PeerGroup peerGroup,
			PipeDiscovery pipeDiscovery, PipeMsgListener listener) {
		try {
			if (backLog != null && !backLog.isEmpty())
				setMessage((Message) backLog.take());
		} catch (InterruptedException e) {
			logger.error(BiDiMessageSender.class.getName() + " "
					+ e.getMessage());
		}

		initialize(pipeFactory, peerGroup, pipeDiscovery, listener);
	}

	/**
	 * Creates an instance of Bi-directional message sender
	 * 
	 * @param backLog
	 *            a message buffer
	 * @param pipeFactory
	 *            a {@link PipeFactory}
	 * @param peerGroup
	 *            a {@link PeerGroup}
	 * @param pipeDiscovery
	 *            a {@link PipeDiscovery}
	 * @param listener
	 *            a {@link PipeMsgListener}
	 * @param retry
	 *            a number of retries
	 */
	public BiDiMessageSender(BlockingQueue<Message> backLog,
			PipeFactory pipeFactory, PeerGroup peerGroup,
			PipeDiscovery pipeDiscovery, PipeMsgListener listener, int retry) {
		try {
			if (backLog != null && !backLog.isEmpty()) {
				setMessage((Message) backLog.take());
			}
		} catch (InterruptedException e) {
			logger.error(BiDiMessageSender.class.getName() + " "
					+ e.getMessage());
		}

		initialize(pipeFactory, peerGroup, pipeDiscovery, listener);

		setRetry(retry);
	}

	/**
	 * Sets a status flag.
	 * 
	 * @param status
	 */
	public void setStopped(boolean status) {
		isStopped = status;
	}

	// @Override
	public void run() {
		PipeAdvertisement pipeAdvertisement = null;
		Validator.validateObjNotNull(getMessage());
		Thread.currentThread().setName(
				Tag.BIDI_THREAD_PREFIX.getValue() + System.currentTimeMillis());
		try {
			pipeAdvertisement = lookupPipe(pipeAdvertisement);
			if (pipeAdvertisement != null) {
				sendMessage(pipeAdvertisement);
			} else { // wait for a while
				pipeAdvertisement = waitForFindingPipe(getRetry());
				if (pipeAdvertisement != null) {
					sendMessage(pipeAdvertisement);
				} else {
					setStopped(true);
					logger.warn("Could not send message, as result the message ["
							+ getMessage() + "] is dropped");
					Thread.currentThread().interrupt();
					closePipe();
				}
			}
		} catch (PPCException e) {
			logger.error(BiDiMessageSender.class.getName() + " "
					+ e.getMessage());
		}
	}

	/**
	 * Initializes the following:<br>
	 * <ul>
	 * <li>{@link PipeFactory}</li>
	 * <li>{@link PeerGroup}</li>
	 * <li>{@link PipeDiscovery}</li>
	 * <li>{@link PipeMsgListener}</li>
	 * </ul>
	 * 
	 * 
	 * @param pipeFactory
	 * @param peerGroup
	 * @param pipeDiscovery
	 * @param listener
	 */
	private void initialize(PipeFactory pipeFactory, PeerGroup peerGroup,
			PipeDiscovery pipeDiscovery, PipeMsgListener listener) {
		setPipeFactory(pipeFactory);
		setPeerGroup(peerGroup);
		setPipeDiscovery(pipeDiscovery);
		setListener(listener);
	}

	/**
	 * Seeks a {@link PipeAdvertisement}
	 * 
	 * @param pipeAdvertisement
	 *            {@link PipeAdvertisement}
	 * 
	 * @return a {@link PipeAdvertisement}
	 * 
	 * @throws PPCException
	 *             - if could not find a {@link PipeAdvertisement}
	 * @throws IOException
	 */
	private PipeAdvertisement lookupPipe(PipeAdvertisement pipeAdvertisement)
			throws PPCException {
		List<PipeAdvertisement> findings = null;

		try {
			findings = pipeDiscovery.lookup();
			for (PipeAdvertisement ps : findings) {
				if (ps.toString().contains(getMessage().getDestination())) {
					pipeAdvertisement = ps;
					break;
				}
			}
		} catch (Exception e) {
			throw new PPCException(e);
		}

		return pipeAdvertisement;
	}

	/**
	 * Sends a message.
	 * 
	 * @param pipeAdvertisement
	 */
	private synchronized void sendMessage(PipeAdvertisement pipeAdvertisement) {
		if (!Thread.currentThread().isInterrupted() && !isStopped) {
			try {
				pipe = getPipeFactory().createBiDiPipe(getPeerGroup(),
						pipeAdvertisement, getMessage().getDestination(),
						getListener()).getBidiPipe();

				if (pipe != null) {
					try {
						pipe.connect(getPeerGroup(), null, pipeAdvertisement,
								Integer.MAX_VALUE, getListener());

						boolean status = pipe.sendMessage(getMessage());
						logger.debug("is message send " + status);

						if (!status) {
							retransmit(pipe);
						}

					} catch (Exception e) {
						logger.error(BiDiMessageSender.class.getName() + " " + e.getMessage());
					}
				}

			} catch (IOException e) {
				logger.error(BiDiMessageSender.class.getName() + " "
						+ e.getMessage());
			} finally {
				// closePipe();
			}
		} else { // should close a bidi pipe
			closePipe();
		}
	}

	private void closePipe() {
		try {
			logger.debug("Closing a BIDI PIPE");
			if (pipe != null)
				pipe.close();
		} catch (IOException e) {
			logger.error(BiDiMessageSender.class.getName() + " "
					+ e.getMessage());
		}
	}

	/**
	 * Waits and tries to find pipe
	 * 
	 * @param numberOfTrials
	 *            - a number of trials to find a {@link PipeAdvertisement}
	 * 
	 * @return {@link PipeAdvertisement}
	 * 
	 * @throws IOException
	 *             - if {@link DiscoveryService} could be be initialized
	 */
	private PipeAdvertisement waitForFindingPipe(int numberOfTrials) {
		boolean isFound = false;
		int countDown = numberOfTrials;
		PipeAdvertisement pipeAdvertisement = null;
		Validator.validateObjNotNull(getPipeDiscovery());
		List<PipeAdvertisement> findings = null;

		while (!Thread.currentThread().isInterrupted() && !isStopped
				&& !isFound && countDown-- > 0) {

			try {
				findings = getPipeDiscovery().lookup();
				if (findings != null && !findings.isEmpty()
						&& findings.size() > 0) {
					for (PipeAdvertisement ps : findings) {
						if (ps.toString().contains(message.getDestination())) {
							pipeAdvertisement = ps;
							isFound = true;
							break;
						}
					}
				} else {
					sleep();
				}
			} catch (PPCException e) {
				logger.error(BiDiMessageSender.class.getName() + " "
						+ e.getMessage());
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return pipeAdvertisement;
	}

	/**
	 * In case when messages did not reach its destination by any reason, tries
	 * to re-send
	 * 
	 * @param pipe
	 *            {@link JxtaBiDiPipe}
	 * 
	 * @throws IOException
	 *             - if a sending message failed
	 */
	private void retransmit(JxtaBiDiPipe pipe) {
		Validator.validateObjNotNull(pipe);
		int countDown = getRetry();
		boolean status = false;

		while (!Thread.currentThread().isInterrupted() && !isStopped && !status
				&& countDown-- > 0) {
			logger.debug("Retransmition ...");
			if (pipe.isBound()) {
				try {
					status = pipe.sendMessage(getMessage());
				} catch (IOException e) {
					logger.error(e);
				}
			}
			sleep();
		}
	}

	/**
	 * Thread.sleep. A default is a two seconds
	 */
	private void sleep() {
		try {
			TimeUnit.SECONDS.sleep(DefaultParameter.TWO_SEC.getCode());
		} catch (InterruptedException e) {
			logger.error(BiDiMessageSender.class.getName() + " "
					+ e.getMessage());
		}
	}

	/**
	 * Sets up a {@link Message}
	 * 
	 * @param - a {@link Message}
	 */
	private void setMessage(Message message) {
		Validator.validateObjNotNull(message);
		this.message = message;
	}

	/**
	 * Attains a {@link Message}
	 * 
	 * @return
	 */
	private Message getMessage() {
		return message;
	}

	/**
	 * Sets up {@link PipeFactory}
	 * 
	 * @param pipeFactory
	 */
	private void setPipeFactory(PipeFactory pipeFactory) {
		Validator.validateObjNotNull(pipeFactory);
		this.pipeFactory = pipeFactory;
	}

	/**
	 * Attains a {@link PipeFactory}
	 * 
	 * @return
	 */
	private PipeFactory getPipeFactory() {
		return pipeFactory;
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
	 * Sets up a {@link PeerGroup}
	 * 
	 * @param peerGroup
	 */
	private void setPeerGroup(PeerGroup peerGroup) {
		Validator.validateNetPeerGroup(peerGroup);
		this.peerGroup = peerGroup;
	}

	/**
	 * Attains a {@link PipeDiscovery}
	 * 
	 * @return a pipe discovery
	 */
	private PipeDiscovery getPipeDiscovery() {
		return pipeDiscovery;
	}

	/**
	 * Sets up a {@link PipeDiscovery}
	 * 
	 * @param pipeDiscovery
	 *            a pipe discovery
	 */
	private void setPipeDiscovery(PipeDiscovery pipeDiscovery) {
		Validator.validateObjNotNull(pipeDiscovery);
		this.pipeDiscovery = pipeDiscovery;
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
	 */
	private void setListener(PipeMsgListener listener) {
		Validator.validateObjNotNull(listener);
		this.listener = listener;
	}

	/**
	 * Sets retry number.
	 * 
	 * @param retry
	 */
	private void setRetry(int retry) {
		this.retry = retry;
	}

	/**
	 * TODO: remove.
	 * 
	 * @return
	 */
	private int getRetry() {
		if (this.retry <= 0) {
			setRetry(Integer.valueOf(PPCUtils.getValue("retry")));
		}
		return retry;
	}
}

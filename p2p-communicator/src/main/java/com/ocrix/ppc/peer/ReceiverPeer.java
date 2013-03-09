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

package com.ocrix.ppc.peer;

import java.io.File;
import java.io.IOException;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Logger;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.listener.MessageReceiver;
import com.ocrix.ppc.listener.ReceiverObservable;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.service.impl.PPCServicePipeFactoryImpl;

/**
 * Represents a Receiver peer, that responsible for getting the incoming
 * messages. <br>
 * In order that client will be able to get messages it should subscribe to
 * that, <br>
 * implementing the {@link Observer} interface
 * 
 */
public class ReceiverPeer extends Peer implements PipeMsgListener {
	/* A peer group */
	private PeerGroup peerGroup = null;
	/* A pipe advertisement */
	private PipeAdvertisement pipeAdvertisement = null;
	/* Input pipe - for catching coming messages */
	private InputPipe inputPipe = null;
	/* Observable */
	private AtomicReference<ReceiverObservable> publisher = null;

	// ---------- END OF CLASS MEMBERS ----------\\

	/**
	 * Creates a Receiver peer. That peer intended to get incoming messages.
	 * 
	 * @param peerName
	 *            a name to be given
	 * @param cachePath
	 *            a path to the cache folder where a peer will save its cache
	 * 
	 * @throws IOException
	 *             - if initialization is not succeeded
	 * @throws PPCException
	 *             - if could not create an input pipe
	 */
	public ReceiverPeer(String peerName, String cachePath) throws IOException,
			PPCException {
		super(peerName, cachePath);
		publisher = new AtomicReference<ReceiverObservable>(
				new ReceiverObservable());
		init();
	}

	/**
	 * Creates a receiver peer.
	 * 
	 * @param peerName
	 *            - a name to be given
	 * @param log
	 *            {@link ILogger}
	 * @param isDebug
	 *            - a flag indicating activate JXTA logger or not.<b>true</b>
	 *            indicates to activate JXTA logger
	 * 
	 * @throws IOException
	 *             - if could not create the peer
	 * @throws PPCException
	 *             - if could not start JXTA network
	 */
	public ReceiverPeer(String peerName, Logger log, boolean isDebug)
			throws IOException, PPCException {
		super(peerName, log, isDebug);

		if (isDebug) {/* JXTA Logger in Debug mode is ON */
			System.setProperty("net.jxta.logging.Logging", "FINEST");
			System.setProperty("net.jxta.level", "FINEST");
		} else {
			System.setProperty("net.jxta.logging.Logging", "OFF");
			System.setProperty("net.jxta.level", "OFF");
		}

		publisher = new AtomicReference<ReceiverObservable>(
				new ReceiverObservable());
		init();
	}

	@Override
	public void destroy() {
		/* Closes the input pipe */
		if (inputPipe != null)
			inputPipe.close();
		/* Stops the JXTA's task manager */
		net.jxta.impl.util.threads.TaskManager tm = getPeerManager()
				.getNetPeerGroup().getTaskManager();
		/* Stops JXTA's network manager */
		getPeerManager().stopNetwork();
		if (tm != null)
			tm.shutdown();
	}

	// @Override
	public void pipeMsgEvent(PipeMsgEvent event) {
		/* event.getMessage is a type of Message and it's not TextualMessage */
		try {
			Validator.validateObjNotNull(event);
			Message myMessage = PPCUtils.convertJxtaMsgToPpcMsg(event
					.getMessage());
			publisher.get().alter();
			publisher.get().notifySubscribers(myMessage);

		} catch (Exception e) {
			getLog().error(
					ReceiverPeer.class.getName() + " pipeMsgEvent "
							+ e.getMessage());
		}
	}

	/**
	 * 
	 * @param subscriber
	 */
	public void subscribeReceiveMessages(MessageReceiver subscriber) {
		publisher.get().subscribeReceiveMessages(subscriber);
	}

	/**
	 * Initializes the following:
	 * <ul>
	 * <li>{@link PeerGroup}</li>
	 * <li>{@link PPCServicePipeFactoryImpl}</li>
	 * <li>{@link PipeAdvertisement}</li>
	 * <li>{@link InputPipe}</li>
	 * </ul>
	 * 
	 * @throws IOException
	 *             - if could not create a {@link InputPipe}
	 * 
	 * @throws PPCException
	 *             - if {@link InputPipe} is null
	 */
	protected void init() throws IOException, PPCException {
		/* Deletes old cache if exists */
		PPCUtils.deleteDir(new File(getCachePath()));
		/* Creates a new one */
		PPCUtils.createDir(new File(getCachePath()));
		/* Starts a JXTA network */
		peerGroup = startNetwork();
		/* Creates a pipe factory */
		PPCServicePipeFactoryImpl pfc = new PPCServicePipeFactoryImpl();
		/* Creates a pipe advertisement */
		pipeAdvertisement = pfc.createUnicastPipe(peerGroup, getPeerName())
				.getPipeAdv();
		/* Creates an input pipe for listening */
		inputPipe = peerGroup.getPipeService().createInputPipe(
				pipeAdvertisement, this);
		/* Validates a created input pipe */
		validateInputPipe(inputPipe);
	}

	/**
	 * Validates if an input pipe is not null
	 * 
	 * @param inputPipe
	 *            {@link InputPipe}
	 * 
	 * @throws PPCException
	 *             if input pipe is null
	 */
	private void validateInputPipe(InputPipe inputPipe) throws PPCException {
		if (inputPipe == null)
			throw new PPCException("cannot open InputPipe");
	}
}

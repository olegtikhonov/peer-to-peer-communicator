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

import org.apache.log4j.Logger;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.message.Message;

import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.pipe.OutputPipeListener;


/**
 * Represents a {@link OutputListener} 
 */
public class OutputListener implements OutputPipeListener{
	/* Class members */
	private Message message = null;
	private OutputPipe outputPipe = null;
	private static final Logger logger = Logger.getLogger(OutputListener.class);
	/* --------- end of class members declarations --------- */
	
	
	/**
	 * Creates a {@link OutputListener}
	 * 
	 * @param {@link {@link Message}}
	 */
	public OutputListener(Message message) {
		setMessage(message);
	}
	
	
//	@Override
	public void outputPipeEvent(OutputPipeEvent event) {
		@SuppressWarnings("unused")
		boolean isSent = false;
		setOutputPipe(event.getOutputPipe());
		// send the message
        try {
        	isSent = getOutputPipe().send(getMessage());
		} catch (IOException e) {
			logger.error(e);
		}
	}

	
	/** 
	 * Closes the pipe. 
	 */
	protected void stop() {
		if(getOutputPipe() != null) {
			getOutputPipe().close();
		}
	}

	
	/**
	 * Sets up a {@link Message}.
	 * 
	 * @param message
	 */
	private void setMessage(Message message) {
		Validator.validateObjNotNull(message);
		this.message = message;
	}


	/**
	 * Attains a {@link Message}.
	 * 
	 * @return a message
	 */
	private Message getMessage() {
		return message;
	}
	
	
	/**
	 * Sets up a {@link OutputPipe}.
	 * 
	 * @param outputPipe
	 */
	private void setOutputPipe(OutputPipe outputPipe) {
		this.outputPipe = outputPipe;
	}


	/**
	 * Attains a {@link OutputPipe}.
	 * 
	 * @return an output pipe
	 */
	private OutputPipe getOutputPipe() {
		return outputPipe;
	}
}

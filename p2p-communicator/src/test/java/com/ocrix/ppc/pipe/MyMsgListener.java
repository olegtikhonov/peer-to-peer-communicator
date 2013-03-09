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

import org.apache.log4j.Logger;

import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.endpoint.MessageElement;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;

public class MyMsgListener implements PipeMsgListener {
	private static final Logger logger = Logger.getLogger(MyMsgListener.class);

	public void pipeMsgEvent(PipeMsgEvent event) {
		ElementIterator ei = event.getMessage().getMessageElements();
		while (ei.hasNext()) {
			MessageElement messageElement = (MessageElement) ei.next();
			logger.info(messageElement.toString());
		}
	}
}

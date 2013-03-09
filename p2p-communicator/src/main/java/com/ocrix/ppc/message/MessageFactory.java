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

package com.ocrix.ppc.message;


/**
 * Creates a message of following types:
 * <br><b>TEXTUAL</b> i.e a message payload contains text 
 * <br><b>BINARY</b> i.e a message payload contains byte[]
 * <br><b>XML</b> i.e a message payload contains XML
 */
public class MessageFactory {
	
	/**
	 * Default constructor, just in case
	 */
	public MessageFactory() { }
	
	
	/**
	 * Creates an instance of textual message
	 * @param source - means who is sending
	 * @param destination - means who is receiving
	 * @param payLoad - a message itself
	 * 
	 * @return {@link TextualMessage}
	 */
	public TextualMessage createTextualMessage(String source, String destination, String payLoad) {
		return new TextualMessage(source, destination, payLoad);
	}
	
	/**
	 * Creates a binary message, i.e. a message contains byte[] as its pay-load
	 * 
	 * @param source a sender
	 * @param destination a recipient
	 * @param data a byte[]
	 * 
	 * @return {@link BinaryMessage}
	 */
	public BinaryMessage createBinaryMessage(String source, String destination, byte[] data){
		return new BinaryMessage(source, destination, data);
	}
	
	/**
	 * Creates an object message, i.e. a message that contains {@link Object} as its payload
	 * 
	 * @param source - who is sending
	 * @param destination - who is receiving
	 * @param object - a pay-load
	 * 
	 * @return {@link ObjectMessage}
	 */
	public ObjectMessage createObjectMessage(String source, String destination, Object object){
		return new ObjectMessage(source, destination, object);
	}
}

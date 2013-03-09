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

package com.ocrix.ppc.exception;


/**
 * Creates PpcException
 *
 */
public class PPCException extends Exception {

	private static final long serialVersionUID = 2229798730674522233L;

	/**
	 * Creates PpcException
	 */
	public PPCException() {
		super();
	}
	
	/**
	 * Creates PpcException describing the error
	 * 
	 * @param message error description
	 */
	public PPCException(String message){
		super(message);
	}
	
	/**
	 * Creates PpcException describing the cause
	 * 
	 * @param cause of the exception
	 */
	public PPCException(Throwable cause){
		super(cause);
	}
	
	/**
	 * Creates PpcException describing the error and the cause of the exception
	 * 
	 * @param message - error description
	 * @param cause of the Eception
	 */
	public PPCException(String message, Throwable cause){
		super(message, cause);
	}
}

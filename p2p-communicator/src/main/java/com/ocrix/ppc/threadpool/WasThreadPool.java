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

package com.ocrix.ppc.threadpool;

//import javax.naming.InitialContext;
//import javax.naming.NamingException;

//import com.ibm.websphere.asynchbeans.WorkException;
//import com.ibm.websphere.asynchbeans.WorkManager;
//import com.ocrix.ppc.commons.Validator;
//import com.ocrix.ppc.state.WasBiDiMessageSender;

/**
 * An implementation of the {@link TaskManager}
 */
public class WasThreadPool {// implements TaskManager{
	/* WAS' work manager */
	// private WorkManager workManager;

	/**
	 * Constructor
	 * 
	 * @param jndiLookupName
	 */
	public WasThreadPool(String jndiLookupName) {
		// InitialContext ic = null;
		// try {
		// // ic = new InitialContext();
		//
		// // if(ic != null)
		// // setWorkManager((WorkManager) ic.lookup(jndiLookupName));
		//
		// } catch (NamingException e) {
		// System.out.println(WasThreadPool.class.getName() + " " +
		// e.getMessage());
		// }
	}

	// @Override
	// public void execute(Runnable jobToDo) {
	// if(getWorkManager() != null){
	// WasBiDiMessageSender sender = (WasBiDiMessageSender) jobToDo;
	// try {
	// getWorkManager().startWork(sender);//or startWork
	// } catch (WorkException e) {
	// System.out.println(WasThreadPool.class.getName() + " " + e.getMessage());
	// } catch (IllegalArgumentException e) {
	// System.out.println(WasThreadPool.class.getName() + " " + e.getMessage());
	// }
	//
	// }else { //TODO:
	//
	// }
	// }

	// @Override
	// public void shutdown() {
	// // TODO Auto-generated method stub
	//
	// }

	// @Override
	// public void init() {
	// // TODO Auto-generated method stub
	//
	// }

	/**
	 * Attains a {@link WorkManager}
	 * 
	 * @return a WAS work manager
	 */
	// public WorkManager getWorkManager() {
	// return workManager;
	// }

	/**
	 * Sets up a {@link WorkManager}
	 * 
	 * @param workManager
	 */
	// private void setWorkManager(WorkManager workManager) {
	// Validator.validateObjNotNull(workManager);
	// // this.workManager = workManager;
	// }
}

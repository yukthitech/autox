/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yukthitech.autox;

/**
 * Listener to listen to step events which can be added to automation context.
 * @author akiran
 */
public interface IStepListener
{
	public void stepStarted(IStep step);
	
	/**
	 * Expected to be used by steps to inform about intermediate phases with messages.
	 * @param step
	 * @param mssg
	 */
	public void stepPhase(IStep step, String mssg);
	
	public void stepCompleted(IStep step);
	
	public void stepErrored(IStep step, Exception ex);
}

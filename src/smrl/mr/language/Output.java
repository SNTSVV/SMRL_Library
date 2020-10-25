/*******************************************************************************
 * Copyright (c) University of Luxembourg 2018-2020
 * Created by Fabrizio Pastore (fabrizio.pastore@uni.lu), Xuan Phu MAI (xuanphu.mai@uni.lu)
 *     
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package smrl.mr.language;

import java.io.File;
import java.util.List;

public interface Output {

	/**
	 * @return the last redirect URL will be returned
	 */
	public String redirectURL();
	
	public boolean isError();
	
	/**
	 * Check if the last output contains an empty file
	 * @return true if the last output contains an empty file
	 */
	public boolean isEmptyFile();
	
	/**
	 * @return File of the last output
	 */
	public File file();
	
	/**
	 * Check if the last output contains a file
	 * @return true if the last output does not contain any file
	 */
	public boolean noFile();
	
	public boolean hasStrictTransportSecurityHeader();
	
	public String getChannel();
	
	public Session getSession();	// get the last session status
	
	public Session getSession(int pos);	//get the session status after executing the action at the pos position 

	public boolean containListOfTags();	//check if the output contains lists of tags
	
	public List<CollectionOfConcepts> listsOfTags(); //get all list of tags from the output
	
	public CollectionOfConcepts listOfTags(String key); //get the list of tags with "key" name from the output

}

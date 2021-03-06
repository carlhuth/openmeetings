/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.test.userdata;

import static org.junit.Assert.assertNotNull;

import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.remote.MainService;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestLogin extends AbstractJUnitDefaults {
	
	@Autowired
	private MainService mService;
	
	@Autowired
	private UserManager userManager;

	@Test
	public void testTestLogin(){
		Sessiondata sessionData = mService.getsessiondata();
		
		User us = (User) userManager.loginUser(sessionData.getSession_id(), username, userpass, null, null, false);
		
		assertNotNull("User is unable to login", us);
		
		//mService.getLanguageById(1);
		
	}

}

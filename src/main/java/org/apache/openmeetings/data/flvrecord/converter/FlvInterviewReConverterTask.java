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
package org.apache.openmeetings.data.flvrecord.converter;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import org.apache.openmeetings.converter.FlvInterviewConverter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

public class FlvInterviewReConverterTask {
	private static final Logger log = Red5LoggerFactory.getLogger(FlvInterviewReConverterTask.class, webAppRootKey);

	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private FlvInterviewConverter flvInterviewConverter;

	public void startConversionThread(final Long flvRecordingId, final Integer leftSideLoud, final Integer rightSideLoud,
			final Integer leftSideTime, final Integer rightSideTime) {
		try {
			log.debug("[-1-]" + taskExecutor);

			taskExecutor.execute(new Runnable() {
				public void run() {
					flvInterviewConverter.startReConversion(flvRecordingId, leftSideLoud, rightSideLoud, leftSideTime, rightSideTime);
				}
			});
		} catch (Exception err) {
			log.error("[startConversionThread]", err);
		}
	}
}

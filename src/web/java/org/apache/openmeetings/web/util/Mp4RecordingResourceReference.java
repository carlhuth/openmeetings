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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.util.OmFileHelper.MP4_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.getMp4Recording;

import java.io.File;

import org.apache.openmeetings.db.entity.record.FlvRecording;

public class Mp4RecordingResourceReference extends RecordingResourceReference {
	private static final long serialVersionUID = 6181482949621024822L;

	public Mp4RecordingResourceReference() {
		super(Mp4RecordingResourceReference.class);
	}
	
	@Override
	public String getContentType() {
		return "video/mp4";
	}
	
	@Override
	String getFileName(FlvRecording r) {
		return r.getFileHash() + MP4_EXTENSION;
	}
	
	@Override
	File getFile(FlvRecording r) {
		return getMp4Recording(r.getFileHash());
	}
}

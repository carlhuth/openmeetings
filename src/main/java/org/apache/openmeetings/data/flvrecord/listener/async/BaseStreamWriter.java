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
package org.apache.openmeetings.data.flvrecord.listener.async;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.openmeetings.db.dao.record.FlvRecordingMetaDataDao;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData;
import org.apache.openmeetings.db.entity.record.FlvRecordingMetaData.Status;
import org.apache.openmeetings.util.OmFileHelper;
import org.red5.io.IStreamableFile;
import org.red5.io.IStreamableFileFactory;
import org.red5.io.IStreamableFileService;
import org.red5.io.ITagWriter;
import org.red5.io.StreamableFileFactory;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.red5.server.util.ScopeUtils;
import org.slf4j.Logger;

public abstract class BaseStreamWriter implements Runnable {
	private static final Logger log = Red5LoggerFactory.getLogger(BaseStreamWriter.class, webAppRootKey);
	protected int startTimeStamp = -1;
	protected long initialDelta = 0;

	// thread is running
	private boolean running = false;
	// thread is stopped
	private boolean stopping = false;
	// thread will be stopped as soon as the queue is empty
	private boolean dostopping = false;

	protected ITagWriter writer = null;

	protected Long metaDataId = null;

	protected Date startedSessionTimeDate = null;

	protected File file;

	protected IScope scope;

	protected boolean isScreenData = false;

	protected String streamName = "";
	protected final FlvRecordingMetaDataDao metaDataDao;
	private final BlockingQueue<CachedEvent> queue = new LinkedBlockingQueue<CachedEvent>();

	public BaseStreamWriter(String streamName, IScope scope, Long metaDataId, boolean isScreenData, FlvRecordingMetaDataDao metaDataDao) {
		startedSessionTimeDate = new Date();
		this.isScreenData = isScreenData;
		this.streamName = streamName;
		this.metaDataId = metaDataId;
		this.metaDataDao = metaDataDao;
		this.scope = scope;
		try {
			init();
		} catch (IOException ex) {
			log.error("[BaseStreamWriter] Could not start Thread", ex);
		}
		open();

		FlvRecordingMetaData metaData = metaDataDao.get(metaDataId);
		metaData.setStreamStatus(Status.STARTED);
		metaDataDao.update(metaData);
	}

	/**
	 * Initialization
	 * 
	 * @throws IOException
	 *             I/O exception
	 */
	private void init() throws IOException {
		file = new File(OmFileHelper.getStreamsSubDir(scope.getName()), streamName + ".flv");

		IStreamableFileFactory factory = (IStreamableFileFactory) ScopeUtils.getScopeService(scope, IStreamableFileFactory.class,
				StreamableFileFactory.class);

		if (!file.isFile()) {
			// Maybe the (previously existing) file has been deleted
			file.createNewFile();

		} else if (!file.canWrite()) {
			throw new IOException("The file is read-only");
		}

		IStreamableFileService service = factory.getService(file);
		IStreamableFile flv = service.getStreamableFile(file);
		writer = flv.getWriter();
	}

	private void open() {
		running = true;
		new Thread(this, "Recording " + file.getName()).start();
	}

	public void stop() {
		dostopping = true;
	}

	public void run() {
		while (!stopping) {
			try {
				CachedEvent item = queue.poll(100, TimeUnit.MICROSECONDS);
				if (item != null) {
					if (dostopping) {
						log.trace("metadatId: {} :: Recording stopped but still packets to write to file!", metaDataId);
					}

					packetReceived(item);
				} else if (dostopping) {
					stopping = true;
					closeStream();
				}
			} catch (InterruptedException e) {
				log.error("[run]", e);
			}
		}
	}

	/**
	 * Write the actual packet data to the disk and do calculate any needed additional information
	 * 
	 * @param streampacket
	 */
	public abstract void packetReceived(CachedEvent streampacket);

	protected abstract void internalCloseStream();
	/**
	 * called when the stream is finished written on the disk
	 */
	public void closeStream() {
		try {
			writer.close();
		} catch (Exception err) {
			log.error("[closeStream, close writer]", err);
		}
		internalCloseStream();
		// Write the complete Bit to the meta data, the converter task will wait for this bit!
		try {
			FlvRecordingMetaData metaData = metaDataDao.get(metaDataId);
			log.debug("Stream Status was: {} has been written for: {}", metaData.getStreamStatus(), metaDataId);
			metaData.setStreamStatus(Status.STOPPED);
			metaDataDao.update(metaData);
		} catch (Exception err) {
			log.error("[closeStream, complete Bit]", err);
		}
	}

	public void append(CachedEvent streampacket) {
		if (!running) {
			throw new IllegalStateException("Append called before the Thread was started!");
		}
		try {
			queue.put(streampacket);
		} catch (InterruptedException ignored) {
			log.error("[append]", ignored);
		}
	}

}

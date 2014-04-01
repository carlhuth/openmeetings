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
package org.apache.openmeetings.web.user.dashboard;

import static org.apache.openmeetings.db.entity.user.PrivateMessage.INBOX_FOLDER_ID;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_EDIT;
import static org.apache.openmeetings.web.util.OmUrlFragment.PROFILE_MESSAGES;

import org.apache.openmeetings.db.dao.user.PrivateMessagesDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.common.UploadableProfileImagePanel;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.SwfPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ro.fortsoft.wicket.dashboard.Widget;
import ro.fortsoft.wicket.dashboard.web.WidgetView;

public class WelcomeWidgetView extends WidgetView {
	private static final long serialVersionUID = -6257866996099503210L;

	public WelcomeWidgetView(String id, Model<Widget> model) {
		super(id, model);

		User u = getBean(UserDao.class).get(getUserId());
		add(new UploadableProfileImagePanel("img", getUserId()));
		 //FIXME this need to be aligned according to Locale
		add(new Label("firstname", Model.of(u.getFirstname())));
		add(new Label("lastname", Model.of(u.getLastname())));
		add(new Label("tz", Model.of(u.getTimeZoneId())));
		add(new AjaxLink<Void>("openUnread") {
			private static final long serialVersionUID = -1847619557485964386L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((MainPage)getPage()).updateContents(PROFILE_MESSAGES, target);
			}
		}.add(new Label("unread", Model.of("" + getBean(PrivateMessagesDao.class).count(getUserId(), INBOX_FOLDER_ID, null)))));
		add(new AjaxLink<Void>("editProfile") {
			private static final long serialVersionUID = -1847619557485964386L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				((MainPage)getPage()).updateContents(PROFILE_EDIT, target);
			}
		});
		add(new Link<Void>("netTest") {
			private static final long serialVersionUID = -9055312659797800331L;

			@Override
			public void onClick() {
				PageParameters pp = new PageParameters();
				pp.add("swf", "networktesting.swf10.swf");
				setResponsePage(SwfPage.class, pp);
			}
		});
	}
}

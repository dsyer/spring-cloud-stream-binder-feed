/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder.feed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Item;

/**
 * @author Dave Syer
 *
 */
public class FeedMessageHandler extends AbstractRssFeedView implements MessageHandler {

	private List<Message<?>> messages = new ArrayList<>();

	private String channelName;

	private Date pubDate;

	public FeedMessageHandler(String channelName) {
		this.channelName = channelName;
	}

	@Override
	public void handleMessage(Message<?> message) throws MessagingException {
		synchronized (this.messages) {
			while (this.messages.size() > 100) {
				this.messages.remove(0);
			}
		}
		this.pubDate = new Date(message.getHeaders().getTimestamp());
		this.messages.add(message);
	}

	@Override
	protected Channel newFeed() {
		Channel channel = new Channel("rss_2.0");
		channel.setLink(this.channelName);
		channel.setTitle("Events");
		channel.setDescription("Spring Application Events");
		if (this.pubDate != null) {
			channel.setPubDate(this.pubDate);
		}
		return channel;
	}

	public Message<?> getItem(String baseUrl, String id) {
		Optional<Message<?>> first = this.messages.stream()
				.filter(message -> id.equals(message.getHeaders().getId()+"")).findFirst();
		return first.isPresent() ? first.get() : null;
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String baseUrl = ServletUriComponentsBuilder.fromRequest(request)
				.replacePath("feeds").toUriString();
		return this.messages.stream().map(message -> createItem(message, baseUrl))
				.collect(Collectors.toList());
	}

	private Item createItem(Message<?> message, String baseUrl) {
		Item item = new Item();
		Guid guid = new Guid();
		guid.setValue("" + message.getHeaders().getId());
		item.setGuid(guid);
		item.setLink(baseUrl + "/" + this.channelName + "/" + guid.getValue());
		item.setTitle(message.getPayload().getClass().getName());
		item.setPubDate(new Date(message.getHeaders().getTimestamp()));
		return item;
	}

}
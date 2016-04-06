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

import javax.servlet.http.HttpServletRequest;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * @author Dave Syer
 *
 */
@Controller
public class FeedController {

	private FeedMessageHandlers handler;

	public FeedController(FeedMessageHandlers handler) {
		this.handler = handler;
	}

	@RequestMapping(value = "/feeds/{channel}", produces = "application/rss+xml")
	public ModelAndView handleFeed(@PathVariable String channel) {
		return new ModelAndView(this.handler.getHandler(channel));
	}

	@RequestMapping(value = "/feeds/{channel}/{id}")
	@ResponseBody
	public Message<?> handleItem(@PathVariable String channel, @PathVariable String id,
			HttpServletRequest request) {
		String baseUrl = ServletUriComponentsBuilder.fromRequest(request)
				.replacePath("feed").build().toUriString();
		return this.handler.getHandler(channel).getItem(baseUrl, id);
	}

}

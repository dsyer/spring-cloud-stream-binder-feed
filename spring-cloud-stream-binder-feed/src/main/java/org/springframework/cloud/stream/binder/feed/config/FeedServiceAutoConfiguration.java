/*
 * Copyright 2015-2016 the original author or authors.
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

package org.springframework.cloud.stream.binder.feed.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.binder.feed.FeedController;
import org.springframework.cloud.stream.binder.feed.FeedMessageHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Bind to services, either locally or in a cloud environment.
 *
 * @author Dave Syer
 */
@Configuration
@ConditionalOnMissingBean(Binder.class)
@Import(FeedMessageChannelBinderConfiguration.class)
@EnableConfigurationProperties(FeedProperties.class)
public class FeedServiceAutoConfiguration {

	@Bean
	FeedMessageHandlers feedMessageHandlers() {
		return new FeedMessageHandlers();
	}

	@Bean
	FeedController feedController(FeedMessageHandlers handlers) {
		return new FeedController(handlers);
	}

}

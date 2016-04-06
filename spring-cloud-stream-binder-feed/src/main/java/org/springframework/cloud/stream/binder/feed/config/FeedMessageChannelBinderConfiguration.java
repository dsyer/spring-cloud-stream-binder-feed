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

package org.springframework.cloud.stream.binder.feed.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.feed.FeedExtendedBindingProperties;
import org.springframework.cloud.stream.binder.feed.FeedMessageChannelBinder;
import org.springframework.cloud.stream.binder.feed.FeedMessageHandlers;
import org.springframework.cloud.stream.config.codec.kryo.KryoCodecAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.codec.Codec;


/**
 * Configuration class for feed message channel binder.
 *
 * @author Davie Syer
 */

@Configuration
@Import({PropertyPlaceholderAutoConfiguration.class, KryoCodecAutoConfiguration.class})
@EnableConfigurationProperties({FeedExtendedBindingProperties.class})
public class FeedMessageChannelBinderConfiguration {

	@Autowired
	private Codec codec;

	@Autowired
	private FeedExtendedBindingProperties feedExtendedBindingProperties;

	@Bean
	FeedMessageChannelBinder feedMessageChannelBinder(FeedMessageHandlers handlers) {
		FeedMessageChannelBinder binder = new FeedMessageChannelBinder(handlers);
		binder.setCodec(this.codec);
		binder.setExtendedBindingProperties(this.feedExtendedBindingProperties);
		return binder;
	}

}


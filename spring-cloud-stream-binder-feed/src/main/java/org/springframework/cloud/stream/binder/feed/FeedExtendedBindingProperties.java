/*
 * Copyright 2016 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.stream.binder.ExtendedBindingProperties;

/**
 * @author Marius Bogoevici
 */
@ConfigurationProperties("spring.cloud.stream.feed")
public class FeedExtendedBindingProperties implements ExtendedBindingProperties<FeedConsumerProperties, FeedProducerProperties> {

	private Map<String, FeedBindingProperties> bindings = new HashMap<>();

	public Map<String, FeedBindingProperties> getBindings() {
		return this.bindings;
	}

	public void setBindings(Map<String, FeedBindingProperties> bindings) {
		this.bindings = bindings;
	}

	@Override
	public FeedConsumerProperties getExtendedConsumerProperties(String channelName) {
		if (this.bindings.containsKey(channelName) && this.bindings.get(channelName).getConsumer() != null) {
			return this.bindings.get(channelName).getConsumer();
		}
		else {
			return new FeedConsumerProperties(channelName);
		}
	}

	@Override
	public FeedProducerProperties getExtendedProducerProperties(String channelName) {
		if (this.bindings.containsKey(channelName) && this.bindings.get(channelName).getProducer() != null) {
			return this.bindings.get(channelName).getProducer();
		}
		else {
			return new FeedProducerProperties(channelName);
		}
	}
}

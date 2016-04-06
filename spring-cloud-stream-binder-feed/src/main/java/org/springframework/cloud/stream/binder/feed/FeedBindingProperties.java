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

/**
 * @author Marius Bogoevici
 */
public class FeedBindingProperties {

	private FeedConsumerProperties consumer = new FeedConsumerProperties();

	private FeedProducerProperties producer = new FeedProducerProperties();

	public FeedConsumerProperties getConsumer() {
		return this.consumer;
	}

	public void setConsumer(FeedConsumerProperties consumer) {
		this.consumer = consumer;
	}

	public FeedProducerProperties getProducer() {
		return this.producer;
	}

	public void setProducer(FeedProducerProperties producer) {
		this.producer = producer;
	}
}

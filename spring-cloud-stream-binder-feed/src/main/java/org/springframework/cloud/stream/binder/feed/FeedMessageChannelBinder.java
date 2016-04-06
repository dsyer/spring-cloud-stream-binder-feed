package org.springframework.cloud.stream.binder.feed;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.springframework.cloud.stream.binder.AbstractBinder;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.DefaultBinding;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.feed.inbound.FeedEntryMessageSource;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.RestTemplate;

import com.rometools.rome.feed.synd.SyndEntry;

public class FeedMessageChannelBinder extends
		AbstractBinder<MessageChannel, ExtendedConsumerProperties<FeedConsumerProperties>, ExtendedProducerProperties<FeedProducerProperties>>
		implements
		ExtendedPropertiesBinder<MessageChannel, FeedConsumerProperties, FeedProducerProperties> {

	private FeedExtendedBindingProperties extendedBindingProperties = new FeedExtendedBindingProperties();

	private FeedMessageHandlers handlers;

	public FeedMessageChannelBinder(FeedMessageHandlers handlers) {
		this.handlers = handlers;
	}

	public void setExtendedBindingProperties(
			FeedExtendedBindingProperties extendedBindingProperties) {
		this.extendedBindingProperties = extendedBindingProperties;
	}

	@Override
	public FeedConsumerProperties getExtendedConsumerProperties(String channelName) {
		return this.extendedBindingProperties.getExtendedConsumerProperties(channelName);
	}

	@Override
	public FeedProducerProperties getExtendedProducerProperties(String channelName) {
		return this.extendedBindingProperties.getExtendedProducerProperties(channelName);
	}

	@Override
	protected Binding<MessageChannel> doBindConsumer(String name, String group,
			MessageChannel input,
			ExtendedConsumerProperties<FeedConsumerProperties> consumer) {
		SourcePollingChannelAdapter endpoint = new SourcePollingChannelAdapter();
		endpoint.setApplicationContext(getApplicationContext());
		try {
			FeedEntryMessageSource source = new FeedEntryMessageSource(
					new URL(consumer.getExtension().getUri()), "message") {
				@Override
				public Message<SyndEntry> receive() {
					try {
						return super.receive();
					}
					catch (MessagingException e) {
						return null;
					}
				}
			};
			source.setApplicationContext(getApplicationContext());
			source.afterPropertiesSet();
			endpoint.setSource(source);
		}
		catch (MalformedURLException e) {
			throw new IllegalStateException("Cannot create feed URL", e);
		}
		DirectChannel bridgeToModuleChannel = new DirectChannel();
		bridgeToModuleChannel.setBeanFactory(this.getBeanFactory());
		bridgeToModuleChannel.setBeanName(name + ".bridge");
		ReceivingHandler convertingBridge = new ReceivingHandler();
		convertingBridge.setOutputChannel(input);
		convertingBridge.setBeanName(name + ".convert.bridge");
		convertingBridge.afterPropertiesSet();
		bridgeToModuleChannel.subscribe(convertingBridge);
		endpoint.setOutputChannel(bridgeToModuleChannel);
		endpoint.setBeanFactory(getBeanFactory());
		endpoint.start();
		Binding<MessageChannel> binding = new DefaultBinding<MessageChannel>(name, group,
				bridgeToModuleChannel, endpoint);
		return binding;
	}

	@Override
	protected Binding<MessageChannel> doBindProducer(String name, MessageChannel output,
			ExtendedProducerProperties<FeedProducerProperties> producer) {
		EventDrivenConsumer consumer = new EventDrivenConsumer(
				(SubscribableChannel) output, this.handlers.getHandler(name));
		consumer.setBeanFactory(getBeanFactory());
		consumer.setBeanName("outbound." + name);
		consumer.afterPropertiesSet();
		consumer.start();
		Binding<MessageChannel> binding = new DefaultBinding<MessageChannel>(name, null,
				output, consumer);
		return binding;
	}

	private class ReceivingHandler extends AbstractReplyProducingMessageHandler {

		public ReceivingHandler() {
			super();
			this.setBeanFactory(FeedMessageChannelBinder.this.getBeanFactory());
		}

		@Override
		protected Object handleRequestMessage(Message<?> requestMessage) {
			try {
				SyndEntry entry = (SyndEntry) requestMessage.getPayload();
				String link = entry.getLink();
				@SuppressWarnings("rawtypes")
				ResponseEntity<Map> response = new RestTemplate().getForEntity(link,
						Map.class);
				@SuppressWarnings("unchecked")
				Map<String, Object> map = response.getBody();
				@SuppressWarnings("unchecked")
				Map<String, ?> headers = (Map<String, ?>) map.get("headers");
				Message<?> message = MessageBuilder.withPayload(map.get("payload"))
						.copyHeaders(headers).build();
				return deserializePayloadIfNecessary(message)
						.toMessage(getMessageBuilderFactory());
			}
			catch (Exception e) {
				return null;
			}
		}

	}
}

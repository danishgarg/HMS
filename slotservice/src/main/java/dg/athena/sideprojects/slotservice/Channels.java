package dg.athena.sideprojects.slotservice;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface Channels{

	public static final String EVENTS_OUTPUT_CHANNEL = "eventsOutputChannel";
	public static final String EVENTS_INPUT_CHANNEL = "eventsInputChannel";
	// public static final String STATE_OUTPUT_CHANNEL = "stateOutputChannel";

	@Output("eventsOutputChannel")
	MessageChannel eventsOutputChannel();

	@Input("eventsInputChannel")
	KStream<?, ?> eventsInputChannel();

	// @Output("stateOutputChannel")
	// KStream<?, ?> stateOutputChannel();
}
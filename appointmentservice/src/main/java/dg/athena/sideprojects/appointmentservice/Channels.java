package dg.athena.sideprojects.appointmentservice;

import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;

public interface Channels{

	public static final String STATE_INPUT_CHANNEL = "slotstateinput";

	@Input("slotstateinput")
	KStream<?,?> slotstateinput();
}
package dg.athena.sideprojects.slotservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import dg.athena.sideprojects.slotservice.domain.DomainEvent;
import dg.athena.sideprojects.slotservice.domain.Slot;

@Component
@EnableBinding(Channels.class)
public class Repository {

    public static final String SNAPSHOTS_FOR_SLOTS = "slots";
    
    private Channels channels;
    private QueryableStoreRegistry storeRegistry;
    private ReadOnlyKeyValueStore<String, Slot> store;


    @Autowired
    public Repository(Channels channels, QueryableStoreRegistry storeRegistry) {
        this.channels = channels;
        this.storeRegistry = storeRegistry;
    }

    private ReadOnlyKeyValueStore<String, Slot> getStore() {
        if (this.store == null) {
            this.store = this.storeRegistry.getQueryableStoreType(Repository.SNAPSHOTS_FOR_SLOTS,
                    QueryableStoreTypes.keyValueStore());
        }
        return this.store;
    }

    public void save(Slot slot) {
        List<DomainEvent> newEvents = slot.getDirtyEvents();
        newEvents.forEach(domainEvent -> channels.eventsOutputChannel().send(MessageBuilder.withPayload(domainEvent)
                .setHeader(KafkaHeaders.MESSAGE_KEY, slot.getUuid().toString()).build()));
        slot.flushEvents();
    }

    @StreamListener(Channels.EVENTS_INPUT_CHANNEL)
    public void listen(KStream<String, DomainEvent> stream) {
        Serde<DomainEvent> domainEventSerde = new JsonSerde<>(DomainEvent.class);
        Serde<Slot> slotSerde = new JsonSerde<>(Slot.class);
        stream
            .groupByKey(Serialized.with(Serdes.String(), domainEventSerde))
            .aggregate(
                    Slot::new, 
                    (s, domainEvent, slot) -> slot.handle(domainEvent),
                    Materialized.<String, Slot, KeyValueStore<Bytes, byte[]>>
                    as(Repository.SNAPSHOTS_FOR_SLOTS)
                        .withKeySerde(Serdes.String()).withValueSerde(slotSerde)
            );
    }

    public List<Slot> findAll() {
        List<Slot> slots = new ArrayList<>();
        this.getStore().all().forEachRemaining(slotKeyValue -> slots.add(slotKeyValue.value));
        return slots;
    }

    public Slot findById(String uuid) {
        return this.getStore().get(uuid);
    }
}
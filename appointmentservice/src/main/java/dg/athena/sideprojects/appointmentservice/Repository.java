package dg.athena.sideprojects.appointmentservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.kafka.streams.QueryableStoreRegistry;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(Channels.class)
public class Repository {

    public static final String SNAPSHOTS_FOR_SLOTS = "appointment-slots";

    private QueryableStoreRegistry storeRegistry;
    private ReadOnlyKeyValueStore<String, Slot> store;

    @Autowired
    public Repository(QueryableStoreRegistry storeRegistry) {
        this.storeRegistry = storeRegistry;
    }

    private ReadOnlyKeyValueStore<String, Slot> getStore() {
        if (this.store == null) {
            this.store = this.storeRegistry.getQueryableStoreType(Repository.SNAPSHOTS_FOR_SLOTS,
                    QueryableStoreTypes.keyValueStore());
        }
        return this.store;
    }

    @StreamListener
    public void listen(@Input(Channels.STATE_INPUT_CHANNEL) KStream<String, Slot> slots) {
        Serde<Slot> slotSerde = new JsonSerde<>(Slot.class);
        slots.groupByKey(Serialized.with(Serdes.String(), slotSerde))
            .reduce((agg,value)->{return value;}, 
        Materialized.<String, Slot, KeyValueStore<Bytes, byte[]>>as(Repository.SNAPSHOTS_FOR_SLOTS)
                .withKeySerde(Serdes.String()).withValueSerde(slotSerde));

        // slots.mapValues(v -> {
        //     return convertStringToSlot(v);
        // }, Materialized.<String, Slot, KeyValueStore<Bytes, byte[]>>as(Repository.SNAPSHOTS_FOR_SLOTS)
        //         .withKeySerde(Serdes.String()).withValueSerde(slotSerde));
    }

    private Slot convertStringToSlot(String slotString) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(slotString, Slot.class);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return null;
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
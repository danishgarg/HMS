package dg.athena.sideprojects.slotservice.domain;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(name="slot.created", value=SlotCreated.class),
    @JsonSubTypes.Type(name="slot.opened", value=SlotOpened.class),
    @JsonSubTypes.Type(name="slot.booked", value=SlotBooked.class),
    @JsonSubTypes.Type(name="slot.closed", value=SlotClosed.class)
}
)
public interface DomainEvent{
    UUID aggregateUUID();
    Date timestamp();
	Slot applyOn(Slot slot);
}
package dg.athena.sideprojects.slotservice.domain;

import java.util.Date;
import java.util.UUID;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlotCreated implements DomainEvent{

    private UUID uuid;
    private Date start;
    private Date end;
    private Date timestamp;
    private String providerId;
    private final String type = "slot.created";

    public SlotCreated(UUID uuid, String providerId, Date start, Date end, Date instant){
        this.uuid = uuid;
        this.timestamp = instant;
        this.providerId = providerId;
        this.start = start;
        this.end = end;
    }

	@Override
	public UUID aggregateUUID() {
		return uuid;
	}

	@Override
	public Date timestamp() {
		return timestamp;
	}

    @Override
	public Slot applyOn(Slot slot) {
		return slot.applyEvent(this);
	}
}
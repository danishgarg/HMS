package dg.athena.sideprojects.slotservice.domain;

import java.util.Date;
import java.util.UUID;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlotOpened implements DomainEvent{


    private UUID uuid;
    private Date timestamp;
    private String openedBy;
    private final String type = "slot.opened";

    public SlotOpened(UUID uuid, Date instant, String openedBy){
        this.uuid = uuid;
        this.timestamp = instant;
        this.openedBy = openedBy;
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
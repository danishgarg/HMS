package dg.athena.sideprojects.slotservice.domain;

import java.util.Date;
import java.util.UUID;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlotClosed implements DomainEvent{

    private UUID uuid;
    private Date timestamp;
    private String reason;
    private String closedBy;
    private final String type = "slot.closed";

    public SlotClosed(UUID uuid, Date instant, String reason, String closedBy){
        this.uuid = uuid;
        this.timestamp = instant;
        this.reason = reason;
        this.closedBy = closedBy;
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
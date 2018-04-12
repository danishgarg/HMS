package dg.athena.sideprojects.slotservice.domain;

import java.util.Date;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlotBooked implements DomainEvent{

    private UUID uuid;
    private Date timestamp;
    private String appointmentId;
    private final String type = "slot.booked";

    public SlotBooked(UUID uuid, Date instant, String appointmentId){
        this.uuid = uuid;
        this.timestamp = instant;
        this.appointmentId = appointmentId;
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
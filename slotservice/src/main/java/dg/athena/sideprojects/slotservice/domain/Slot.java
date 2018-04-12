package dg.athena.sideprojects.slotservice.domain;

import static javaslang.collection.List.ofAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
@JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME, 
      include = As.PROPERTY, 
      property = "type")
public class Slot implements Aggregate{

    enum SlotStatus{
        INITIALIZED,
        OPENED,
        BOOKED,
        CLOSED
    }

    private UUID uuid;
    private Date start;
    private Date end;
    private String appointmentId;
    private String reason;
    private String closedBy;
    private String providerId;
    private String openedBy;
    private SlotStatus status;

    public List<DomainEvent> getDirtyEvents() {
        return Collections.unmodifiableList(dirtyEvents);
    }

    private final List<DomainEvent> dirtyEvents = new ArrayList<>();

    public void create(String providerId, Date start, Date end){
        this.applyEvent(new SlotCreated(UUID.randomUUID(),providerId, start, end, new Date()));
    }

    public Slot applyEvent(SlotCreated slotCreated){
        this.uuid = slotCreated.aggregateUUID();
        this.providerId = slotCreated.getProviderId();
        this.start = slotCreated.getStart();
        this.end = slotCreated.getEnd();
        this.status = SlotStatus.INITIALIZED;
        this.dirtyEvents.add(slotCreated);
        return this;
    }

    public void open(String openedBy){
        if(!this.status.equals(SlotStatus.INITIALIZED)){
            throw new IllegalStateException();
        }
        this.applyEvent(new SlotOpened(this.uuid,new Date(),openedBy));
    }

    public Slot applyEvent(SlotOpened slotOpened) {
        this.openedBy = slotOpened.getOpenedBy();
        this.status = SlotStatus.OPENED;
        this.dirtyEvents.add(slotOpened);
        return this;
    }

	public void close(String closedBy, String reason){
        if(this.status.equals(SlotStatus.CLOSED)){
            throw new IllegalStateException();
        }
        this.applyEvent(new SlotClosed(this.uuid, new Date(), reason, closedBy));
    }

    public Slot applyEvent(SlotClosed slotClosed) {
        this.closedBy = slotClosed.getClosedBy();
        this.reason = slotClosed.getReason();
        this.status = SlotStatus.CLOSED;
        this.dirtyEvents.add(slotClosed);
        return this;
    }

	public void book(String appointmentId){
        if(!this.status.equals(SlotStatus.OPENED)){
            throw new IllegalStateException();
        }
        this.applyEvent(new SlotBooked(this.uuid, new Date(), appointmentId));
    }

    public Slot applyEvent(SlotBooked slotBooked) {
        this.appointmentId = slotBooked.getAppointmentId();
        this.status = SlotStatus.BOOKED;
        this.dirtyEvents.add(slotBooked);
        return this;
    }

    public void flushEvents() {
        dirtyEvents.clear();
    }

    public static Slot recreate(UUID uuid, List<DomainEvent> events) {
        return ofAll(events).foldLeft(new Slot(), Slot::handle);
    }

    public Slot handle(DomainEvent domainEvent) {
        return domainEvent.applyOn(this);
    }
}
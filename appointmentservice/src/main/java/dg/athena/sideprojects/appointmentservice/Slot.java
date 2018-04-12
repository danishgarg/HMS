package dg.athena.sideprojects.appointmentservice;

import java.util.Date;

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
public class Slot{

    enum SlotStatus{
        INITIALIZED,
        OPENED,
        BOOKED,
        CLOSED
    }

    private String uuid;
    private Date start;
    private Date end;
    private SlotStatus status;
}
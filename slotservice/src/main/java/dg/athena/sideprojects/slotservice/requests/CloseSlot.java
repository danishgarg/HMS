package dg.athena.sideprojects.slotservice.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CloseSlot{
    private String closedBy;
    private String reason;
}
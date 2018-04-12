package dg.athena.sideprojects.slotservice.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Aggregate{
    @JsonIgnore
    public List<DomainEvent> getDirtyEvents();
}
package dg.athena.sideprojects.slotservice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dg.athena.sideprojects.slotservice.domain.Slot;
import dg.athena.sideprojects.slotservice.exceptions.NotFoundException;
import dg.athena.sideprojects.slotservice.requests.BookSlot;
import dg.athena.sideprojects.slotservice.requests.CloseSlot;
import dg.athena.sideprojects.slotservice.requests.CreateSlot;
import dg.athena.sideprojects.slotservice.requests.OpenSlot;

@RestController
@RequestMapping(path = "/slots")
public class SlotController {

    @Autowired
    Repository repository;

    @GetMapping("/list")
    public List<Slot> slotList() {
        return repository.findAll();
    }

    @PostMapping("/create")
    public void createSlot(@RequestBody CreateSlot createRequest) {
        Slot slot = new Slot();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            slot.create(createRequest.getProviderId(), format.parse(createRequest.getStart()),
                    format.parse(createRequest.getEnd()));
        } catch (Exception ex) {
            System.out.println(ex);
        }
        repository.save(slot);
    }

    @GetMapping("/{id}")
    public Slot getSlotById(@PathVariable(name = "id") String id) {
        return repository.findById(id);
    }

    @PostMapping("/open/{id}")
    public void openSlot(@PathVariable("id") String id, @RequestBody OpenSlot openedBy) {
        Slot slot = repository.findById(id);
        if (slot == null) {
            throw new NotFoundException("Slot with id: {" + id + "} not found");
        }
        slot.open(openedBy.getOpenedBy());
        repository.save(slot);
    }

    @PostMapping("book/{id}")
    public void bookSlot(@PathVariable("id") String id, @RequestBody BookSlot bookRequest){
        Slot slot = repository.findById(id);
        if (slot == null) {
            throw new NotFoundException("Slot with id: {" + id + "} not found");
        }
        slot.book(bookRequest.getAppointmentId());
        repository.save(slot);
    }

    @PostMapping("close/{id}")
    public void closeSlot(@PathVariable("id") String id, @RequestBody CloseSlot closeRequest){
        Slot slot = repository.findById(id);
        if (slot == null) {
            throw new NotFoundException("Slot with id: {" + id + "} not found");
        }
        slot.close(closeRequest.getClosedBy(), closeRequest.getReason());
        repository.save(slot);
    }

}
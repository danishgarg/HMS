package dg.athena.sideprojects.appointmentservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/appointments")
public class AppointmentsController {

    @Autowired
    Repository repository;

    @GetMapping("/list")
    public List<Slot> slotList() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Slot getSlotById(@PathVariable(name = "id") String id) {
        return repository.findById(id);
    }
}
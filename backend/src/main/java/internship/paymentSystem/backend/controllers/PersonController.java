package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.DTOs.BaseObjectDto;
import internship.paymentSystem.backend.DTOs.CurrentUserDto;
import internship.paymentSystem.backend.models.Person;
import internship.paymentSystem.backend.models.PersonHistory;
import internship.paymentSystem.backend.models.User;
import internship.paymentSystem.backend.services.interfaces.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonController {

    @Autowired
    private IPersonService personService;

    @GetMapping()
    public List<Person> getPersons(){
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable Long id) {return personService.findPersonById(id).get();}

    @GetMapping("/history")
    public List<PersonHistory> getHistoryOfPersons() {
        return personService.getHistoryOfPersons();
    }

    @GetMapping("/history/{personId}")
    public List<PersonHistory> getHistoryOfPerson(@PathVariable Long personId){
        return personService.getHistoryByPersonId(personId);
    }

    @GetMapping("/user/{currentUserId}")
    public Set<Person> getPersonsOfUser(@PathVariable Long currentUserId){
        return personService.getPersonsOfUser(currentUserId);
    }

    @GetMapping("/{firstName}/{lastName}/{phoneNumber}")
    public Person getPersonByDetails(@PathVariable String firstName, @PathVariable String lastName,
                                     @PathVariable String phoneNumber){
        return personService.getPersonByDetails(firstName,lastName,phoneNumber);
    }

    @PostMapping("/{currentUserId}")
    public Person createPerson(@RequestBody Person personalInfo, @PathVariable Long currentUserId){
        Person person = personService.createPerson(personalInfo,currentUserId);
        return person;
    }

    @PutMapping()
    public ResponseEntity<Person> updatePersonalInfo(@RequestBody BaseObjectDto<Person> personDto){
        Person updatedPerson = personService.updatePerson(personDto.getCurrentUserDto().getObjectId(),
                personDto.getCurrentUserDto().getCurrentUserId(),personDto.getObject());
        return ResponseEntity.ok(updatedPerson);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approvePerson(@RequestBody CurrentUserDto currentUserDto){
        try{
            Person activePerson = personService.approvePerson(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
            return ResponseEntity.ok(activePerson);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject")
    public ResponseEntity<?> rejectPerson(@RequestBody CurrentUserDto currentUserDto){
        try{
            Person rejectedPerson = personService.rejectPerson(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
            return ResponseEntity.ok(rejectedPerson);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<Person> deletePerson(@RequestBody CurrentUserDto currentUserDto){
        Person deletedPerson = personService.deletePerson(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
        return ResponseEntity.ok(deletedPerson);
    }
}

package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.DTOs.BaseObjectDto;
import internship.paymentSystem.backend.DTOs.CurrentUserDto;
import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.models.Person;
import internship.paymentSystem.backend.models.PersonHistory;
import internship.paymentSystem.backend.models.User;
import internship.paymentSystem.backend.models.enums.StatusEnum;
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
//@CrossOrigin(origins = "http://frontend-paymentsys.s3-website-eu-west-1.amazonaws.com")
public class PersonController {

    private final MyLogger LOGGER = MyLogger.getInstance();

    @Autowired
    private IPersonService personService;

    @GetMapping()
    public List<Person> getPersons(){
        LOGGER.logInfo("HTTP Request -- Get Persons");
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable Long id) {
        LOGGER.logInfo("HTTP Request -- Get Person by Id");
        return personService.findPersonById(id).isPresent() ? personService.findPersonById(id).get() : null;
    }

    @GetMapping("/history")
    public List<PersonHistory> getHistoryOfPersons() {
        LOGGER.logInfo("HTTP Request -- Get History of Persons");
        return personService.getHistoryOfPersons();
    }

    @GetMapping("/history/{personId}")
    public List<PersonHistory> getHistoryOfPerson(@PathVariable Long personId){
        LOGGER.logInfo("HTTP Request -- Get History of Given Person");
        return personService.getHistoryByPersonId(personId);
    }

    @GetMapping("/user/{currentUserId}")
    public List<Person> getPersonsOfUser(@PathVariable Long currentUserId){
        LOGGER.logInfo("HTTP Request -- Get Persons of User");
        return personService.getPersonsOfUser(currentUserId);
    }

    @GetMapping("/history/user/{currentUserId}")
    public List<PersonHistory> getPersonsHistoryOfUser(@PathVariable Long currentUserId){
        LOGGER.logInfo("HTTP Request -- Get Persons History of User");
        return personService.getPersonsHistoryOfUser(currentUserId);
    }

    @GetMapping("/status/{filterStatus}")
    public List<Person> getPersonsByStatus(@PathVariable StatusEnum filterStatus){
        LOGGER.logInfo("HTTP Request -- Get Persons by Status");
        return personService.getPersonsByStatus(filterStatus);
    }

    @GetMapping("/{firstName}/{lastName}/{phoneNumber}")
    public Person getPersonByDetails(@PathVariable String firstName, @PathVariable String lastName,
                                     @PathVariable String phoneNumber){
        LOGGER.logInfo("HTTP Request -- Get Person by Details");
        return personService.getPersonByDetails(firstName,lastName,phoneNumber);
    }

    @PostMapping("/{currentUserId}")
    public Person createPerson(@RequestBody Person personalInfo, @PathVariable Long currentUserId){
        LOGGER.logInfo("HTTP Request -- Post Create Person");
        return personService.createPerson(personalInfo,currentUserId);
    }

    @PutMapping()
    public ResponseEntity<Person> updatePersonalInfo(@RequestBody BaseObjectDto<Person> personDto){
        LOGGER.logInfo("HTTP Request -- Put Update Person");
        Person updatedPerson = personService.updatePerson(personDto.getCurrentUserDto().getObjectId(),
                personDto.getCurrentUserDto().getCurrentUserId(),personDto.getObject());
        return ResponseEntity.ok(updatedPerson);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approvePerson(@RequestBody CurrentUserDto currentUserDto){
        try{
            Person activePerson = personService.approvePerson(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Approve Person");
            return ResponseEntity.ok(activePerson);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Approve Account Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject")
    public ResponseEntity<?> rejectPerson(@RequestBody CurrentUserDto currentUserDto){
        try{
            Person rejectedPerson = personService.rejectPerson(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
            LOGGER.logInfo("HTTP Request -- Put Reject Person");
            return ResponseEntity.ok(rejectedPerson);
        }
        catch(Exception e){
            LOGGER.logError("HTTP Request -- Put Reject Person Failed: "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/delete")
    public ResponseEntity<Person> deletePerson(@RequestBody CurrentUserDto currentUserDto){
        LOGGER.logInfo("HTTP Request -- Put Delete Person");
        Person deletedPerson = personService.deletePerson(currentUserDto.getObjectId(), currentUserDto.getCurrentUserId());
        return ResponseEntity.ok(deletedPerson);
    }
}

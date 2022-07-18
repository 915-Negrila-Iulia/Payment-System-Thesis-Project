package com.example.backend.controllers;

import com.example.backend.models.*;
import com.example.backend.models.enumerations.ObjectTypeEnum;
import com.example.backend.models.enumerations.OperationEnum;
import com.example.backend.models.enumerations.StatusEnum;
import com.example.backend.services.interfaces.IAuditService;
import com.example.backend.services.interfaces.IPersonHistoryService;
import com.example.backend.services.interfaces.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/{currentUserId}")
    public Person createPerson(@RequestBody Person personalInfo, @PathVariable Long currentUserId){
        Person person = personService.createPerson(personalInfo,currentUserId);
        return person;
    }

    /**
     * Update person
     * Check if the person exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'PersonHistory' table containing the previous state of the person
     * Update 'Audit' table
     * @param id of the person that is updated
     * @param details updates to be done on the person
     * @return status set to 'OK' and the updated person
     */
    @PutMapping("/{id}/{currentUserId}")
    public ResponseEntity<Person> updatePersonalInfo(@PathVariable Long id, @PathVariable Long currentUserId, @RequestBody Person details){
        Person updatedPerson = personService.updatePerson(id,currentUserId,details);
        return ResponseEntity.ok(updatedPerson);
    }

    @PutMapping("/approve/{id}/{currentUserId}")
    public ResponseEntity<Person> approvePerson(@PathVariable Long id, @PathVariable Long currentUserId){
        Person activePerson = personService.approvePerson(id,currentUserId);
        return ResponseEntity.ok(activePerson);
    }

    @PutMapping("/reject/{id}/{currentUserId}")
    public ResponseEntity<Person> rejectPerson(@PathVariable Long id, @PathVariable Long currentUserId){
        Person rejectedPerson = personService.rejectPerson(id,currentUserId);
        return ResponseEntity.ok(rejectedPerson);
    }

    @PutMapping("/delete/{id}/{currentUserId}")
    public ResponseEntity<Person> deletePerson(@PathVariable Long id, @PathVariable Long currentUserId){
        Person deletedPerson = personService.deletePerson(id,currentUserId);
        return ResponseEntity.ok(deletedPerson);
    }
}

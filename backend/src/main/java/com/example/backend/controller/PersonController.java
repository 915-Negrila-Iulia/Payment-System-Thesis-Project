package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.service.IPersonHistoryService;
import com.example.backend.service.IPersonService;
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

    @Autowired
    private IPersonHistoryService personHistoryService;

    @GetMapping()
    public List<Person> getPersons(){
        return personService.getAllPersons();
    }

    @GetMapping("/history")
    public List<PersonHistory> getHistoryOfPersons() {
        return personHistoryService.getHistoryOfPersons();
    }

    @PostMapping()
    public Person createUser(@RequestBody Person personalInfo){
        return personService.savePerson(personalInfo);
    }

    /**
     * Update person
     * Check if the person exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'PersonHistory' table containing the previous state of the person
     * @param id of the person that is updated
     * @param details updates to be done on the person
     * @return status set to 'OK' and the updated person
     */
    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePersonalInfo(@PathVariable Long id, @RequestBody Person details){
        Person person = personService.findPersonById(id)
                .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
        personHistoryService.savePersonHistory(person);
        person.setFirstName(details.getFirstName());
        person.setLastName(details.getLastName());
        person.setAddress(details.getAddress());
        person.setDateOfBirth(details.getDateOfBirth());
        person.setPhoneNumber(details.getPhoneNumber());
        person.setUserID(details.getUserID());
        person.setStatus(StatusEnum.APPROVE);
        Person updatedPerson = personService.savePerson(person);
        return ResponseEntity.ok(updatedPerson);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Person> approvePerson(@PathVariable Long id){
        Person person = personService.findPersonById(id)
                .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
        personHistoryService.savePersonHistory(person);
        person.setStatus(StatusEnum.ACTIVE);
        Person activePerson = personService.savePerson(person);
        return ResponseEntity.ok(activePerson);
    }
}

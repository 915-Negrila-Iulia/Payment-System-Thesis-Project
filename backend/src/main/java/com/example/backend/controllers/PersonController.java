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

    @Autowired
    private IPersonHistoryService personHistoryService;

    @Autowired
    private IAuditService auditService;

    @Autowired
    private AuthController authController;

    @GetMapping()
    public List<Person> getPersons(){
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable Long id) {return personService.findPersonById(id).get();}

    @GetMapping("/history")
    public List<PersonHistory> getHistoryOfPersons() {
        return personHistoryService.getHistoryOfPersons();
    }

    @GetMapping("/history/{personId}")
    public List<PersonHistory> getHistoryOfPerson(@PathVariable Long personId){
        return personHistoryService.getHistoryByPersonId(personId);
    }

    @PostMapping()
    public Person createPerson(@RequestBody Person personalInfo){
        personalInfo.setStatus(StatusEnum.APPROVE);
        personalInfo.setNextStatus(StatusEnum.ACTIVE);
        Person person = personService.savePerson(personalInfo);
        personHistoryService.savePersonHistory(personalInfo);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(person.getId(), ObjectTypeEnum.PERSON, OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
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
        person.setNextStatus(StatusEnum.ACTIVE);
        Person updatedPerson = personService.savePerson(person);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(person.getId(),ObjectTypeEnum.PERSON,OperationEnum.UPDATE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(updatedPerson);
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Person> approvePerson(@PathVariable Long id){
        Person person = personService.findPersonById(id)
                .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
        personHistoryService.savePersonHistory(person);
        person.setStatus(person.getNextStatus());
        Person activePerson = personService.savePerson(person);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(person.getId(),ObjectTypeEnum.PERSON,OperationEnum.APPROVE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(activePerson);
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<Person> rejectPerson(@PathVariable Long id){
        Person person = personService.findPersonById(id)
                .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
        PersonHistory lastVersion = personHistoryService.getLastVersionOfPerson(id);
        personHistoryService.savePersonHistory(person);
        if(personHistoryService.getHistoryByPersonId(id).size() <= 2){
            // person will be deleted
            person.setStatus(StatusEnum.DELETE);
            person.setNextStatus(StatusEnum.DELETE);
        }
        else{
            // person will have changes undone
            personService.undonePersonChanges(person,lastVersion);
        }
        Person rejectedPerson = personService.savePerson(person);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(person.getId(),ObjectTypeEnum.PERSON,OperationEnum.REJECT,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(rejectedPerson);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<Person> deletePerson(@PathVariable Long id){
        Person person = personService.findPersonById(id)
                .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
        personHistoryService.savePersonHistory(person);
        person.setStatus(StatusEnum.APPROVE);
        person.setNextStatus(StatusEnum.DELETE);
        Person deletedPerson = personService.savePerson(person);
        Long currentUserId = this.authController.currentUser().getId();
        Audit audit = new Audit(person.getId(),ObjectTypeEnum.PERSON,OperationEnum.DELETE,currentUserId);
        auditService.saveAudit(audit);
        return ResponseEntity.ok(deletedPerson);
    }
}

package com.example.backend.controller;

import com.example.backend.model.Person;
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

    @GetMapping()
    public List<Person> getPersons(){
        return personService.getAllPersons();
    }

    @PostMapping()
    public Person createUser(@RequestBody Person personalInfo){
        return personService.savePerson(personalInfo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePersonalInfo(@PathVariable Long id, @RequestBody Person details){
        Person person = personService.findPersonById(id)
                .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
        person.setFirstName(details.getFirstName());
        person.setLastName(details.getLastName());
        person.setAddress(details.getAddress());
        person.setDateOfBirth(details.getDateOfBirth());
        person.setStatus(details.getStatus());
        Person updatedPerson = personService.savePerson(person);
        return ResponseEntity.ok(updatedPerson);
    }
}

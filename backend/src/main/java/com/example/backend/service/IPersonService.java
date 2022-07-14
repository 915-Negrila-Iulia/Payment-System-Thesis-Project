package com.example.backend.service;

import com.example.backend.model.Person;
import com.example.backend.model.PersonHistory;

import java.util.List;
import java.util.Optional;

public interface IPersonService {
    Person savePerson(Person person);
    void deletePersonById(Long id);
    Optional<Person> findPersonById(Long id);
    List<Person> getAllPersons();
    Person undonePersonChanges(Person person, PersonHistory lastVersion);
}

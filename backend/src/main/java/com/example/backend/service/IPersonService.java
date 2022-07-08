package com.example.backend.service;

import com.example.backend.model.Person;

import java.util.List;
import java.util.Optional;

public interface IPersonService {
    Person savePerson(Person person);
    void deletePersonById(Long id);
    Optional<Person> findPersonById(Long id);
    List<Person> getAllPersons();
}

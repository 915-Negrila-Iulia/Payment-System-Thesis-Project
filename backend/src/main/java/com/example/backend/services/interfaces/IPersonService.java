package com.example.backend.services.interfaces;

import com.example.backend.models.Person;
import com.example.backend.models.PersonHistory;

import java.util.List;
import java.util.Optional;

public interface IPersonService {

    Person savePerson(Person person);

    void deletePersonById(Long id);

    Optional<Person> findPersonById(Long id);

    List<Person> getAllPersons();

    Person undonePersonChanges(Person person, PersonHistory lastVersion);

}

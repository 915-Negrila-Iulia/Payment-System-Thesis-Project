package com.example.backend.services.interfaces;

import com.example.backend.models.Person;
import com.example.backend.models.PersonHistory;
import com.example.backend.models.User;
import com.example.backend.models.UserHistory;

import java.util.List;
import java.util.Optional;

public interface IPersonService {

    Person savePerson(Person person);

    void deletePersonById(Long id);

    Optional<Person> findPersonById(Long id);

    List<Person> getAllPersons();

    Person undonePersonChanges(Person person, PersonHistory lastVersion);

    List<PersonHistory> getHistoryOfPersons();

    List<PersonHistory> getHistoryByPersonId(Long personId);

    Person createPerson(Person personalInfo, Long currentUserId);

    Person updatePerson(Long id, Long currentUserId, Person personalInfo);

    Person approvePerson(Long id, Long currentUserId);

    Person rejectPerson(Long id, Long currentUserId);

    Person deletePerson(Long id, Long currentUserId);

}

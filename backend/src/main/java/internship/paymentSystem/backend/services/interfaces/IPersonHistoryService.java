package com.example.backend.services.interfaces;

import com.example.backend.models.Person;
import com.example.backend.models.PersonHistory;

import java.util.List;

public interface IPersonHistoryService {

    PersonHistory savePersonHistory(Person person);

    List<PersonHistory> getHistoryOfPersons();

    List<PersonHistory> getHistoryByPersonId(Long id);

    PersonHistory getLastVersionOfPerson(Long personId);

}

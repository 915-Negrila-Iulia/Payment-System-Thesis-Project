package com.example.backend.service;

import com.example.backend.model.Person;
import com.example.backend.model.PersonHistory;

import java.util.List;

public interface IPersonHistoryService {
    PersonHistory savePersonHistory(Person person);
    List<PersonHistory> getHistoryOfPersons();
    List<PersonHistory> getHistoryByPersonId(Long id);
}

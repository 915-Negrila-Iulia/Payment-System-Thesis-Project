package com.example.backend.service;

import com.example.backend.model.Person;
import com.example.backend.model.PersonHistory;
import com.example.backend.model.UserHistory;

import java.util.List;

public interface IPersonHistoryService {
    PersonHistory savePersonHistory(Person person);
    List<PersonHistory> getHistoryOfPersons();
}

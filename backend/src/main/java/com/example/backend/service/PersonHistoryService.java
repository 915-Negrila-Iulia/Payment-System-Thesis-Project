package com.example.backend.service;

import com.example.backend.model.Person;
import com.example.backend.model.PersonHistory;
import com.example.backend.repository.IPersonHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonHistoryService implements IPersonHistoryService{

    @Autowired
    IPersonHistoryRepository personHistoryRepository;

    /**
     * Create a new PersonHistory object
     * Save version of a given person in the 'PersonHistory' table
     * @param person details of PersonHistory object
     * @return PersonHistory object created
     */
    @Override
    public PersonHistory savePersonHistory(Person person) {
        PersonHistory personHistory = new PersonHistory(person.getFirstName(), person.getLastName(), person.getAddress(),
                person.getDateOfBirth(), person.getPhoneNumber(), person.getUserID(), person.getStatus(), person.getId());
        return personHistoryRepository.save(personHistory);
    }

    @Override
    public List<PersonHistory> getHistoryOfPersons() {
        return personHistoryRepository.findAll();
    }
}

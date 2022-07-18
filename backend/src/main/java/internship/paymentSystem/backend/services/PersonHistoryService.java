package com.example.backend.services;

import com.example.backend.models.Person;
import com.example.backend.models.PersonHistory;
import com.example.backend.repositories.IPersonHistoryRepository;
import com.example.backend.services.interfaces.IPersonHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PersonHistoryService implements IPersonHistoryService {

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
                person.getDateOfBirth(), person.getPhoneNumber(), person.getUserID(), person.getStatus(),
                person.getNextStatus(), person.getId());
        return personHistoryRepository.save(personHistory);
    }

    @Override
    public List<PersonHistory> getHistoryByPersonId(Long personId){
        return personHistoryRepository.findAll().stream()
                .filter(personHistory -> Objects.equals(personHistory.getPersonID(), personId))
                .collect(Collectors.toList());
    }

    @Override
    public PersonHistory getLastVersionOfPerson(Long personId){
        List<PersonHistory> history = this.getHistoryByPersonId(personId);
        PersonHistory findLastVersion = history.stream().max(Comparator.comparing(PersonHistory::getTimestamp)).get();
        PersonHistory lastVersion = new PersonHistory(findLastVersion.getFirstName(), findLastVersion.getLastName(),
                findLastVersion.getAddress(), findLastVersion.getDateOfBirth(), findLastVersion.getPhoneNumber());
        return lastVersion;
    }

    @Override
    public List<PersonHistory> getHistoryOfPersons() {
        return personHistoryRepository.findAll();
    }
}

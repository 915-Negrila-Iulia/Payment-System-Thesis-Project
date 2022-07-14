package com.example.backend.service;

import com.example.backend.model.Person;
import com.example.backend.model.PersonHistory;
import com.example.backend.model.StatusEnum;
import com.example.backend.repository.IPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService implements IPersonService {

    @Autowired
    private IPersonRepository personRepository;

    @Override
    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    @Override
    public void deletePersonById(Long id) {
        personRepository.deleteById(id);
    }

    @Override
    public Optional<Person> findPersonById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @Override
    public Person undonePersonChanges(Person person, PersonHistory lastVersion){
        person.setFirstName(lastVersion.getFirstName());
        person.setLastName(lastVersion.getLastName());
        person.setAddress(lastVersion.getAddress());
        person.setPhoneNumber(lastVersion.getPhoneNumber());
        person.setDateOfBirth(lastVersion.getDateOfBirth());
        person.setStatus(StatusEnum.ACTIVE);
        person.setNextStatus(StatusEnum.ACTIVE);
        return person;
    }
}

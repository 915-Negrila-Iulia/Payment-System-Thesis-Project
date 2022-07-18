package com.example.backend.services;

import com.example.backend.models.Audit;
import com.example.backend.models.Person;
import com.example.backend.models.PersonHistory;
import com.example.backend.models.UserHistory;
import com.example.backend.models.enumerations.ObjectTypeEnum;
import com.example.backend.models.enumerations.OperationEnum;
import com.example.backend.models.enumerations.StatusEnum;
import com.example.backend.repositories.IPersonRepository;
import com.example.backend.services.interfaces.IAuditService;
import com.example.backend.services.interfaces.IPersonHistoryService;
import com.example.backend.services.interfaces.IPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PersonService implements IPersonService {

    @Autowired
    private IPersonRepository personRepository;

    @Autowired
    private IPersonHistoryService personHistoryService;

    @Autowired
    private IAuditService auditService;

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

    @Override
    public List<PersonHistory> getHistoryOfPersons() {
        return personHistoryService.getHistoryOfPersons();
    }

    @Override
    public List<PersonHistory> getHistoryByPersonId(Long personId) {
        return personHistoryService.getHistoryByPersonId(personId);
    }

    /**
     * Create person
     * Add a new record in 'PersonHistory' table containing the initial state of the person
     * Add a new record in 'Person' table
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param personalInfo details of user that is created
     * @param currentUserId id of user performing the creation
     * @return person created
     */
    @Transactional
    @Override
    public Person createPerson(Person personalInfo, Long currentUserId) {
        personalInfo.setStatus(StatusEnum.APPROVE);
        personalInfo.setNextStatus(StatusEnum.ACTIVE);
        Person person = personRepository.save(personalInfo);
        personHistoryService.savePersonHistory(personalInfo);
        Audit audit = new Audit(person.getId(), ObjectTypeEnum.PERSON, OperationEnum.CREATE,currentUserId);
        auditService.saveAudit(audit);
        return person;
    }

    /**
     * Update person
     * Check if the person exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'PersonHistory' table containing the previous state of the person
     * Change 'Status' to 'APPROVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param id of the person that is updated
     * @param currentUserId id of user performing the update
     * @param details updates to be done on the person
     * @return the updated person
     */
    @Transactional
    @Override
    public Person updatePerson(Long id, Long currentUserId, Person details) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
        personHistoryService.savePersonHistory(person);
        person.setFirstName(details.getFirstName());
        person.setLastName(details.getLastName());
        person.setAddress(details.getAddress());
        person.setDateOfBirth(details.getDateOfBirth());
        person.setPhoneNumber(details.getPhoneNumber());
        person.setUserID(details.getUserID());
        person.setStatus(StatusEnum.APPROVE);
        person.setNextStatus(StatusEnum.ACTIVE);
        Person updatedPerson = personRepository.save(person);
        Audit audit = new Audit(person.getId(),ObjectTypeEnum.PERSON,OperationEnum.UPDATE,currentUserId);
        auditService.saveAudit(audit);
        return updatedPerson;
    }

    /**
     * Approve person's changes
     * Check if the person exists by using the given id and throw an exception otherwise
     * Check if user that wants to approve changes is not the same one that made them previously
     * Add a new record in 'PersonHistory' table containing the previous state of the person
     * Change 'Status' to 'ACTIVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param id of the person that is approved
     * @param currentUserId id of user performing the approval
     * @return the approved person
     */
    @Transactional
    @Override
    public Person approvePerson(Long id, Long currentUserId) {
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.PERSON), currentUserId)) {
            Person person = personRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
            personHistoryService.savePersonHistory(person);
            person.setStatus(person.getNextStatus());
            Person activePerson = personRepository.save(person);
            Audit audit = new Audit(person.getId(), ObjectTypeEnum.PERSON, OperationEnum.APPROVE, currentUserId);
            auditService.saveAudit(audit);
            return activePerson;
        }
        return null;
    }

    /**
     * Reject person's changes
     * Check if the person exists by using the given id and throw an exception otherwise
     * Check if user that wants to reject changes is not the same one that made them previously
     * Add a new record in 'PersonHistory' table containing the previous state of the person
     * Change 'Status' to 'ACTIVE' and 'NextStatus' to 'ACTIVE'
     * Update 'Audit' table
     * @param id of the person that is rejected
     * @param currentUserId id of user performing the rejection
     * @return the rejected person
     */
    @Transactional
    @Override
    public Person rejectPerson(Long id, Long currentUserId) {
        if(!Objects.equals(auditService.getUserThatMadeUpdates(id, ObjectTypeEnum.PERSON), currentUserId)) {
            Person person = personRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
            PersonHistory lastVersion = personHistoryService.getLastVersionOfPerson(id);
            personHistoryService.savePersonHistory(person);
            if (personHistoryService.getHistoryByPersonId(id).size() <= 2) {
                // person will be deleted
                person.setStatus(StatusEnum.DELETE);
                person.setNextStatus(StatusEnum.DELETE);
            } else {
                // person will have changes undone
                this.undonePersonChanges(person, lastVersion);
            }
            Person rejectedPerson = personRepository.save(person);
            Audit audit = new Audit(person.getId(), ObjectTypeEnum.PERSON, OperationEnum.REJECT, currentUserId);
            auditService.saveAudit(audit);
            return rejectedPerson;
        }
        return null;
    }

    /**
     * Delete person
     * Person object remains stored in the database
     * Check if the person exists by using the given id
     * And throw an exception otherwise
     * Add a new record in 'PersonHistory' table containing the previous state of the person
     * Change 'Status' and 'NextStatus' to 'DELETE'
     * Update 'Audit' table
     * @param id of the person that is deleted
     * @param currentUserId id of user performing the deletion
     * @return the deleted person
     */
    @Transactional
    @Override
    public Person deletePerson(Long id, Long currentUserId) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person with id " + id + " not found"));
        personHistoryService.savePersonHistory(person);
        person.setStatus(StatusEnum.APPROVE);
        person.setNextStatus(StatusEnum.DELETE);
        Person deletedPerson = personRepository.save(person);
        Audit audit = new Audit(person.getId(),ObjectTypeEnum.PERSON,OperationEnum.DELETE,currentUserId);
        auditService.saveAudit(audit);
        return deletedPerson;
    }


}

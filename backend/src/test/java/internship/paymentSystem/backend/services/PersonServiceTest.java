package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.Person;
import internship.paymentSystem.backend.models.PersonHistory;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import internship.paymentSystem.backend.models.enums.OperationEnum;
import internship.paymentSystem.backend.models.enums.StatusEnum;
import internship.paymentSystem.backend.repositories.IPersonRepository;
import internship.paymentSystem.backend.services.interfaces.IAuditService;
import internship.paymentSystem.backend.services.interfaces.IPersonHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {
    @Mock
    private IPersonRepository personRepository;
    @Mock
    private IPersonHistoryService personHistoryService;
    @Mock
    private IAuditService auditService;
    @InjectMocks
    private PersonService personService;

    private Person person, person2;
    private PersonHistory personHistory;
    private Audit audit;

    @BeforeEach
    void setUp(){
        person = new Person();
        person.setId(1L);
        person.setStatus(StatusEnum.APPROVE);
        person.setNextStatus(StatusEnum.ACTIVE);
        person.setFirstName("Bob");
        person.setLastName("Bobbert");
        person.setAddress("Romania/Cluj-Napoca/411458/Memo10");
        person.setDateOfBirth(new Date(1997,10,10));
        person.setPhoneNumber("0745698412");
        person.setUserID(1L);

        person2 = new Person();
        person2.setId(2L);
        person2.setStatus(StatusEnum.APPROVE);
        person2.setNextStatus(StatusEnum.ACTIVE);
        person2.setFirstName("Mary");
        person2.setLastName("Poppins");
        person2.setAddress("Romania/Brasov/700180/Cerbului34");
        person2.setDateOfBirth(new Date(1990,7,9));
        person2.setPhoneNumber("0747261288");
        person2.setUserID(2L);

        personHistory = new PersonHistory(person);

        audit = new Audit(person.getId(), ObjectTypeEnum.PERSON, OperationEnum.CREATE, 1L);
    }

    @Test
    void testSavePerson(){
        when(personRepository.save(person)).thenReturn(person);
        when(personHistoryService.savePersonHistory(person)).thenReturn(personHistory);

        Person savedPerson = personService.savePerson(person);
        PersonHistory savedPersonHistory = personHistoryService.savePersonHistory(person);

        assertEquals(person.getId(), savedPerson.getId());
        assertEquals(person.getFirstName(), savedPerson.getFirstName());
        assertEquals(person.getAddress(), savedPerson.getAddress());
        assertEquals(personHistory.getId(), savedPersonHistory.getId());
        assertEquals(personHistory.getPersonID(), savedPersonHistory.getPersonID());

        verify(personRepository, times(1)).save(person);
        verify(personHistoryService, times(1)).savePersonHistory(person);
    }

    @Test
    void testDeletePerson(){
        Long id = 1L;

        personService.deletePersonById(id);

        verify(personRepository, times(1)).deleteById(id);
    }

    @Test
    void testFindPersonById(){
        Long id = 1L;

        when(personRepository.findById(id)).thenReturn(Optional.of(person));

        Optional<Person> foundPerson = personService.findPersonById(id);

        assertEquals(Optional.of(person), foundPerson);

        verify(personRepository, times(1)).findById(id);
    }

    @Test
    void testGetAllPersons(){
        List<Person> persons = new ArrayList<>();
        persons.add(person);
        persons.add(person2);

        when(personRepository.findAll()).thenReturn(persons);

        List<Person> allPersons = personService.getAllPersons();

        assertEquals(persons, allPersons);
        assertEquals(allPersons.size(), 2);

        verify(personRepository, times(1)).findAll();
    }

    @Test
    void testGetPersonsByStatus(){
        List<Person> persons = new ArrayList<>();
        persons.add(person);
        person2.setStatus(StatusEnum.ACTIVE);
        persons.add(person2);

        when(personRepository.findAll()).thenReturn(persons);

        List<Person> filteredPersons = personService.getPersonsByStatus(StatusEnum.ACTIVE);

        assertEquals(filteredPersons.size(), 1);
        assertEquals(filteredPersons.get(0).getStatus(), StatusEnum.ACTIVE);

        verify(personRepository, times(1)).findAll();
    }

    @Test
    void testUndonePersonChanges(){
        assertEquals(person.getPhoneNumber(), "0745698412");
        assertEquals(person.getAddress(), "Romania/Cluj-Napoca/411458/Memo10");

        person.setPhoneNumber("0712345678");
        person.setAddress("newAddress");

        assertEquals(person.getPhoneNumber(), "0712345678");
        assertEquals(person.getAddress(), "newAddress");

        personService.undonePersonChanges(person, personHistory);

        assertEquals(person.getPhoneNumber(), "0745698412");
        assertEquals(person.getAddress(), "Romania/Cluj-Napoca/411458/Memo10");
    }

    @Test
    void testCreatePerson(){
        person.setStatus(StatusEnum.DELETE);
        person.setNextStatus(StatusEnum.DELETE);

        when(personRepository.save(person)).thenReturn(person);
        when(personHistoryService.savePersonHistory(person)).thenReturn(personHistory);
        when(auditService.saveAudit(audit)).thenReturn(audit);

        Person createdPerson = personService.savePerson(person);
        PersonHistory createdPersonHistory = personHistoryService.savePersonHistory(person);
        Audit createdAudit = auditService.saveAudit(audit);

        assertEquals(person.getId(), createdPerson.getId());
        assertEquals(person.getFirstName(), createdPerson.getFirstName());
        assertEquals(personHistory.getId(), createdPersonHistory.getId());
        assertEquals(personHistory.getPersonID(), createdPersonHistory.getPersonID());
        assertEquals(audit.getObjectID(), createdAudit.getObjectID());
        assertEquals(audit.getObjectType(), createdAudit.getObjectType());

        verify(personRepository, times(1)).save(person);
        verify(personHistoryService, times(1)).savePersonHistory(person);
        verify(auditService, times(1)).saveAudit(audit);
    }
}

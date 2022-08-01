package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.models.Person;
import internship.paymentSystem.backend.models.PersonHistory;
import internship.paymentSystem.backend.models.enums.StatusEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IPersonService {

    Person savePerson(Person person);

    void deletePersonById(Long id);

    Optional<Person> findPersonById(Long id);

    List<Person> getAllPersons();

    List<Person> getPersonsOfUser(Long userId);

    List<Person> getPersonsByStatus(StatusEnum status);

    Person getPersonByDetails(String firstName, String lastName, String phoneNumber);

    Person undonePersonChanges(Person person, PersonHistory lastVersion);

    List<PersonHistory> getHistoryOfPersons();

    List<PersonHistory> getHistoryByPersonId(Long personId);

    List<PersonHistory> getPersonsHistoryOfUser(Long currentUserId);

    Person createPerson(Person personalInfo, Long currentUserId);

    Person updatePerson(Long id, Long currentUserId, Person personalInfo);

    Person approvePerson(Long id, Long currentUserId) throws Exception;

    Person rejectPerson(Long id, Long currentUserId) throws Exception;

    Person deletePerson(Long id, Long currentUserId);

}

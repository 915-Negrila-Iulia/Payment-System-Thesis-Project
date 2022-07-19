package internship.paymentSystem.backend.services.interfaces;

import internship.paymentSystem.backend.models.Person;
import internship.paymentSystem.backend.models.PersonHistory;

import java.util.List;

public interface IPersonHistoryService {

    PersonHistory savePersonHistory(Person person);

    List<PersonHistory> getHistoryOfPersons();

    List<PersonHistory> getHistoryByPersonId(Long id);

    PersonHistory getLastVersionOfPerson(Long personId);

}

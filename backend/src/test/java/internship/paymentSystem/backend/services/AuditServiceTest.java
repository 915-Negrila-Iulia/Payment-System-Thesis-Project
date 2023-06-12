package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.User;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import internship.paymentSystem.backend.models.enums.OperationEnum;
import internship.paymentSystem.backend.repositories.IAuditRepository;
import internship.paymentSystem.backend.services.interfaces.IAccountHistoryService;
import internship.paymentSystem.backend.services.interfaces.IPersonHistoryService;
import internship.paymentSystem.backend.services.interfaces.ITransactionHistoryService;
import internship.paymentSystem.backend.services.interfaces.IUserHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @Mock
    private IAuditRepository auditRepository;
    @Mock
    private IUserHistoryService userHistoryService;
    @Mock
    private IPersonHistoryService personHistoryService;
    @Mock
    private IAccountHistoryService accountHistoryService;
    @Mock
    private ITransactionHistoryService transactionHistoryService;
    @InjectMocks
    private AuditService auditService;

    Audit userAudit, personAudit, accountAudit;

    @BeforeEach
    public void setup(){
        userAudit = new Audit(1L, ObjectTypeEnum.USER, OperationEnum.CREATE, 1L);
        personAudit = new Audit(2L, ObjectTypeEnum.PERSON, OperationEnum.CREATE, 2L);
        accountAudit = new Audit(3L, ObjectTypeEnum.ACCOUNT, OperationEnum.CREATE, 1L);
    }

    @Test
    void testGetAudit(){
        List<Audit> audits = new ArrayList<>();
        audits.add(userAudit);
        audits.add(personAudit);
        audits.add(accountAudit);

        when(auditRepository.findAll()).thenReturn(audits);

        List<Audit> allAudit = auditService.getAudit();

        assertEquals(audits, allAudit);
        assertEquals(allAudit.size(), 3);

        verify(auditRepository, times(1)).findAll();
    }

    @Test
    void testGetAuditOfUser(){
        List<Audit> audits = new ArrayList<>();
        audits.add(userAudit);
        audits.add(personAudit);
        audits.add(accountAudit);

        when(auditRepository.findAll()).thenReturn(audits);

        List<Audit> auditOfUser = auditService.getAuditOfUser(1L);

        assertEquals(auditOfUser.size(), 2);
        assertEquals(auditOfUser.get(0).getUserID(), 1L);

        verify(auditRepository, times(1)).findAll();
    }

    @Test
    void testGetUserThatMadeUpdates(){
        List<Audit> audits = new ArrayList<>();
        audits.add(userAudit);
        audits.add(personAudit);
        audits.add(accountAudit);

        when(auditRepository.findAll()).thenReturn(audits);

        Long userId = auditService.getUserThatMadeUpdates(3L,ObjectTypeEnum.ACCOUNT);

        assertEquals(userId, 1);

        verify(auditRepository, times(1)).findAll();
    }
}

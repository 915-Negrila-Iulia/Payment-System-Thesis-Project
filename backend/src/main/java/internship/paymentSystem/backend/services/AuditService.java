package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import internship.paymentSystem.backend.repositories.IAuditRepository;
import internship.paymentSystem.backend.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AuditService implements IAuditService {

    @Autowired
    private IAuditRepository auditRepository;

    @Autowired
    private IUserHistoryService userHistoryService;

    @Autowired
    private IPersonHistoryService personHistoryService;

    @Autowired
    private IAccountHistoryService accountHistoryService;

    @Override
    public Audit saveAudit(Audit audit) {
        return auditRepository.save(audit);
    }

    @Override
    public List<Audit> getAudit() {
        return auditRepository.findAll();
    }

    @Override
    public List<Audit> getAuditOfObject(Long objectId, ObjectTypeEnum objectType){
        return auditRepository.findAll().stream()
                .filter(audit -> Objects.equals(audit.getObjectID(), objectId) &&
                        audit.getObjectType() == objectType)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUserThatMadeUpdates(Long objectId, ObjectTypeEnum objectType){
        List<Audit> auditOfObject = this.getAuditOfObject(objectId,objectType);
        return auditOfObject.stream().max(Comparator.comparing(Audit::getTimestamp))
                .map(Audit::getUserID).orElseThrow();
    }

    @Override
    public List<?> getStateOfObject(LocalDateTime timestamp, ObjectTypeEnum type){
        switch (type){
            case USER:
                return this.userHistoryService.getUserState(timestamp);
            case PERSON:
                return this.personHistoryService.getPersonState(timestamp);
            case ACCOUNT:
                return this.accountHistoryService.getAccountState(timestamp);
            default:
                return null;
        }
    }

}

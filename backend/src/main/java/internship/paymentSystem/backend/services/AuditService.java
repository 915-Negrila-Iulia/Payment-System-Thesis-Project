package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.enumerations.ObjectTypeEnum;
import internship.paymentSystem.backend.repositories.IAuditRepository;
import internship.paymentSystem.backend.services.interfaces.IAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AuditService implements IAuditService {

    @Autowired
    private IAuditRepository auditRepository;

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
}

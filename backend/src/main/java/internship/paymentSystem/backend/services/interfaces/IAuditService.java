package com.example.backend.services.interfaces;

import com.example.backend.models.Audit;
import com.example.backend.models.enumerations.ObjectTypeEnum;

import java.util.List;

public interface IAuditService {

    Audit saveAudit(Audit audit);

    List<Audit> getAudit();

    List<Audit> getAuditOfObject(Long objectId, ObjectTypeEnum objectType);

    Long getUserThatMadeUpdates(Long objectId, ObjectTypeEnum objectType);

}

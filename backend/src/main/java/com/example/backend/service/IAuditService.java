package com.example.backend.service;

import com.example.backend.model.Audit;
import com.example.backend.model.ObjectTypeEnum;
import com.example.backend.model.User;

import java.util.List;

public interface IAuditService {
    Audit saveAudit(Audit audit);
    List<Audit> getAudit();
    List<Audit> getAuditOfObject(Long objectId, ObjectTypeEnum objectType);
    Long getUserThatMadeUpdates(Long objectId, ObjectTypeEnum objectType);
}

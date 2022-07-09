package com.example.backend.service;

import com.example.backend.model.Audit;

import java.util.List;

public interface IAuditService {
    Audit saveAudit(Audit audit);
    List<Audit> getAudit();
}

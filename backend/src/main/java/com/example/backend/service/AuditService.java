package com.example.backend.service;

import com.example.backend.model.Audit;
import com.example.backend.repository.IAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService implements IAuditService{

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
}

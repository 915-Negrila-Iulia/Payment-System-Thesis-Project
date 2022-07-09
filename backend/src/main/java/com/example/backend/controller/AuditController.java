package com.example.backend.controller;

import com.example.backend.model.Audit;
import com.example.backend.service.IAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "http://localhost:4200")
public class AuditController {

    @Autowired
    private IAuditService auditService;

    @GetMapping()
    public List<Audit> getAudit(){
        return auditService.getAudit();
    }
}

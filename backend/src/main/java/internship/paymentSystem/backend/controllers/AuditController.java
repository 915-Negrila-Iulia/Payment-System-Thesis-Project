package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.enumerations.ObjectTypeEnum;
import internship.paymentSystem.backend.services.interfaces.IAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}/{type}")
    public List<Audit> getAuditOfObject(@PathVariable Long id, @PathVariable ObjectTypeEnum type){
        return auditService.getAuditOfObject(id,type);
    }
}

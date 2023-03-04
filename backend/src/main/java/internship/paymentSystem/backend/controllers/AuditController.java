package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.DTOs.ObjectStateUtilsDto;
import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import internship.paymentSystem.backend.services.interfaces.IAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "http://frontend-paymentsys.s3-website-eu-west-1.amazonaws.com")
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

    @PostMapping("/history")
    public List<?> getObjectState(@RequestBody ObjectStateUtilsDto objectUtils){
        LocalDateTime timestamp = objectUtils.getTimestamp();
        ObjectTypeEnum type = objectUtils.getType();
        return auditService.getStateOfObject(timestamp,type);
    }

}

package internship.paymentSystem.backend.controllers;

import internship.paymentSystem.backend.DTOs.ObjectStateUtilsDto;
import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.models.Audit;
import internship.paymentSystem.backend.models.enums.ObjectTypeEnum;
import internship.paymentSystem.backend.services.interfaces.IAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "http://frontend-paymentsys.s3-website-eu-west-1.amazonaws.com")
public class AuditController {

    private final MyLogger LOGGER = MyLogger.getInstance();

    @Autowired
    private IAuditService auditService;

    @GetMapping()
    public List<Audit> getAudit(){
        LOGGER.logInfo("HTTP Request -- Get Audit");
        return auditService.getAudit();
    }

    @GetMapping("/user/{id}")
    public List<Audit> getAuditOfUser(@PathVariable Long id){
        LOGGER.logInfo("HTTP Request -- Get Audit of User");
        return auditService.getAuditOfUser(id);
    }

    @GetMapping("/{id}/{type}")
    public List<Audit> getAuditOfObject(@PathVariable Long id, @PathVariable ObjectTypeEnum type){
        LOGGER.logInfo("HTTP Request -- Get Audit of Object");
        return auditService.getAuditOfObject(id,type);
    }

    @PostMapping("/history")
    public List<?> getObjectState(@RequestBody ObjectStateUtilsDto objectUtils){
        LOGGER.logInfo("HTTP Request -- Post Audit Object State");
        LocalDateTime timestamp = objectUtils.getTimestamp();
        ObjectTypeEnum type = objectUtils.getType();
        return auditService.getStateOfObject(timestamp,type);
    }

}

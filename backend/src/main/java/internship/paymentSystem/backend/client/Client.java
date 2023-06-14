package internship.paymentSystem.backend.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import internship.paymentSystem.backend.DTOs.CheckTransactionDto;
import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.services.interfaces.IFraudDetectionClassifierService;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class Client {

    private final MyLogger LOGGER = MyLogger.getInstance();

    private final String fraudCheckURL = "http://localhost:5000";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IFraudDetectionClassifierService fraudDetectionClassifierService;

    public List<Object> isFraudCheck(int timeInHours, ActionTransactionEnum type, BigDecimal amount, BigDecimal oldbalanceOrg,
                             BigDecimal newbalanceOrig, BigDecimal oldbalanceDest, BigDecimal newbalanceDest) throws JsonProcessingException {
        LOGGER.logInfo("Check Fraud");
        String classifierType = fraudDetectionClassifierService.getClassifierType();
        CheckTransactionDto checkTransaction;
        if(type.equals(ActionTransactionEnum.TRANSFER))
            checkTransaction = new CheckTransactionDto(classifierType, timeInHours, 0, amount, oldbalanceOrg, newbalanceOrig,
                oldbalanceDest, newbalanceDest);
        else
            checkTransaction = new CheckTransactionDto(classifierType, timeInHours, 1, amount, oldbalanceOrg, newbalanceOrig,
                    new BigDecimal(0), new BigDecimal(0)); // no target balances for withdrawal => use value 0

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CheckTransactionDto> request = new HttpEntity<>(checkTransaction, headers);
        ResponseEntity<String> response = restTemplate.exchange(fraudCheckURL+"/predict",
                HttpMethod.POST, request, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseNode = mapper.readTree(response.getBody());
        float fraudProbability = responseNode.get("probability").floatValue();
        String predictionResult = responseNode.get("prediction").asText();
        return Arrays.asList(fraudProbability, predictionResult);
    }

}

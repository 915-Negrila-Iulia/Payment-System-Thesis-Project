package internship.paymentSystem.backend.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import internship.paymentSystem.backend.DTOs.CheckTransactionDto;
import internship.paymentSystem.backend.config.GenerateXML;
import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enums.MLClassifierType;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;
import internship.paymentSystem.backend.repositories.IFraudDetectionClassifierRepository;
import internship.paymentSystem.backend.services.interfaces.IFraudDetectionClassifierService;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class Client {

    private final MyLogger LOGGER = MyLogger.getInstance();

    private final String bicHeader = "INTNROB0";
    private final String protocolVersion = "1";
    private final String URL = "https://ipsdemo.montran.ro/rtp/";

    private final String fraudCheckURL = "http://localhost:5000";

    private final String bicSender = "INTNROB0";

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

    public void getPositions(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MONTRAN-RTP-Channel",bicHeader);
        headers.set("X-MONTRAN-RTP-Version",protocolVersion);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(URL+"/Positions",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
    }

    public void getMessage(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MONTRAN-RTP-Channel",bicHeader);
        headers.set("X-MONTRAN-RTP-Version",protocolVersion);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(URL+"/Message",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
        System.out.println(response.getHeaders());
    }

    public String sendPaymentRequestIPS(BigDecimal amount, String idTransaction,
                                      String nameSender, String ibanSender, String bicReceiver, String nameReceiver,
                                      String ibanReceiver){
        LOGGER.logInfo("Send IPS Request");
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MONTRAN-RTP-Channel",bicHeader);
        headers.set("X-MONTRAN-RTP-Version",protocolVersion);

        String xml = GenerateXML.generate(bicSender, amount, idTransaction,
                nameSender, ibanSender, bicReceiver, nameReceiver,
                ibanReceiver);

        HttpEntity<String> entity = new HttpEntity<>(xml,headers);
        ResponseEntity<String> response = restTemplate.exchange(URL+"/Message",
                HttpMethod.POST, entity, String.class);
        return response.getHeaders().get("x-montran-rtp-reqsts").get(0);
    }

}

package internship.paymentSystem.backend.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import internship.paymentSystem.backend.DTOs.CheckTransactionDto;
import internship.paymentSystem.backend.config.GenerateXML;
import internship.paymentSystem.backend.config.MyLogger;
import internship.paymentSystem.backend.models.enums.ActionTransactionEnum;
import internship.paymentSystem.backend.models.enums.TypeTransactionEnum;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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

    public String isFraudCheck(int timeInHours, ActionTransactionEnum type, BigDecimal amount, BigDecimal oldbalanceOrg,
                               BigDecimal newbalanceOrig, BigDecimal oldbalanceDest, BigDecimal newbalanceDest) throws JsonProcessingException {
        LOGGER.logInfo("Check Fraud");
        int type_converted;
        if(type.equals(ActionTransactionEnum.TRANSFER))
            type_converted = 0;
        else
            type_converted = 1;
        CheckTransactionDto checkTransaction = new CheckTransactionDto(timeInHours, type_converted, amount, oldbalanceOrg, newbalanceOrig,
                oldbalanceDest, newbalanceDest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CheckTransactionDto> request = new HttpEntity<>(checkTransaction, headers);
        ResponseEntity<String> response = restTemplate.exchange(fraudCheckURL+"/predict",
                HttpMethod.POST, request, String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode responseNode = mapper.readTree(response.getBody());
        String predictionValue = responseNode.get("prediction").asText();
        return predictionValue;
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

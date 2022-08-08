package internship.paymentSystem.backend.client;

import internship.paymentSystem.backend.config.GenerateXML;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
public class IpsClient {

    private final String bicHeader = "INTNROB0";
    private final String protocolVersion = "1";
    private final String URL = "https://ipsdemo.montran.ro/rtp/";

    private final String bicSender = "INTNROB0";
//    private final String bicReceiver = "RBNKTTPX";

    @Autowired
    private RestTemplate restTemplate;

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
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MONTRAN-RTP-Channel",bicHeader);
        headers.set("X-MONTRAN-RTP-Version",protocolVersion);

        String xml = GenerateXML.generate(bicSender, amount, idTransaction,
                nameSender, ibanSender, bicReceiver, nameReceiver,
                ibanReceiver);

        HttpEntity<String> entity = new HttpEntity<>(xml,headers);
        ResponseEntity<String> response = restTemplate.exchange(URL+"/Message",
                HttpMethod.POST, entity, String.class);
        //System.out.println(response.getBody());
        System.out.println(response.getHeaders().get("x-montran-rtp-reqsts").get(0));
        return response.getHeaders().get("x-montran-rtp-reqsts").get(0);
    }

}

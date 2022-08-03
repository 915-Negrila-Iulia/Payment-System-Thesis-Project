package internship.paymentSystem.backend.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;



@Component
public class IpsClient {

    private final String BIC = "INTNROB0";
    private final String protocolVersion = "1";
    private final String URL = "https://ipsdemo.montran.ro/rtp/";

    @Autowired
    private RestTemplate restTemplate;

    public void getPositions(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MONTRAN-RTP-Channel",BIC);
        headers.set("X-MONTRAN-RTP-Version",protocolVersion);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(URL+"/Positions",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
    }

    public void getMessage(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MONTRAN-RTP-Channel",BIC);
        headers.set("X-MONTRAN-RTP-Version",protocolVersion);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(URL+"/Message",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
    }

    public void sendMessage(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MONTRAN-RTP-Channel",BIC);
        headers.set("X-MONTRAN-RTP-Version",protocolVersion);

        String xml = "<env:Message xmlns:env=\"urn:montran:message.01\">\n" +
                "<env:AppHdr xmlns=\"urn:iso:std:iso:20022:tech:xsd:head.001.001.01\">\n" +
                "<Fr>\n" +
                "<FIId>\n" +
                "<FinInstnId>\n" +
                "<BICFI>INTIROB0</BICFI>\n" +
                "<!-- BIC Sender -->\n" +
                "</FinInstnId>\n" +
                "</FIId>\n" +
                "</Fr>\n" +
                "<To>\n" +
                "<FIId>\n" +
                "<FinInstnId>\n" +
                "<BICFI>ZYAKIIDJ</BICFI>\n" +
                "</FinInstnId>\n" +
                "</FIId>\n" +
                "</To>\n" +
                "<BizMsgIdr>MREF124g234fad4</BizMsgIdr>\n" +
                "<!-- ID MESAJ -->\n" +
                "<MsgDefIdr>pacs.008.001.07</MsgDefIdr>\n" +
                "<BizSvc>RTP</BizSvc>\n" +
                "<CreDt>2022-08-01T10:00:01Z</CreDt>\n" +
                "<!-- Datetime curent -->\n" +
                "</env:AppHdr>\n" +
                "<env:FIToFICstmrCdtTrf xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.07\">\n" +
                "<GrpHdr xmlns:grphdr=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.07\">\n" +
                "<grphdr:MsgId>MREF124g234fad4</grphdr:MsgId>\n" +
                "<!-- ID Mesaj Plata -->\n" +
                "<grphdr:CreDtTm>2022-08-01T10:00:01+03:00</grphdr:CreDtTm>\n" +
                "<!-- Datetime curent -->\n" +
                "<grphdr:NbOfTxs>1</grphdr:NbOfTxs>\n" +
                "<grphdr:TtlIntrBkSttlmAmt Ccy=\"IDR\">1.12</grphdr:TtlIntrBkSttlmAmt>\n" +
                "<!-- Amount -->\n" +
                "<grphdr:IntrBkSttlmDt>2022-08-01</grphdr:IntrBkSttlmDt>\n" +
                "<!--  DAta curenta  -->\n" +
                "<grphdr:SttlmInf>\n" +
                "<grphdr:SttlmMtd>CLRG</grphdr:SttlmMtd>\n" +
                "<grphdr:ClrSys>\n" +
                "<grphdr:Prtry>SENT</grphdr:Prtry>\n" +
                "</grphdr:ClrSys>\n" +
                "</grphdr:SttlmInf>\n" +
                "<grphdr:PmtTpInf>\n" +
                "<grphdr:SvcLvl>\n" +
                "<grphdr:Prtry>SENT</grphdr:Prtry>\n" +
                "</grphdr:SvcLvl>\n" +
                "<grphdr:LclInstrm>\n" +
                "<grphdr:Cd>INST</grphdr:Cd>\n" +
                "</grphdr:LclInstrm>\n" +
                "</grphdr:PmtTpInf>\n" +
                "<grphdr:InstgAgt>\n" +
                "<grphdr:FinInstnId>\n" +
                "<grphdr:BICFI>INTIROB0</grphdr:BICFI>\n" +
                "<!-- Bic sender -->\n" +
                "</grphdr:FinInstnId>\n" +
                "</grphdr:InstgAgt>\n" +
                "</GrpHdr>\n" +
                "<CdtTrfTxInf xmlns:crdt=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.07\">\n" +
                "<crdt:PmtId>\n" +
                "<crdt:InstrId>PREF124g234fad4</crdt:InstrId>\n" +
                "<!--  ID plata -->\n" +
                "<crdt:EndToEndId>PREF124g234fad4</crdt:EndToEndId>\n" +
                "<!--  ID -->\n" +
                "<crdt:TxId>PREF124g234fad4</crdt:TxId>\n" +
                "<!-- ID -->\n" +
                "</crdt:PmtId>\n" +
                "<crdt:IntrBkSttlmAmt Ccy=\"IDR\">1.12</crdt:IntrBkSttlmAmt>\n" +
                "<!--  Amount -->\n" +
                "<crdt:AccptncDtTm>2022-08-01T10:00:01+03:00</crdt:AccptncDtTm>\n" +
                "<!-- Datetime curent -->\n" +
                "<crdt:ChrgBr>SLEV</crdt:ChrgBr>\n" +
                "<crdt:Dbtr>\n" +
                "<crdt:Nm>ASDAA</crdt:Nm>\n" +
                "<!--  Nume sender  -->\n" +
                "<crdt:Id>\n" +
                "<crdt:OrgId>\n" +
                "<crdt:AnyBIC>INTIROB0</crdt:AnyBIC>\n" +
                "<!-- BIC sender -->\n" +
                "</crdt:OrgId>\n" +
                "</crdt:Id>\n" +
                "</crdt:Dbtr>\n" +
                "<crdt:DbtrAcct>\n" +
                "<crdt:Id>\n" +
                "<crdt:IBAN>RO04812719847121assaf</crdt:IBAN>\n" +
                "<!--  IBAN Sender -->\n" +
                "</crdt:Id>\n" +
                "</crdt:DbtrAcct>\n" +
                "<crdt:DbtrAgt>\n" +
                "<crdt:FinInstnId>\n" +
                "<crdt:BICFI>INTIROB0</crdt:BICFI>\n" +
                "<!-- BIC Sender -->\n" +
                "</crdt:FinInstnId>\n" +
                "</crdt:DbtrAgt>\n" +
                "<crdt:CdtrAgt>\n" +
                "<crdt:FinInstnId>\n" +
                "<crdt:BICFI>RBNKTTPX</crdt:BICFI>\n" +
                "<!--  BIC receiver -->\n" +
                "</crdt:FinInstnId>\n" +
                "</crdt:CdtrAgt>\n" +
                "<crdt:Cdtr>\n" +
                "<crdt:Nm>dfhds</crdt:Nm>\n" +
                "<!--  Nume receiver -->\n" +
                "<crdt:Id>\n" +
                "<crdt:OrgId>\n" +
                "<crdt:AnyBIC>RBNKTTPX</crdt:AnyBIC>\n" +
                "<!--  BIC receiver  -->\n" +
                "</crdt:OrgId>\n" +
                "</crdt:Id>\n" +
                "</crdt:Cdtr>\n" +
                "<crdt:CdtrAcct>\n" +
                "<crdt:Id>\n" +
                "<crdt:IBAN>RO05812719847121assaf</crdt:IBAN>\n" +
                "<!--  IBAN receiver -->\n" +
                "</crdt:Id>\n" +
                "</crdt:CdtrAcct>\n" +
                "<crdt:RmtInf> </crdt:RmtInf>\n" +
                "</CdtTrfTxInf>\n" +
                "</env:FIToFICstmrCdtTrf>\n" +
                "</env:Message>";

        HttpEntity<String> entity = new HttpEntity<>(xml,headers);
        ResponseEntity<String> response = restTemplate.exchange(URL+"/Message",
                HttpMethod.POST, entity, String.class);
        System.out.println(response.getBody());
    }

}

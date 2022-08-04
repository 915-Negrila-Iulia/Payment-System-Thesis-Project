package internship.paymentSystem.backend.config;

import com.prowidesoftware.swift.model.MxSwiftMessage;
import com.prowidesoftware.swift.model.mx.*;
import com.prowidesoftware.swift.model.mx.dic.*;
import com.prowidesoftware.swift.model.mx.sys.dic.Message;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

public class GenerateXML {

    public static String generate(String bicSender, String idMessage, BigDecimal amount, String idTransaction,
                                String nameSender, String ibanSender, String bicReceiver, String nameReceiver,
                                String ibanReceiver) {

        LocalDate date = LocalDate.now();

        XMLGregorianCalendar datetime = getXMLGregorianCalendarNow();

        idMessage = UUID.randomUUID().toString().replaceAll("-","");
        idTransaction = UUID.randomUUID().toString().replaceAll("-","");

        String xml = "<env:Message xmlns:env=\"urn:montran:message.01\">\n" +
                "<env:AppHdr xmlns=\"urn:iso:std:iso:20022:tech:xsd:head.001.001.01\">\n" +
                "<Fr>\n" +
                "<FIId>\n" +
                "<FinInstnId>\n" +
                "<BICFI>"+bicSender+"</BICFI>\n" +
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
                "<BizMsgIdr>"+idMessage+"</BizMsgIdr>\n" +
                "<!-- ID MESAJ -->\n" +
                "<MsgDefIdr>pacs.008.001.07</MsgDefIdr>\n" +
                "<BizSvc>RTP</BizSvc>\n" +
                "<CreDt>"+OffsetDateTime.now(ZoneOffset.UTC)+"</CreDt>\n" +
                "<!-- Datetime curent -->\n" +
                "</env:AppHdr>\n" +
                "<env:FIToFICstmrCdtTrf xmlns=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.07\">\n" +
                "<GrpHdr xmlns:grphdr=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.07\">\n" +
                "<grphdr:MsgId>"+idMessage+"</grphdr:MsgId>\n" +
                "<!-- ID Mesaj Plata -->\n" +
                "<grphdr:CreDtTm>"+datetime+"</grphdr:CreDtTm>\n" +
                "<!-- Datetime curent -->\n" +
                "<grphdr:NbOfTxs>1</grphdr:NbOfTxs>\n" +
                "<grphdr:TtlIntrBkSttlmAmt Ccy=\"IDR\">"+amount+"</grphdr:TtlIntrBkSttlmAmt>\n" +
                "<!-- Amount -->\n" +
                "<grphdr:IntrBkSttlmDt>"+date+"</grphdr:IntrBkSttlmDt>\n" +
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
                "<grphdr:BICFI>"+bicSender+"</grphdr:BICFI>\n" +
                "<!-- Bic sender -->\n" +
                "</grphdr:FinInstnId>\n" +
                "</grphdr:InstgAgt>\n" +
                "</GrpHdr>\n" +
                "<CdtTrfTxInf xmlns:crdt=\"urn:iso:std:iso:20022:tech:xsd:pacs.008.001.07\">\n" +
                "<crdt:PmtId>\n" +
                "<crdt:InstrId>"+idTransaction+"</crdt:InstrId>\n" +
                "<!--  ID plata -->\n" +
                "<crdt:EndToEndId>"+idTransaction+"</crdt:EndToEndId>\n" +
                "<!--  ID -->\n" +
                "<crdt:TxId>"+idTransaction+"</crdt:TxId>\n" +
                "<!-- ID -->\n" +
                "</crdt:PmtId>\n" +
                "<crdt:IntrBkSttlmAmt Ccy=\"IDR\">"+amount+"</crdt:IntrBkSttlmAmt>\n" +
                "<!--  Amount -->\n" +
                "<crdt:AccptncDtTm>"+datetime+"</crdt:AccptncDtTm>\n" +
                "<!-- Datetime curent -->\n" +
                "<crdt:ChrgBr>SLEV</crdt:ChrgBr>\n" +
                "<crdt:Dbtr>\n" +
                "<crdt:Nm>"+nameSender+"</crdt:Nm>\n" +
                "<!--  Nume sender  -->\n" +
                "<crdt:Id>\n" +
                "<crdt:OrgId>\n" +
                "<crdt:AnyBIC>"+bicSender+"</crdt:AnyBIC>\n" +
                "<!-- BIC sender -->\n" +
                "</crdt:OrgId>\n" +
                "</crdt:Id>\n" +
                "</crdt:Dbtr>\n" +
                "<crdt:DbtrAcct>\n" +
                "<crdt:Id>\n" +
                "<crdt:IBAN>"+ibanSender+"</crdt:IBAN>\n" +
                "<!--  IBAN Sender -->\n" +
                "</crdt:Id>\n" +
                "</crdt:DbtrAcct>\n" +
                "<crdt:DbtrAgt>\n" +
                "<crdt:FinInstnId>\n" +
                "<crdt:BICFI>"+bicSender+"</crdt:BICFI>\n" +
                "<!-- BIC Sender -->\n" +
                "</crdt:FinInstnId>\n" +
                "</crdt:DbtrAgt>\n" +
                "<crdt:CdtrAgt>\n" +
                "<crdt:FinInstnId>\n" +
                "<crdt:BICFI>"+bicReceiver+"</crdt:BICFI>\n" +
                "<!--  BIC receiver -->\n" +
                "</crdt:FinInstnId>\n" +
                "</crdt:CdtrAgt>\n" +
                "<crdt:Cdtr>\n" +
                "<crdt:Nm>"+nameReceiver+"</crdt:Nm>\n" +
                "<!--  Nume receiver -->\n" +
                "<crdt:Id>\n" +
                "<crdt:OrgId>\n" +
                "<crdt:AnyBIC>"+bicReceiver+"</crdt:AnyBIC>\n" +
                "<!--  BIC receiver  -->\n" +
                "</crdt:OrgId>\n" +
                "</crdt:Id>\n" +
                "</crdt:Cdtr>\n" +
                "<crdt:CdtrAcct>\n" +
                "<crdt:Id>\n" +
                "<crdt:IBAN>"+ibanReceiver+"</crdt:IBAN>\n" +
                "<!--  IBAN receiver -->\n" +
                "</crdt:Id>\n" +
                "</crdt:CdtrAcct>\n" +
                "<crdt:RmtInf> </crdt:RmtInf>\n" +
                "</CdtTrfTxInf>\n" +
                "</env:FIToFICstmrCdtTrf>\n" +
                "</env:Message>";

        System.out.println(xml);

        return xml;
    }

    public static void generate2() throws DatatypeConfigurationException {
        MxPacs00800107 mx = new MxPacs00800107();

        /*
         * Initialize main message content main objects
         */
        mx.setFIToFICstmrCdtTrf(new FIToFICustomerCreditTransferV07().setGrpHdr(new GroupHeader70()));

        /*
         * General Information
         */
        mx.getFIToFICstmrCdtTrf().getGrpHdr().setMsgId("MREF124g234fad4"); /* ID transaction message */
        mx.getFIToFICstmrCdtTrf().getGrpHdr().setCreDtTm(getXMLGregorianCalendarNow()); /* current datetime */
        mx.getFIToFICstmrCdtTrf().getGrpHdr().setNbOfTxs("1");
        ActiveCurrencyAndAmount currencyAndAmount = new ActiveCurrencyAndAmount();
        currencyAndAmount.setCcy("IDR");
        currencyAndAmount.setValue(BigDecimal.valueOf(1.12));
        mx.getFIToFICstmrCdtTrf().getGrpHdr().setTtlIntrBkSttlmAmt(currencyAndAmount); /* Amount */
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        mx.getFIToFICstmrCdtTrf().getGrpHdr().setIntrBkSttlmDt(date); /* current date */

        /*
         * Settlement Information
         */
        mx.getFIToFICstmrCdtTrf().getGrpHdr().setSttlmInf(new SettlementInstruction4());
        mx.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().setSttlmMtd(SettlementMethod1Code.CLRG);
        mx.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().setClrSys(new ClearingSystemIdentification3Choice());
        mx.getFIToFICstmrCdtTrf().getGrpHdr().getSttlmInf().getClrSys().setPrtry("SENT");

        /*
         * Payment Type Information
         */
        mx.getFIToFICstmrCdtTrf().getGrpHdr().setPmtTpInf(new PaymentTypeInformation21());
        mx.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().setSvcLvl(
                (new ServiceLevel8Choice()).setPrtry("SENT"));
        mx.getFIToFICstmrCdtTrf().getGrpHdr().getPmtTpInf().setLclInstrm(
                (new LocalInstrument2Choice()).setCd("INST"));

        /*
         * Instructing Agent
         */
        mx.getFIToFICstmrCdtTrf().getGrpHdr().setInstgAgt(
                (new BranchAndFinancialInstitutionIdentification5()).setFinInstnId(
                        (new FinancialInstitutionIdentification8()).setBICFI("INTIROB0"))); /* BIC sender */

        /*
         * Payment Transaction Information
         */
        CreditTransferTransaction30 cti = new CreditTransferTransaction30();

        /*
         * Transaction Identification
         */
        cti.setPmtId(new PaymentIdentification3());
        cti.getPmtId().setInstrId("PREF124g234fad4"); /* ID transaction */
        cti.getPmtId().setEndToEndId("PREF124g234fad4"); /* ID transaction */
        cti.getPmtId().setTxId("PREF124g234fad4"); /* ID transaction */

        /*
         * Transaction Amount
         */
        ActiveCurrencyAndAmount amount = new ActiveCurrencyAndAmount();
        amount.setCcy("IDR");
        amount.setValue(BigDecimal.valueOf(1.12));
        cti.setIntrBkSttlmAmt(amount); /* Amount */

        /*
         * Transaction Value Date
         */
        cti.setAccptncDtTm(getXMLGregorianCalendarNow()); /* current datetime */

        /*
         * Transaction Charges
         */
        cti.setChrgBr(ChargeBearerType1Code.SLEV);

        /*
         * Orderer Name
         */
        cti.setDbtr(new PartyIdentification125());
        cti.getDbtr().setNm("ASDAA"); /* Name sender */

        /*
         * Orderer Account
         */
        cti.getDbtr().setId(new Party34Choice());
        cti.getDbtr().getId().setOrgId(new OrganisationIdentification8());
        cti.getDbtr().getId().getOrgId().setAnyBIC("INTIROB0"); /* BIC sender */
        cti.setDbtrAcct(new CashAccount24());
        cti.getDbtrAcct().setId(new AccountIdentification4Choice());
        cti.getDbtrAcct().getId().setIBAN("RO04812719847121assaf"); /* iban sender */

        /*
         * Order Financial Institution
         */
        cti.setDbtrAgt(new BranchAndFinancialInstitutionIdentification5());
        cti.getDbtrAgt().setFinInstnId(new FinancialInstitutionIdentification8());
        cti.getDbtrAgt().getFinInstnId().setBICFI("INTIROB0"); /* BIC sender */

        /*
         * Beneficiary Institution
         */

        cti.setCdtrAgt(new BranchAndFinancialInstitutionIdentification5());
        cti.getCdtrAgt().setFinInstnId(new FinancialInstitutionIdentification8());
        cti.getCdtrAgt().getFinInstnId().setBICFI("RBNKTTPX"); /* BIC receiver */

        /*
         * Beneficiary Name
         */
        cti.setCdtr(new PartyIdentification125());
        cti.getCdtr().setNm("dfhds"); /* Name receiver */
        cti.getCdtr().setId(new Party34Choice());
        cti.getCdtr().getId().setOrgId(new OrganisationIdentification8());
        cti.getCdtr().getId().getOrgId().setAnyBIC("RBNKTTPX"); /* BIC receiver */

        /*
         * Beneficiary Account
         */
        cti.setCdtrAcct(new CashAccount24());
        cti.getCdtrAcct().setId(new AccountIdentification4Choice());
        cti.getCdtrAcct().getId().setIBAN("RO05812719847121assaf"); /* IBAN receiver */

        cti.setRmtInf(new RemittanceInformation15());

        mx.getFIToFICstmrCdtTrf().addCdtTrfTxInf(cti);

        /*
         * Create and add a message header
         */
        BusinessAppHdrV01 header = AppHdrFactory.createBusinessAppHdrV01("INTIROB0", "ZYAKIIDJ", "MREF124g234fad4", mx.getMxId());
        header.setMsgDefIdr("pacs.008.001.07");
        header.setBizSvc("RTP");
        header.setCreDt(getXMLGregorianCalendarNow());
        mx.setAppHdr(header);

        /*
         * Print the generated message in its XML format
         */
        System.out.println(mx.message());
    }

    public static XMLGregorianCalendar getXMLGregorianCalendarNow() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatatypeFactory datatypeFactory = null;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        return now;
    }
}

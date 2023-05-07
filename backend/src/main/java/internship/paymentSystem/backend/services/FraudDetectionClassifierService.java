package internship.paymentSystem.backend.services;

import internship.paymentSystem.backend.models.FraudDetectionClassifier;
import internship.paymentSystem.backend.models.enums.MLClassifierType;
import internship.paymentSystem.backend.repositories.IFraudDetectionClassifierRepository;
import internship.paymentSystem.backend.services.interfaces.IFraudDetectionClassifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FraudDetectionClassifierService implements IFraudDetectionClassifierService {

    @Autowired
    private IFraudDetectionClassifierRepository classifierRepository;

    public void setClassifierType(String classifierType){
        FraudDetectionClassifier classifier = classifierRepository.findById(0L)
                .orElseThrow(() -> new RuntimeException("Classifier not found"));
        switch (classifierType) {
            case "FAST":
                classifier.setClassifierType(MLClassifierType.FAST);
                break;
            case "RECALL":
                classifier.setClassifierType(MLClassifierType.RECALL);
                break;
            default:
                classifier.setClassifierType(MLClassifierType.OVERALL);
                break;
        }
        classifierRepository.save(classifier);
    }

    public String getClassifierType() {
        FraudDetectionClassifier classifier = classifierRepository.findById(0L)
                .orElseThrow(() -> new RuntimeException("Classifier not found"));
        return classifier.getClassifierType().toString();
    }

}

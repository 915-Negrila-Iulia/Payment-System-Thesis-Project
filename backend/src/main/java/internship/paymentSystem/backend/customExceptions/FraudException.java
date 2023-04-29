package internship.paymentSystem.backend.customExceptions;

public class FraudException extends Exception{
    private final float fraudProbability;

    public FraudException(String message, float fraudProbability){
        super(message);
        this.fraudProbability = fraudProbability;
    }

    public float getFraudProbability(){
        return fraudProbability;
    }
}

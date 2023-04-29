import pickle

#best_model = pickle.load(open('app/model/RFmodel.pkl', "rb"))
best_model = pickle.load(open('app/model/XGBmodel.pkl', "rb"))

def predict_transaction(data):
    """
    Predicts fraud based on the input data using the loaded ML model
    :param data: transaction data given as input
    :return: predicted class label (Fraud or notFraud)
    """
    fraud_probability = best_model.predict_proba(data)[0][1]
    prediction_result = "Fraud" if (fraud_probability >= 0.5) else "notFraud" # 0.5 is the default decision threshold
    return fraud_probability, prediction_result

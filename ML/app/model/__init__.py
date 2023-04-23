import pickle

#best_model = pickle.load(open('app/model/RFmodel.pkl', "rb"))
best_model = pickle.load(open('app/model/XGBmodel.pkl', "rb"))

def predict_transaction(data):
    """
    Predicts fraud based on the input data using the loaded ML model
    :param data: transaction data given as input
    :return: predicted class label (Fraud or notFraud)
    """
    prediction = best_model.predict(data)[0]  # 0 = notFraud, 1 = Fraud
    return "Fraud" if (prediction == 1) else "notFraud"

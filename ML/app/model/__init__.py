import pickle

import pandas as pd
from matplotlib import pyplot as plt

best_model = pickle.load(open("app/model/best_model.pkl", "rb"))
fast_model = pickle.load(open("app/model/fast_model.pkl", "rb"))
recall_model = pickle.load(open("app/model/recall_model.pkl", "rb"))


def predict_transaction(classifier_type,data):
    """
    Predicts fraud based on the input data using the requested classifier loaded from pkl file
    :param data: transaction data given as input
    :return: predicted class label (Fraud or notFraud)
    """
    if classifier_type == "FAST":
        fraud_probability = fast_model.predict_proba(data)[0][1]
        print("FAST CLASSIFIER")
    elif classifier_type == "RECALL":
        fraud_probability = recall_model.predict_proba(data)[0][1]
        print("RECALL CLASSIFIER")
    else: # OVERALL
        fraud_probability = best_model.predict_proba(data)[0][1]
        print("OVERALL CLASSIFIER")
    prediction_result = "Fraud" if (fraud_probability >= 0.5) else "notFraud"  # 0.5 is the default decision threshold
    return fraud_probability, prediction_result

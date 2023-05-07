import numpy as np
from flask import Flask, jsonify, request
from app.model import predict_transaction

app = Flask(__name__)


@app.route('/predict', methods=["POST"])
def predict():
    """
    Take HTTP Post request with JSON object containing transaction data
    Convert data to numpy array and pass it to a function for prediction
    :return: result of the prediction as a JSON response
    """
    input_json = request.get_json(force=True)
    print(input_json)
    classifier_type = input_json["classifierType"]
    float_features = [float(x) for x in input_json.values() if x != classifier_type]
    features = [np.array(float_features)]
    print(classifier_type," and ",features)
    fraud_probability, prediction_result = predict_transaction(classifier_type,features)
    resp = {'probability': round(float(fraud_probability), 2), # limit number of decimals to 2
            'prediction': prediction_result}

    return jsonify(resp)


if __name__ == '__main__':
    app.run()

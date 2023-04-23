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
    float_features = [float(x) for x in input_json.values()]
    features = [np.array(float_features)]
    print(features)
    prediction = predict_transaction(features)
    resp = {'prediction': prediction}
    return jsonify(resp)


if __name__ == '__main__':
    app.run()

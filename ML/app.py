import pickle
import numpy as np
from flask import Flask, jsonify, request

app = Flask(__name__)
#RFmodel = pickle.load(open('RandomForestClassifier/model.pkl', "rb"))
RFmodel = pickle.load(open('RFmodel.pkl', "rb"))
@app.route('/')
def hello_world():  # put application's code here
    return 'Hello World!'

@app.route('/predict', methods=["POST"])
def predict():
    input_json = request.get_json(force=True)
    float_features = [float(x) for x in input_json.values()]
    features = [np.array(float_features)]
    prediction = RFmodel.predict(features)[0] # 0=notFraud 1=Fraud
    result = "Fraud" if (prediction == 1) else "notFraud"
    resp = {'prediction': result}
    return jsonify(resp)

if __name__ == '__main__':
    app.run()

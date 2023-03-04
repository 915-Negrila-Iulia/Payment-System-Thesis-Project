import pandas as pd
import numpy as np
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
import pickle


# Import data
class Data:
    def __init__(self, filename='transactions.csv'):
        # Load the csv file
        self.df = pd.read_csv(filename)

    def clean(self):
        # Rename original column headers to be more clear
        # self.df.rename(columns={'nameOrig':'ibanSource', 'oldbalanceOrg':'oldBalanceSource', 'newbalanceOrig':'newBalanceSource',
        #                     'nameDest':'ibanTarget', 'oldbalanceDest':'oldBalanceTarget', 'newbalanceDest':'newBalanceTarget'})

        # Setting to display all columns
        pd.set_option('display.max_columns', None)

        # Check if there are any missing values
        print(self.df.isnull().values.any())

        # Remove insignificant 'isFlaggedFraud' column
        # axis=1 -> that we remove columns, not rows
        # inplace=True -> remove from original object
        self.df = self.df.loc[(self.df['type'] == 'TRANSFER') | (self.df['type'] == 'CASH_OUT')]
        self.df.drop(['type', 'nameOrig', 'nameDest', 'isFlaggedFraud'], inplace=True, axis=1)

        # drop some rows
        #self.df.drop(self.df.index[700000:2770409], inplace=True)

        # Display first 5 rows
        print(self.df.head(5))

    def createModel(self):
        # prediction
        X = self.df[["step", "amount", "oldbalanceOrg", "newbalanceOrig", "oldbalanceDest", "newbalanceDest"]]
        print("X:\n", X)
        Y = self.df["isFraud"]

        # Split the dataset into train and test
        X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.3, random_state=50)

        # Feature scaling
        sc = StandardScaler()
        X_train = sc.fit_transform(X_train)
        X_test = sc.transform(X_test)

        # Instantiate the model
        classifier = RandomForestClassifier()

        # Fit the model
        # .values will give the values in a numpy array
        # .ravel will flatten the array
        classifier.fit(X_train, y_train.values.ravel())

        # Make pickle file of our model
        pickle.dump(classifier, open("RFmodel.pkl", "wb"))


data = Data()
data.clean()
data.createModel()

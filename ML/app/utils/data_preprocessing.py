import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split


class DataPreprocessing:

    def __init__(self, filename='../data/transactions.csv'):
        self.df = pd.read_csv(filename)  # Load the csv file
        self.rename_headers()
        self.clean_data()

    def rename_headers(self):
        """
        Rename original header columns to be more clear
        Also remove the insignificant 'isFlaggedFraud' column
        :return: -
        """
        self.df.drop(['isFlaggedFraud'], inplace=True, axis=1)
        self.df = self.df.rename(columns={'oldbalanceOrg': 'oldBalanceSender', 'newbalanceOrig': 'newBalanceSender', \
                                          'oldbalanceDest': 'oldBalanceReceiver',
                                          'newbalanceDest': 'newBalanceReceiver'})

    def clean_data(self):
        """
        Modifications done on the original dataset:
        1.  Based on EDA only 'TRANSFER' and 'CASH_OUT' transactions can be fraudulent
            So remove the transactions that have other types
        2.  Eliminate irrelevant columns
        3.  Convert to binary the 'type' column ('TRANSFER' = 0 and 'CASH_OUT' = 1)
        4.  Based on 'step' column create new feature 'timeInHours' = step%24
        :return: -
        """
        self.df = self.df[(self.df.type == 'TRANSFER') | (self.df.type == 'CASH_OUT')]

        self.df.drop(['nameOrig', 'nameDest'], inplace=True, axis=1)

        self.df.loc[self.df.type == 'TRANSFER', 'type'] = 0
        self.df.loc[self.df.type == 'CASH_OUT', 'type'] = 1
        self.df['type'] = pd.to_numeric(self.df['type'])

        self.df['timeInHours'] = np.nan
        self.df.timeInHours = self.df.step % 24
        self.df.drop(['step'], inplace=True, axis=1)

    def partition_data(self, dataset):
        """
        Split the dataset into input features (X) and target class (y)
        :param dataset: dataset to be split
        :return: X and y after the split
        """
        x_columns = ['type', 'amount', 'oldBalanceSender', 'newBalanceSender', 'oldBalanceReceiver',
                     'newBalanceReceiver', 'timeInHours']
        X = dataset.loc[:, x_columns].values
        y = dataset['isFraud'].values
        return X, y

    def split_train_test_data(self, test_size=0.2, random_state=19):
        """
        Randomly split the dataset into a training set and a testing set
        :param test_size: proportion of the dataset to be used as test set, 20% by default
        :param random_state: fixed number ensuring that the same random number are generated for every call
        :return: resulting sets
        """
        features, target = self.partition_data(self.df)
        X_train, X_test, y_train, y_test = train_test_split(features, target,
                                                            test_size=test_size, random_state=random_state)
        return X_train, X_test, y_train, y_test

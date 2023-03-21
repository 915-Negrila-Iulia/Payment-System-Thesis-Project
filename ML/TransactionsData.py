from collections import Counter

import pandas as pd
import numpy as np
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, confusion_matrix, average_precision_score
import matplotlib.pyplot as plt
import seaborn as sns
from imblearn.over_sampling import SMOTE, RandomOverSampler
from imblearn.pipeline import Pipeline
from xgboost import XGBClassifier
import pickle
from faker import Faker
import random

# Setting to display all columns
pd.set_option('display.max_columns', None)

class TransactionsData:
    def __init__(self, filename="transactions.csv"):
        # Load the csv file
        self.df = pd.read_csv(filename)
        self.df.drop(['isFlaggedFraud'], inplace=True, axis=1)

    def exploratory_data_analysis(self):
        # basic info about dataset -> (6362620 rows, 11 cols)
        print('INFO:')
        print('shape: {}'.format(self.df.shape))
        self.df.info()

        # basic statistics, with regular format
        print('STATISTICS:')
        print(self.df.describe().apply(lambda s: s.apply('{0:.5f}'.format)))
        categorical_values = self.df[['type','nameOrig','nameDest']]
        print(categorical_values.describe())

        # maximum sum of missing values on each column = 0 -> No. missing values
        print("No. of missing values: {}".format(self.df.isnull().sum().max()))

        # duplicate values -> none
        print("No. of duplicate values: {}".format(self.df.duplicated().sum()))

        # Fraud vs Genuine
        print("No. of Frauds and No. of Genuine transactions:")
        print(self.df['isFraud'].value_counts())

        # only transactions of type 'TRANSFER' and 'CASH_OUT' can be frauds
        types = list(self.df[self.df.isFraud == 1].type.drop_duplicates().values)
        print("Types of fraudulent transactions: {}".format(types))
        transfer_frauds = self.df[(self.df.isFraud == 1) & (self.df.type == 'TRANSFER')]
        cash_out_frauds = self.df[(self.df.isFraud == 1) & (self.df.type == 'CASH_OUT')]
        print("No. of TRANSFER frauds: {}\nNo. of CASH_OUT frauds: {}".format(len(transfer_frauds), len(cash_out_frauds)))

        # because numbers calculated above are so close we have a theory regarding frauds:
        # fraud transaction = transfer funds to a specific account and then cash_out from that account

        # fraud theory1: first make a transfer Org1 -> Dest1 than cash_out the money Org2 -> Dest2
        # (both transactions are frauds)
        # let's test it -> FALSE
        print("Any pairs like (fraud TRANSFER destination == fraud CASH_OUT origin): {}".format(
            (transfer_frauds.nameDest.isin(cash_out_frauds.nameOrig)).any()))

        # fraud theory2: first make a transfer Org1 -> Dest1 than cash_out the money Org2 -> Dest2
        # (transfer is fraud and cash_out is not detected as fraud)
        # let's test it -> TRUE
        cash_out_not_frauds = self.df[(self.df.isFraud == 0) & (self.df.type == 'CASH_OUT')]
        print("Any pairs like (fraud TRANSFER destination == genuine CASH_OUT origin): {}".format(
            (transfer_frauds.nameDest.isin(cash_out_not_frauds.nameOrig)).any()))
        print(transfer_frauds[(transfer_frauds.nameDest.isin(cash_out_not_frauds.nameOrig))])
        print(cash_out_not_frauds[(cash_out_not_frauds.nameOrig.isin(transfer_frauds.nameDest))])

        # disregard our suspicion => account names features are not useful for prediction

        # we will use only TRANSFER and CASH_OUT transactions, because we proved that these are relevant
        # check transactions with oldDestBalance == newDestBalance == 0 even though the amount != 0
        # see no. of frauds and genuine transactions with given criteria
        transactions_type_criteria = (self.df.type == 'TRANSFER') | (self.df.type == 'CASH_OUT')
        frauds = self.df[(self.df.isFraud == 1) & transactions_type_criteria]
        not_frauds = self.df[(self.df.isFraud == 0) & transactions_type_criteria]
        print("Fraudulent transactions with (oldOrigBalance == newOrigBalance == 0 and amount != 0): \n{}%"
              " from fraudulent transactions".format(
            len(frauds[(frauds.oldbalanceDest == 0) & (frauds.newbalanceDest == 0)
                       & (frauds.amount != 0)]) / (1.0 * len(frauds)))) # 0.4955558261293072%
        print("Non-Fraudulent transactions with (oldOrigBalance == newOrigBalance == 0 and amount != 0): \n{}%"
              " from genuine transactions".format(
            len(not_frauds[(not_frauds.oldbalanceDest == 0) & (not_frauds.newbalanceDest == 0)
                           & (not_frauds.amount != 0)]) / (1.0 * len(not_frauds)))) # 0.0006176245277308345%
        # seeing the differences of % between results (fraud and not-fraud) above it seems that
        # (oldOrigBalance == newOrigBalance == 0 and amount != 0) could be an indicator of fraud
        print("Fraudulent transactions with (oldOrigBalance == newOrigBalance == 0 and amount != 0): \n{}%"
              " from fraudulent transactions".format(
            len(frauds[(frauds.oldbalanceOrg == 0) & (frauds.newbalanceOrig == 0)
                       & (frauds.amount != 0)]) / (1.0 * len(frauds)))) # 0.0030439547059539756%
        print("Non-Fraudulent transactions with (oldOrigBalance == newOrigBalance == 0 and amount != 0): \n{}%"
              " from genuine transactions".format(
            len(not_frauds[(not_frauds.oldbalanceOrg == 0) & (not_frauds.newbalanceOrig == 0)
                           & (not_frauds.amount != 0)]) / (1.0 * len(not_frauds)))) # 0.32873940872846197%

    def visualization(self):
        # correlation matrix
        # the closer the value is to 1 or -1, the stronger the relationship
        # the closer the value is to 0, the weaker the relationship
        plt.figure(figsize=(12, 12))
        matrix = self.df.corr().round(2)
        sns.heatmap(matrix, annot=True, vmax=1, vmin=-1, center=0, cmap='vlag')
        plt.show()

        # drawing pychart for types of transaction
        type = self.df.type.value_counts()
        transaction = type.index
        count = type.values
        plt.figure(figsize=(8,8))
        plt.pie(count, labels=transaction, autopct='%1.0f%%')
        plt.legend(loc='upper left')
        plt.show()

        # no. of fraud and genuine transactions for each type
        plt.figure(figsize=(12,8))
        ax = sns.countplot(x='type', hue='isFraud', data=self.df)
        plt.title('Types of Transaction - Fraud and Genuine')
        for p in ax.patches:
            ax.annotate('{:.1f}'.format(p.get_height()), (p.get_x()+0.1, p.get_height()+50))
        plt.show()

        # transactions (fraud and genuine) on each day of the week
        frauds = self.df[self.df.isFraud == 1]
        genuine = self.df[self.df.isFraud == 0]
        no_days = 7
        no_hours = 24
        fraud_days = (frauds.step // no_hours) % no_days
        fraud_hours = frauds.step % no_hours
        genuine_days = (genuine.step // no_hours) % no_days
        genuine_hours = genuine.step % no_hours
        # fraud transactions(red) and genuine transactions(green) on each day of the week
        plt.subplot(1, 2, 1)
        fraud_days.hist(bins=no_days,color="red")
        plt.title('Daily Frauds')
        plt.xlabel('Day of the Week')
        plt.ylabel('No. of transactions')
        plt.subplot(1, 2, 2)
        genuine_days.hist(bins=no_days, color="green")
        plt.title('Daily Genuine Transactions')
        plt.xlabel('Day of the Week')
        plt.ylabel('No. of Transactions')
        plt.tight_layout()
        plt.show()
        # => not much evidence to suggest when frauds occur
        # => genuine transactions occur less on some consecutive days of week
        plt.subplot(1, 2, 1)
        fraud_hours.hist(bins=no_hours, color="red")
        plt.title('Frauds per Hour')
        plt.xlabel('Hour of the Day')
        plt.ylabel("No. of Transactions")
        plt.subplot(1, 2, 2)
        genuine_hours.hist(bins=no_hours, color="green")
        plt.title('Genuine transactions per Hour')
        plt.xlabel('Hour of the Day')
        plt.ylabel("No. of Transactions")
        plt.tight_layout()
        plt.show()
        # => from hour 0 to hour 9 genuine transactions occur rarely
        # => frauds occur at similar rates any hour of the day
        # => we create a new feature 'HourOfDay' = step%24

    def clean_data(self):
        # from EDA => fraud occurs only in TRANSFER and CASH_OUT transactions
        # so we will use only these 2 types in the dataset
        self.df = self.df[(self.df.type == 'TRANSFER') | (self.df.type == 'CASH_OUT')]

        # eliminate irrelevant columns
        self.df.drop(['nameOrig', 'nameDest', 'isFlaggedFraud'], inplace=True, axis=1)

        # Binary-encoding of labelled data in 'type'
        self.df.loc[self.df.type == 'TRANSFER', 'type'] = 0
        self.df.loc[self.df.type == 'CASH_OUT', 'type'] = 1
        self.df['type'] = pd.to_numeric(self.df['type'])

        # info about dataset after cleaning -> -> (2770409 rows, 8 cols)
        # so we reduced dataset from 6362620 rows to 2770409 rows
        print('INFO after clean:')
        print('shape: {}'.format(self.df.shape))
        self.df.info()

        # basic statistics
        print('STATISTICS:')
        print(self.df.describe())



td = TransactionsData()
td.visualization()
#td.exploratory_data_analysis()
#td.clean_data()
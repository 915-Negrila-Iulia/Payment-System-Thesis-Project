import pandas as pd

from app.utils.data_preprocessing import DataPreprocessing

# Setting to display all columns
pd.set_option('display.max_columns', None)

class EDA:

    def __init__(self):
        self.data_preprocessing = DataPreprocessing()
        self.df = self.data_preprocessing.df

    def display_info(self):
        # basic info about dataset -> (6362620 rows, 10 cols)
        print('INFO:')
        print('shape: {}'.format(self.df.shape))
        self.df.info()

    def display_statistics(self):
        # basic statistics, with regular format
        print('STATISTICS:')
        print(self.df.describe().apply(lambda s: s.apply('{0:.5f}'.format)))
        categorical_values = self.df[['type', 'nameOrig', 'nameDest']]
        print(categorical_values.describe())

    def check_duplicate_or_missing_values(self):
        # maximum sum of missing values on each column = 0 -> No. missing values
        print("No. of missing values: {}".format(self.df.isnull().sum().max()))
        # duplicate values -> none
        print("No. of duplicate values: {}".format(self.df.duplicated().sum()))

    def display_fraud_vs_genuine(self):
        # Fraud vs Genuine transactions
        print("No. of Frauds and No. of Genuine transactions:")
        print(self.df['isFraud'].value_counts())

    def display_types_of_fraudulent_transactions(self):
        # only transactions of type 'TRANSFER' and 'CASH_OUT' can be frauds
        types = list(self.df[self.df.isFraud == 1].type.drop_duplicates().values)
        print("Types of fraudulent transactions: {}".format(types))

    def fraud_theory1(self):
        """
        Based on the fact that number of 'TRANSFER' frauds is close to
        the number of 'CASH_OUT' frauds, check the following possibility of fraud process:
        First make 'TRANSFER' from A to B, then 'CASH_OUT' the money from B
        theory1: both 'TRANSFER' and 'CASH_OUT' transactions are detected as frauds
        result => theory1 is FALSE
        :return: -
        """
        transfer_frauds = self.df[(self.df.isFraud == 1) & (self.df.type == 'TRANSFER')]
        cash_out_frauds = self.df[(self.df.isFraud == 1) & (self.df.type == 'CASH_OUT')]
        print(
            "No. of TRANSFER frauds: {}\nNo. of CASH_OUT frauds: {}".format(len(transfer_frauds), len(cash_out_frauds)))
        print("Any pairs like (fraud TRANSFER receiver == fraud CASH_OUT sender): {}".format(
            (transfer_frauds.nameDest.isin(cash_out_frauds.nameOrig)).any()))

    def fraud_theory2(self):
        """
        Based on the fact that number of 'TRANSFER' frauds (4097) is close to
        the number of 'CASH_OUT' frauds (4116), check the following possibility of fraud process:
        First make 'TRANSFER' from A to B, then 'CASH_OUT' the money from B
        theory2: 'TRANSFER' transaction is detected as fraud, but 'CASH_OUT' transaction is NOT
        result => theory2 is TRUE
        :return: -
        """
        transfer_frauds = self.df[(self.df.isFraud == 1) & (self.df.type == 'TRANSFER')]
        cash_out_not_frauds = self.df[(self.df.isFraud == 0) & (self.df.type == 'CASH_OUT')]
        print("Any pairs like (fraud TRANSFER receiver == genuine CASH_OUT sender): {}".format(
            (transfer_frauds.nameDest.isin(cash_out_not_frauds.nameOrig)).any()))
        print(transfer_frauds[(transfer_frauds.nameDest.isin(cash_out_not_frauds.nameOrig))])
        print(cash_out_not_frauds[(cash_out_not_frauds.nameOrig.isin(transfer_frauds.nameDest))])

    def relations_between_features(self):
        """
        Use only 'TRANSFER' and 'CASH_OUT' transactions after proving that only these are relevant
        Compare number of frauds and number of genuine transactions on different criteria
        And try to find a potential fraud indicator
        :return: -
        """

        # criteria1: oldDestBalance == newDestBalance == 0 even though the amount != 0
        transactions_type_criteria = (self.df.type == 'TRANSFER') | (self.df.type == 'CASH_OUT')
        frauds = self.df[(self.df.isFraud == 1) & transactions_type_criteria]
        not_frauds = self.df[(self.df.isFraud == 0) & transactions_type_criteria]
        print("Fraudulent transactions with (oldBalanceReceiver == newBalanceReceiver == 0 and amount != 0): \n{}%"
              " from fraudulent transactions".format(
            len(frauds[(frauds.oldBalanceReceiver == 0) & (frauds.newBalanceReceiver == 0)
                       & (frauds.amount != 0)]) / (1.0 * len(frauds))))  # 0.4955558261293072%
        print("Non-Fraudulent transactions with (oldBalanceReceiver == newBalanceReceiver == 0 and amount != 0): \n{}%"
              " from genuine transactions".format(
            len(not_frauds[(not_frauds.oldBalanceReceiver == 0) & (not_frauds.newBalanceReceiver == 0)
                           & (not_frauds.amount != 0)]) / (1.0 * len(not_frauds))))  # 0.0006176245277308345%

        # criteria2: oldBalanceSender == newBalanceSender == 0 and amount != 0
        print("Fraudulent transactions with (oldBalanceSender == newBalanceSender == 0 and amount != 0): \n{}%"
              " from fraudulent transactions".format(
            len(frauds[(frauds.oldBalanceSender == 0) & (frauds.newBalanceSender == 0)
                       & (frauds.amount != 0)]) / (1.0 * len(frauds))))  # 0.0030439547059539756%
        print("Non-Fraudulent transactions with (oldBalanceSender == newBalanceSender == 0 and amount != 0): \n{}%"
              " from genuine transactions".format(
            len(not_frauds[(not_frauds.oldBalanceSender == 0) & (not_frauds.newBalanceSender == 0)
                           & (not_frauds.amount != 0)]) / (1.0 * len(not_frauds))))  # 0.4737321319703598%

    def exploratory_data_analysis(self):
        self.display_info()
        self.display_statistics()
        self.check_duplicate_or_missing_values()
        self.display_fraud_vs_genuine()
        self.display_types_of_fraudulent_transactions()
        self.fraud_theory1()
        self.fraud_theory2()
        # from theory1 and theory2 => account names features are not useful for prediction
        self.relations_between_features()


eda = EDA()
eda.exploratory_data_analysis()
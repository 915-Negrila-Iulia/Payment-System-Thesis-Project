import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
import seaborn as sns

from app.utils.data_preprocessing import DataPreprocessing


class DataVisualization:

    def __init__(self):
        self.data_preprocessing = DataPreprocessing()
        self.df = self.data_preprocessing.df

    def plot_correlation_matrix(self):
        # correlation matrix
        # the closer the value is to 1 or -1, the stronger the relationship
        # the closer the value is to 0, the weaker the relationship
        data = self.data_preprocessing.clean_data()
        plt.figure(figsize=(14, 14))
        matrix = data.corr().round(2)
        sns.heatmap(matrix, annot=True, vmax=1, vmin=-1, center=0, cmap='vlag')
        plt.show()

    def plot_transaction_types(self):
        # drawing pychart for types of transaction
        type = self.df.type.value_counts()
        transaction = type.index
        count = type.values
        plt.figure(figsize=(8, 8))
        plt.pie(count, labels=transaction, autopct='%1.0f%%')
        plt.legend(loc='upper left')
        plt.show()

    def plot_fraud_vs_genuine_transaction_types(self):
        # no. of fraud and genuine transactions for each type
        plt.figure(figsize=(12, 8))
        ax = sns.countplot(x='type', hue='isFraud', data=self.df)
        plt.title('Types of Transaction - Fraud and Genuine')
        for p in ax.patches:
            ax.annotate('{:.1f}'.format(p.get_height()), (p.get_x() + 0.1, p.get_height() + 50))
        plt.show()

    def plot_transactions_per_day_and_hour(self):
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
        fraud_days.hist(bins=no_days, color="red")
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
        # => we create a new feature for the hour of the day: 'timeInHours' = step%24

    def plot_kde(self):
        df = self.df

        # create new columns with log-transformed values
        df['oldBalanceSender_log'] = np.log10(df['oldBalanceSender'] + 1)  # adding 1 to avoid log(0)
        df['newBalanceSender_log'] = np.log10(df['newBalanceSender'] + 1)
        df['oldBalanceReceiver_log'] = np.log10(df['oldBalanceReceiver'] + 1)
        df['newBalanceReceiver_log'] = np.log10(df['newBalanceReceiver'] + 1)

        # plot kernel density estimations for the log-transformed values
        plt.figure(figsize=(12, 8))
        sns.kdeplot(df['oldBalanceSender_log'], label='Old Balance Sender', fill=True)
        sns.kdeplot(df['newBalanceSender_log'], label='New Balance Sender', fill=True)
        sns.kdeplot(df['oldBalanceReceiver_log'], label='Old Balance Receiver', fill=True)
        sns.kdeplot(df['newBalanceReceiver_log'], label='New Balance Receiver', fill=True)
        plt.title("Distribution of Old and New Balances for Sender and Receiver (log-transformed)")
        plt.legend()
        plt.show()

    def draw_plots(self):
        self.plot_correlation_matrix()
        self.plot_transaction_types()
        self.plot_fraud_vs_genuine_transaction_types()
        self.plot_transactions_per_day_and_hour()
        self.plot_kde()

# dv = DataVisualization()
# dv.draw_plots()
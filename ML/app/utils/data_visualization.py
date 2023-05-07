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
        type_counts = self.df.type.value_counts()
        fig, ax = plt.subplots(figsize=(8, 8))
        ax.pie(type_counts.values, labels=type_counts.index, autopct='%1.1f%%', startangle=90, counterclock=False,
               pctdistance=0.85, wedgeprops=dict(width=0.5))
        # Add a circle in the center to make it a donut chart
        circle = plt.Circle(xy=(0, 0), radius=0.5, facecolor='white')
        ax.add_artist(circle)
        ax.set_title('Types of Transactions')
        plt.show()

    def plot_fraud_vs_genuine(self, dataset, title):
        # Calculate number of frauds and genuine transactions
        num_frauds = len(dataset[dataset.isFraud == 1])
        num_genuine = len(dataset[dataset.isFraud == 0])

        values = [num_frauds, num_genuine]
        labels = ['Frauds', 'Genuine']

        fig, ax = plt.subplots()
        ax.pie(values, labels=labels, autopct='%1.1f%%', startangle=90, colors=['red', '#7fb9b3'])
        # draw circle
        centre_circle = plt.Circle((0, 0), 0.70, fc='white')
        fig.gca().add_artist(centre_circle)
        ax.set_title(title)
        plt.show()

    def plot_fraud_vs_genuine_transaction_types(self):
        # no. of fraud and genuine transactions for each type
        plt.figure(figsize=(12, 8))
        ax = sns.countplot(x='type', hue='isFraud', data=self.df)
        plt.yscale('log')
        plt.title('Types of Transaction - Fraud and Genuine')
        for p in ax.patches:
            ax.annotate(p.get_height(), (p.get_x() + 0.1, p.get_height() + 50))
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

        # plot daily transactions
        plt.subplot(2, 1, 1)
        sns.histplot(fraud_days, stat="density", color="red", alpha=0.5, label="Frauds", bins=no_days)
        sns.histplot(genuine_days, stat="density", color="green", alpha=0.5, label="Genuine Transactions",
                     bins=no_days)
        plt.title('Daily Transactions')
        plt.xlabel('Days of the Week')
        plt.ylabel("No. of Transactions")
        plt.legend()
        # => not much evidence to suggest when frauds occur
        # => genuine transactions occur less on some consecutive days of week

        # plot hourly transactions
        plt.subplot(2, 1, 2)
        sns.histplot(fraud_hours, stat="density", color="red", alpha=0.5, label="Frauds", bins=no_hours)
        sns.histplot(genuine_hours, stat="density", color="green", alpha=0.5, label="Genuine Transactions", bins=no_hours)
        plt.title('Hourly Transactions')
        plt.xlabel('Hour of the Day')
        plt.ylabel("No. of Transactions")
        plt.legend()
        # => from hour 0 to hour 9 genuine transactions occur rarely
        # => frauds occur at similar rates any hour of the day
        # => we create a new feature for the hour of the day: 'timeInHours' = step%24

        plt.tight_layout()
        plt.show()

    def plot_imbalanced_vs_balanced_data(self):
        data = self.data_preprocessing.clean_data()
        testing_data = pd.read_csv('../data/testing_data.csv')
        frauds = len(data[data.isFraud == 1])
        genuine = len(data[data.isFraud == 0])
        testing_frauds = len(testing_data[testing_data.isFraud == 1])
        testing_genuine = len(testing_data[testing_data.isFraud == 0])
        imbalanced_num_frauds = frauds - testing_frauds
        imbalanced_num_genuine = genuine - testing_genuine

        rus_balanced_data = pd.read_csv('../data/training_RandomUnderSampling.csv')
        rus_balanced_num_frauds = len(rus_balanced_data[rus_balanced_data.isFraud == 1])
        rus_balanced_num_genuine = len(rus_balanced_data[rus_balanced_data.isFraud == 0])

        smote_balanced_data = pd.read_csv('../data/training_SMOTE.csv')
        smote_balanced_num_frauds = len(smote_balanced_data[smote_balanced_data.isFraud == 1])
        smote_balanced_num_genuine = len(smote_balanced_data[smote_balanced_data.isFraud == 0])

        fig, (ax0, ax1, ax2) = plt.subplots(1, 3, figsize=(12, 4), sharey='all')
        colors = ['#ff8e7f', '#7fb9b3']

        ax0.bar(['Fraud', 'Genuine'], [imbalanced_num_frauds, imbalanced_num_genuine], color=colors)
        ax0.set_title('Imbalanced original dataset')

        ax1.bar(['Fraud', 'Genuine'], [rus_balanced_num_frauds, rus_balanced_num_genuine], color=colors)
        ax1.set_title('Sampled dataset using RUS')

        ax2.bar(['Fraud', 'Genuine'], [smote_balanced_num_frauds, smote_balanced_num_genuine], color=colors)
        ax2.set_title('Sampled dataset using SMOTE')

        ax0.set_ylabel('Number of Transactions')
        ax0.set_yscale('log')
        ax0.set_ylim(bottom=1000)

        plt.show()


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
        self.plot_fraud_vs_genuine(self.df,'Frauds vs Genuine Transactions')
        self.plot_transaction_types()
        self.plot_fraud_vs_genuine_transaction_types()
        self.plot_transactions_per_day_and_hour()
        self.plot_kde()

dv = DataVisualization()
# dv.draw_plots()
# dataset = dv.data_preprocessing.clean_data()
# dv.plot_fraud_vs_genuine(dataset, 'Frauds vs Genuine Transactions after preprocessing')
# dv.plot_imbalanced_vs_balanced_data()

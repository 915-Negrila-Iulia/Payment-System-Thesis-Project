from collections import Counter

import pandas as pd
import numpy as np
from imblearn.combine import SMOTETomek, SMOTEENN
from imblearn.under_sampling import RandomUnderSampler, TomekLinks
from sklearn import svm
from sklearn.naive_bayes import BernoulliNB, MultinomialNB, GaussianNB
from sklearn.neighbors import KNeighborsRegressor, KNeighborsClassifier
from sklearn.neural_network import MLPClassifier
from sklearn.preprocessing import StandardScaler, MinMaxScaler
from sklearn.ensemble import RandomForestClassifier, BaggingClassifier, VotingClassifier, AdaBoostClassifier
from sklearn.model_selection import train_test_split, cross_val_score, KFold, GridSearchCV, cross_validate, \
    cross_val_predict
from sklearn.linear_model import LogisticRegression, SGDClassifier
from sklearn.metrics import classification_report, confusion_matrix, average_precision_score, roc_auc_score, \
    mean_squared_error, accuracy_score, precision_score, recall_score, f1_score, make_scorer, roc_curve, auc
import matplotlib.pyplot as plt
import seaborn as sns
from imblearn.over_sampling import SMOTE, RandomOverSampler
from imblearn.pipeline import Pipeline, make_pipeline
from sklearn.tree import DecisionTreeRegressor
from xgboost import XGBClassifier
from lightgbm import LGBMClassifier
import pickle
from faker import Faker
import random

from ClassifierUtils import ClassifierUtils

# Setting to display all columns
pd.set_option('display.max_columns', None)


class TransactionsData:
    def __init__(self, filename="transactions.csv"):
        # Load the csv file
        self.df = pd.read_csv(filename)
        self.df.drop(['isFlaggedFraud'], inplace=True, axis=1)
        self.df = self.df.rename(columns={'oldbalanceOrg': 'oldBalanceSender', 'newbalanceOrig': 'newBalanceSender', \
                                          'oldbalanceDest': 'oldBalanceReceiver',
                                          'newbalanceDest': 'newBalanceReceiver'})

    def exploratory_data_analysis(self):
        # basic info about dataset -> (6362620 rows, 11 cols)
        print('INFO:')
        print('shape: {}'.format(self.df.shape))
        self.df.info()

        # basic statistics, with regular format
        print('STATISTICS:')
        print(self.df.describe().apply(lambda s: s.apply('{0:.5f}'.format)))
        categorical_values = self.df[['type', 'nameOrig', 'nameDest']]
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
        print(
            "No. of TRANSFER frauds: {}\nNo. of CASH_OUT frauds: {}".format(len(transfer_frauds), len(cash_out_frauds)))

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
        print("Fraudulent transactions with (oldBalanceReceiver == newBalanceReceiver == 0 and amount != 0): \n{}%"
              " from fraudulent transactions".format(
            len(frauds[(frauds.oldBalanceReceiver == 0) & (frauds.newBalanceReceiver == 0)
                       & (frauds.amount != 0)]) / (1.0 * len(frauds))))  # 0.4955558261293072%
        print("Non-Fraudulent transactions with (oldBalanceReceiver == newBalanceReceiver == 0 and amount != 0): \n{}%"
              " from genuine transactions".format(
            len(not_frauds[(not_frauds.oldBalanceReceiver == 0) & (not_frauds.newBalanceReceiver == 0)
                           & (not_frauds.amount != 0)]) / (1.0 * len(not_frauds))))  # 0.0006176245277308345%
        # seeing the differences of % between results (fraud and not-fraud) above it seems that
        # (oldOrigBalance == newOrigBalance == 0 and amount != 0) could be an indicator of fraud
        print("Fraudulent transactions with (oldBalanceSender == newBalanceSender == 0 and amount != 0): \n{}%"
              " from fraudulent transactions".format(
            len(frauds[(frauds.oldBalanceSender == 0) & (frauds.newBalanceSender == 0)
                       & (frauds.amount != 0)]) / (1.0 * len(frauds))))  # 0.0030439547059539756%
        print("Non-Fraudulent transactions with (oldBalanceSender == newBalanceSender == 0 and amount != 0): \n{}%"
              " from genuine transactions".format(
            len(not_frauds[(not_frauds.oldBalanceSender == 0) & (not_frauds.newBalanceSender == 0)
                           & (not_frauds.amount != 0)]) / (1.0 * len(not_frauds))))  # 0.32873940872846197%

    def visualize_data(self):
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
        plt.figure(figsize=(8, 8))
        plt.pie(count, labels=transaction, autopct='%1.0f%%')
        plt.legend(loc='upper left')
        plt.show()

        # no. of fraud and genuine transactions for each type
        plt.figure(figsize=(12, 8))
        ax = sns.countplot(x='type', hue='isFraud', data=self.df)
        plt.title('Types of Transaction - Fraud and Genuine')
        for p in ax.patches:
            ax.annotate('{:.1f}'.format(p.get_height()), (p.get_x() + 0.1, p.get_height() + 50))
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

    def clean_data(self):
        # from EDA => fraud occurs only in TRANSFER and CASH_OUT transactions
        # so we will use only these 2 types in the dataset
        self.df = self.df[(self.df.type == 'TRANSFER') | (self.df.type == 'CASH_OUT')]

        # eliminate irrelevant columns
        self.df.drop(['nameOrig', 'nameDest'], inplace=True, axis=1)

        # Binary-encoding of labelled data in 'type'
        self.df.loc[self.df.type == 'TRANSFER', 'type'] = 0
        self.df.loc[self.df.type == 'CASH_OUT', 'type'] = 1
        self.df['type'] = pd.to_numeric(self.df['type'])

        # based on discussion => we create a new feature 'timeInHours' = step%24
        # and drop the step column because we do not need it anymore
        # and also the web app will provide info about 'step' only about 'timeInHours'
        self.df['timeInHours'] = np.nan
        self.df.timeInHours = self.df.step % 24
        self.df.drop(['step'], inplace=True, axis=1)

        # info about dataset after cleaning -> -> (2770409 rows, 8 cols)
        # so we reduced dataset from 6362620 rows to 2770409 rows
        print('INFO after clean:')
        print('shape: {}'.format(self.df.shape))
        self.df.info()
        # see first elements
        print(self.df.head())

        # basic statistics
        print('STATISTICS:')
        print(self.df.describe())

    def prep_data(self):
        """
        X: data columns (nameOrig, oldBalanceOrig, ...)
        y: target column (isFraud)
        """
        x_columns = ['type', 'amount', 'oldBalanceSender', 'newBalanceSender', 'oldBalanceReceiver',
                     'newBalanceReceiver', 'timeInHours']
        X = self.df.loc[:, x_columns].values
        y = self.df["isFraud"].values
        return X, y

    """
        Unscaled, Normalized, Standardize Data
    """

    def scaling_data(self, model):
        # Unscaled
        X, y = self.prep_data()
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=50)

        # Normalization
        X_train_norm = MinMaxScaler().fit_transform(X_train)  # transform training data
        X_test_norm = MinMaxScaler().fit_transform(X_test)  # transform test data

        # Standardization
        X_train_stand = StandardScaler().fit_transform(X_train)  # transform the training data column
        X_test_stand = StandardScaler().fit_transform(X_test)  # transform the testing data column

        # apply scaling to ML algo
        # raw, normalized and standardized training and testing data
        trainX = [X_train, X_train_norm, X_train_stand]
        testX = [X_test, X_test_norm, X_test_stand]

        # RMSE = Root Mean Square Error -> standard way to measure the error of a model in predicting quantitative data
        rmse = []

        # Calculate the cross-validation scores before standardization
        scores_before = cross_val_score(model, X_train, y_train, cv=5)
        scores_after = cross_val_score(model, X_train_norm, y_train, cv=5)

        print("\nBefore ",scores_before.mean(),"\nAfter",scores_after.mean())

        # model fitting and measuring RMSE
        # for i in range(len(trainX)):
        #     # fit
        #     model.fit(trainX[i], y_train)
        #     # predict
        #     pred = model.predict(testX[i])
        #     # RMSE
        #     rmse.append(np.sqrt(mean_squared_error(y_test, pred)))

        # visualizing the result
        # print(str(model))
        # df_rmse = pd.DataFrame({'RMSE': rmse}, index=['Original', 'Normalized', 'Standardized'])
        # print(df_rmse)

    """
        Run given classifier with default params
    """

    def classification(self, model):
        X, y = self.prep_data()
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=50)

        clf = model.fit(X_train, y_train)
        probabilities = clf.predict_proba(X_test)
        predicted = model.predict(X_test)

        print("report:\n", classification_report(y_test, predicted))
        conf_mat = confusion_matrix(y_true=y_test, y_pred=predicted)
        print("confMatrx:\n", conf_mat)
        print("AUPRC: ", average_precision_score(y_test, probabilities[:, 1]))
        print("score: ", clf.score(X_test, y_test))
        print("k-fold cross validation: ", cross_val_score(clf, X, y, cv=5))

    """
        The data is highly unbalanced, the positive class (frauds) account for 0.01% of all transactions. 
        So I will be measuring the accuracy using the Area Under the Precision-Recall Curve (AUPRC). 
        Confusion matrix accuracy is not meaningful for unbalanced classification.
    """

    def ml_func(self, algorithm):
        features, target = self.prep_data()
        X_train, X_test, y_train, y_test = train_test_split(features, target, test_size=0.2)

        # train and fit regression model
        model = algorithm()
        model.fit(X_train, y_train)

        # predict
        train_preds = model.predict(X_train)
        test_preds = model.predict(X_test)

        # evaluate
        train_accuracy = roc_auc_score(y_train, train_preds)
        test_accuracy = roc_auc_score(y_test, test_preds)
        # report = classification_report(y_test, test_preds)

        print(str(algorithm))
        print("------------------------")
        print(f"Training Accuracy: {(train_accuracy * 100):.4}%")
        print(f"Test Accuracy:     {(test_accuracy * 100):.4}%")

        # store accuracy in a new dataframe
        score_logreg = [algorithm, train_accuracy, test_accuracy]
        models = pd.DataFrame([score_logreg])

    def kfold_scores(self, model, X, y, param_grid, n_folds=5, scoring='accuracy'):
        """
        Performs k-fold cross-validation to tune the hyperparameters of a machine learning model.

        Parameters:
            model (sklearn estimator): The machine learning model to tune.
            X (array-like): The input features for the model.
            y (array-like): The target variable for the model.
            param_grid (dict): The hyperparameters to tune and their possible values.
            n_folds (int): The number of folds to use for cross-validation. Default is 5.
            scoring (str): The scoring metric to use for cross-validation. Default is 'accuracy'.

        Returns:
            best_params (dict): The best hyperparameters found during cross-validation.
            best_score (float): The score achieved with the best hyperparameters found.
        """
        # Create a k-fold cross-validation object
        kf = KFold(n_splits=n_folds, shuffle=True, random_state=42)

        # Create a grid search objects
        grid = GridSearchCV(model, param_grid=param_grid, cv=kf, scoring=scoring)

        # Fit the grid search object to the data
        grid.fit(X, y)

        # Return the best hyperparameters and the score achieved with them
        best_params = grid.best_params_
        best_score = grid.best_score_

        return best_params, best_score

    def sampling(self, method, X, y):
        X_resampled, y_resampled = method.fit_resample(X,y)
        return X_resampled, y_resampled

    def tune_hyperparameters(self):
        features, target = self.prep_data()
        X_train, X_test, y_train, y_test = train_test_split(features, target, test_size=0.2)

        #X_resampled, y_resampled = self.sampling(RandomUnderSampler(), X_train, y_train)
        #X_resampled, y_resampled = self.sampling(SMOTE(), X_train, y_train)
        #X_resampled, y_resampled = self.sampling(SMOTETomek(), X_train, y_train)
        #X_resampled, y_resampled = self.sampling(SMOTEENN(), X_train, y_train)

        # Create an instance of the model and define the hyperparameters to tune
        # use n_jobs = -1 to enable parallel processing and make use of all CPU cores
        LR = ClassifierUtils(LogisticRegression(), {'C': [0.001, 0.01, 0.1, 1, 10, 100]})
        XGB = ClassifierUtils(XGBClassifier(), {'n_estimators': [200], 'max_depth': [7], 'learning_rate': [0.8],
                                                'n_jobs': [14]})
        RF = ClassifierUtils(RandomForestClassifier(), {'n_estimators': [200], 'max_depth': [10],
                                                        'min_samples_split': [8], 'n_jobs': [14]})
        NB = ClassifierUtils(GaussianNB(), {})
        KNN = ClassifierUtils(KNeighborsClassifier(), {'n_neighbors': [3, 5, 7], 'n_jobs': [14]})
        ADA = ClassifierUtils(AdaBoostClassifier(), {'n_estimators': [100]})
        LGBM = ClassifierUtils(LGBMClassifier(), {'max_depth': [7]})
        MLP = ClassifierUtils(MLPClassifier(), {})

        lr = LogisticRegression(C=0.01)
        rf = RandomForestClassifier(max_depth=5)
        bagging_model = BaggingClassifier(base_estimator=lr, n_estimators=10)

        # Define the scoring metrics
        scoring = {'accuracy': make_scorer(accuracy_score), 'precision': make_scorer(precision_score),
                   'recall': make_scorer(recall_score), 'f1': make_scorer(f1_score),
                   'average_precision': make_scorer(average_precision_score), 'roc_auc': make_scorer(roc_auc_score)}

        # Perform the grid search

        # hyperparameters tunning
        grid_search = GridSearchCV(MLP.get_model(), param_grid={}, cv=5, scoring=scoring, refit=False)
        # grid_search = GridSearchCV(bagging_model, param_grid={}, cv=5, scoring=scoring, refit=False) # for bagging
        grid_search.fit(X_train, y_train)

        # Print the results
        print("Scores:\n", grid_search.cv_results_)

        # for mo in [LR.get_model(), NB.get_model(), KNN.get_model(), XGB.get_model(), RF.get_model()]:
        #     # sampling
        #     grid_search = GridSearchCV(mo, param_grid={}, cv=5, scoring=scoring, refit=False)
        #     grid_search.fit(X_resampled, y_resampled)
        #
        #     # Print only mean results
        #     print(mo,"Scores:\n", grid_search.cv_results_)
        #     print(f"Mean accuracy: {grid_search.cv_results_['mean_test_accuracy'][0]}")
        #     print(f"Mean precision: {grid_search.cv_results_['mean_test_precision'][0]}")
        #     print(f"Mean recall: {grid_search.cv_results_['mean_test_recall'][0]}")
        #     print(f"Mean f1: {grid_search.cv_results_['mean_test_f1'][0]}")
        #     print(f"Mean average precision: {grid_search.cv_results_['mean_test_average_precision'][0]}")
        #     print(f"Mean roc auc: {grid_search.cv_results_['mean_test_roc_auc'][0]}")

    def plot_AUC_ROC(self, model):
        features, target = self.prep_data()
        X_train, X_test, y_train, y_test = train_test_split(features, target, test_size=0.2)

        # train classifier on training data
        model.fit(X_train, y_train)

        # Predict probabilities for the testing data
        probas = model.predict_proba(X_test)
        y_scores = probas[:, 1]  # Keep only the positive class probabilities

        # calculate ROC curve
        # fpr = false positive rate
        # tpr = true positive rate
        # thr = thresholds
        fpr, tpr, thresholds = roc_curve(y_test, y_scores)
        roc_auc = auc(fpr, tpr)

        # Plot ROC curve and AUC score
        plt.plot(fpr, tpr, color='darkorange', lw=2, label='ROC curve (area = %0.2f)' % roc_auc)
        plt.plot([0, 1], [0, 1], color='navy', lw=2, linestyle='--')
        plt.xlim([0.0, 1.0])
        plt.ylim([0.0, 1.05])
        plt.xlabel('False Positive Rate')
        plt.ylabel('True Positive Rate')
        plt.title('ROC curve')
        plt.legend(loc="lower right")
        plt.show()


td = TransactionsData()
# td.visualize_data()
# td.exploratory_data_analysis()
td.clean_data()
td.tune_hyperparameters()

# td.plot_AUC_ROC(LogisticRegression(C=0.01))

# rmse
#td.scaling_data(KNeighborsClassifier())

# list of all classifiers that I will run for base models
# algorithms = [LogisticRegression, RandomForestClassifier, XGBClassifier, svm.SVC]

# running each model and print accuracy scores
# for algorithm in algorithms:
#     td.ml_func(algorithm)

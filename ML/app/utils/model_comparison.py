import pickle
import time

import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix, precision_score, recall_score, f1_score, accuracy_score, roc_auc_score, \
    average_precision_score
from sklearn.model_selection import cross_validate
from sklearn.naive_bayes import GaussianNB
from sklearn.neighbors import KNeighborsClassifier
from sklearn.tree import DecisionTreeClassifier
from xgboost import XGBClassifier

from app.utils.model_sampling import ModelSampling
from app.utils.model_tuning import ModelTuning
from data_preprocessing import DataPreprocessing


class ModelComparison:
    """
    Compare performance of ML classifiers
    """

    def __init__(self):
        self.data_preprocessing = DataPreprocessing()
        self.data_preprocessing.clean_data()
        self.model_sampling = ModelSampling()
        self.classifiers = [GaussianNB(), LogisticRegression(), KNeighborsClassifier(), DecisionTreeClassifier(),
                            RandomForestClassifier(), XGBClassifier()]
        self.classifier_names = ['Gaussian NB', 'Logistic Regression', 'KNN', 'Decision Tree', 'Random Forest',
                                 'XGBoost']

    def basic_comparison_util(self, X_train, X_test, y_train, y_test, recordName=""):
        """
        Compare classifiers based on multiple evaluation metrics
        Display the results and also store them in a csv file
        :param X_train: set of feature values for training ML model
        :param X_test: set of feature values for testing ML model
        :param y_train: set of target value for training ML model
        :param y_test: set of target value for testing ML model
        :param recordName: name of a method used, besides the classifier's name
        :param filename: path of csv file to save the results
        :return: list of results
        """
        results = []

        for classifier, name in zip(self.classifiers, self.classifier_names):

            start_time_train = time.time()
            classifier.fit(X_train, y_train)
            end_time_train = time.time()
            train_time = end_time_train - start_time_train

            start_time_test = time.time()
            y_pred = classifier.predict(X_test)
            end_time_test = time.time()
            test_time = end_time_test - start_time_test

            cm = confusion_matrix(y_test, y_pred)
            precision = precision_score(y_test, y_pred)
            recall = recall_score(y_test, y_pred)
            f1 = f1_score(y_test, y_pred)
            accuracy = accuracy_score(y_test, y_pred)
            auroc = roc_auc_score(y_test, y_pred)
            auprc = average_precision_score(y_test, y_pred)

            print(f"Results for {name} with {recordName}:")
            print(f"Confusion Matrix:\n{cm}")
            print(f"Precision: {precision}")
            print(f"Recall: {recall}")
            print(f"F1 Score: {f1}")
            print(f"Accuracy: {accuracy}")
            print(f"Area Under ROC Curve: {auroc}")
            print(f"Area Under PR Curve: {auprc}")
            print(f"Training time: {train_time}")
            print(f"Testing time: {test_time}")
            print("----------------------------------------")

            result = {
                'Classifier': name+" "+recordName,
                'Confusion Matrix': cm,
                'Precision': precision,
                'Recall': recall,
                'Accuracy': accuracy,
                'F1': f1,
                'AUROC': auroc,
                'AUPRC': auprc,
                'Training times (s)': train_time,
                'Testing times(s)': test_time
            }

            results.append(result)

        return results

    def basic_comparison(self):
        X_train, X_test, y_train, y_test = self.data_preprocessing.split_train_test_data()
        results = self.basic_comparison_util(X_train, X_test, y_train, y_test)
        self.save_list_to_csv(results,'../results/basics.csv')

    def basic_cross_validation_comparison(self):
        """
        Compare classifiers based on multiple evaluation metrics
        Use cross validation and compare the means of the results
        Store them in a csv file
        :return: -
        """
        X, y = self.data_preprocessing.partition_data(self.data_preprocessing.df)
        results = []
        scoring = ['precision', 'recall', 'accuracy', 'f1', 'roc_auc', 'average_precision']

        for classifier, name in zip(self.classifiers, self.classifier_names):

            classifier_scores = cross_validate(classifier, X, y, cv=5, scoring=scoring)

            precision_mean = classifier_scores['test_precision'].mean()
            recall_mean = classifier_scores['test_recall'].mean()
            f1_mean = classifier_scores['test_f1'].mean()
            accuracy_mean = classifier_scores['test_accuracy'].mean()
            auroc_mean = classifier_scores['test_roc_auc'].mean()
            auprc_mean = classifier_scores['test_average_precision'].mean()
            train_time = classifier_scores['fit_time'].mean()
            test_time = classifier_scores['score_time'].mean()

            result = {
                'Classifier': name,
                'Precision': precision_mean,
                'Recall': recall_mean,
                'Accuracy': accuracy_mean,
                'F1': f1_mean,
                'AUROC': auroc_mean,
                'AUPRC': auprc_mean,
                'Training time (s)': train_time,
                'Testing time (s)': test_time
            }

            results.append(result)

        df_results = pd.DataFrame(results)

        df_results.to_csv('../results/basics_means.csv', index=False)

    def save_list_to_csv(self, list, filename):
        df = pd.DataFrame(list)
        df.to_csv(filename, index=False)


    def basic_comparison_on_sample_data(self):
        """
        Use sampled data saved in csv files
        To compare classifiers
        And store the results in a csv file
        :return: -
        """
        final_results = []
        X_test, y_test = self.model_sampling.separate_sets_from_csv('../data/testing_data.csv')

        for sampler_name in self.model_sampling.sampler_names:

            X_train, y_train = self.model_sampling.separate_sets_from_csv(f"../data/training_{sampler_name}.csv")
            results_per_sample = self.basic_comparison_util(X_train, X_test, y_train, y_test, recordName=sampler_name)
            final_results.extend(results_per_sample)

        self.save_list_to_csv(final_results, '../results/basics_and_sampling.csv')

    def tuning_comparison_XGBoost(self):
        """

        :return:
        """
        params = {'n_estimators': [100, 200, 400], 'max_depth': [3, 5, 7], 'learning_rate': [0.01, 0.2, 0.8]}
        mt = ModelTuning(XGBClassifier(), params)
        X_train, y_train = self.model_sampling.separate_sets_from_csv(f"../data/training_SMOTETomek.csv")
        tuning_results = mt.tune(X_train, y_train)

        print(tuning_results)
        results = []

        for i, params in enumerate(tuning_results['params']):
            print(i)

            result = {
                'XGBoost + SMOTETomek params': params,
                'Precision': tuning_results['mean_test_precision'][i],
                'Recall': tuning_results['mean_test_recall'][i],
                'Accuracy': tuning_results['mean_test_accuracy'][i],
                'F1': tuning_results['mean_test_f1'][i],
                'AUROC': tuning_results['mean_test_roc_auc'][i],
                'AUPRC': tuning_results['mean_test_average_precision'][i],
                'Training time (s)': tuning_results['mean_fit_time'][i],
                'Testing time (s)': tuning_results['mean_score_time'][i]
            }

            results.append(result)

        df_results = pd.DataFrame(results)

        df_results.to_csv('../results/tuning_xgb.csv', index=False)

    def save_best_model(self):
        # Instantiate the model
        classifier = XGBClassifier()

        X_train, y_train = self.model_sampling.separate_sets_from_csv(f"../data/training_SMOTETomek.csv")
        classifier.fit(X_train, y_train)

        # Make pickle file of our model
        pickle.dump(classifier, open("../model/XGBmodel.pkl", "wb"))

mc = ModelComparison()
# mc.basic_comparison()
# mc.basic_cross_validation_comparison()
# mc.basic_comparison_on_sample_data()
mc.tuning_comparison_XGBoost()
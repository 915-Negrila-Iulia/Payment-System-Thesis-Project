import pickle
import time

import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix, precision_score, recall_score, f1_score, accuracy_score, roc_auc_score, \
    average_precision_score, roc_curve, auc, precision_recall_curve
from sklearn.model_selection import cross_validate
from sklearn.naive_bayes import GaussianNB
from sklearn.neighbors import KNeighborsClassifier
from sklearn.tree import DecisionTreeClassifier
from xgboost import XGBClassifier

from app.utils.model_sampling import ModelSampling
from app.utils.model_tuning import ModelTuning
from data_preprocessing import DataPreprocessing

from sklearn.metrics import RocCurveDisplay

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
                'Classifier': name + " " + recordName,
                'Confusion Matrix': cm,
                'Precision': precision,
                'Recall': recall,
                'Accuracy': accuracy,
                'F1': f1,
                'AUROC': auroc,
                'AUPRC': auprc,
                'Training times (s)': train_time,
                'Testing times (s)': test_time
            }

            results.append(result)

        return results

    def basic_comparison(self, test_size=0.2, results_filename='../results/basics.csv'):
        X_train, X_test, y_train, y_test = self.data_preprocessing.split_train_test_data(test_size)
        results = self.basic_comparison_util(X_train, X_test, y_train, y_test)
        self.save_list_to_csv(results, results_filename)

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

    def tuning_comparison_XGBoost(self, dataset_filename, results_filename):
        """
        Hyperparameter tuning on best classifier (XGBoost) trained on different resampled datasets
        Store the results in a csv file
        :return: -
        """
        params = {'n_estimators': [100, 200, 400], 'max_depth': [3, 5, 7], 'learning_rate': [0.01, 0.2, 0.8]}
        mt = ModelTuning(XGBClassifier(), params)
        X_train, y_train = self.model_sampling.separate_sets_from_csv(dataset_filename)
        tuning_results = mt.tune(X_train, y_train)

        print(tuning_results)
        results = []

        for i, params in enumerate(tuning_results['params']):
            print(i)

            result = {
                'Params': params,
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

        df_results.to_csv(results_filename, index=False)

    def test_final_best_model(self):
        X_test, y_test = self.model_sampling.separate_sets_from_csv('../data/testing_data.csv')
        X_train, y_train = self.model_sampling.separate_sets_from_csv('../data/training_SMOTEENN.csv')

        classifier = XGBClassifier(learning_rate=0.2, max_depth=7, n_estimators=400)
        name = "Tuned XGB with SMOTE+ENN"

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

        print(f"Results for {name}:")
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

    def roc_final_models(self):
        best_model = pickle.load(open("../model/best_model.pkl", "rb"))
        recall_model = pickle.load(open("../model/recall_model.pkl", "rb"))
        fast_model = pickle.load(open("../model/fast_model.pkl", "rb"))

        X_test, y_test = self.model_sampling.separate_sets_from_csv('../data/testing_data.csv')

        best_probs = best_model.predict_proba(X_test)[:,1]
        recall_probs = recall_model.predict_proba(X_test)[:,1]
        fast_probs = fast_model.predict_proba(X_test)[:,1]

        best_fpr, best_tpr, _ = roc_curve(y_test, best_probs)
        recall_fpr, recall_tpr, _ = roc_curve(y_test, recall_probs)
        fast_fpr, fast_tpr, _ = roc_curve(y_test, fast_probs)

        best_auc = auc(best_fpr, best_tpr)
        recall_auc = auc(recall_fpr, recall_tpr)
        fast_auc = auc(fast_fpr, fast_tpr)

        plt.plot(best_fpr, best_tpr, color='blue', label='Best Overall Model AUC = {:.3f}'.format(best_auc))
        plt.plot(recall_fpr, recall_tpr, color='green', label='Best Recall Model AUC = {:.3f}'.format(recall_auc))
        plt.plot(fast_fpr, fast_tpr, color='red', label='Best Testing Time Model AUC = {:.3f}'.format(fast_auc))

        plt.plot([0, 1], [0, 1], color='black', linestyle='--') # Random Guess line

        plt.title('Receiver Operating Characteristic (ROC) Curve')
        plt.xlabel('False Positive Rate (FPR)')
        plt.ylabel('True Positive Rate (TPR)')

        plt.legend()
        plt.show()

    def prc_final_models(self):
        best_model = pickle.load(open("../model/best_model.pkl", "rb"))
        recall_model = pickle.load(open("../model/recall_model.pkl", "rb"))
        fast_model = pickle.load(open("../model/fast_model.pkl", "rb"))

        X_test, y_test = self.model_sampling.separate_sets_from_csv('../data/testing_data.csv')

        best_probs = best_model.predict_proba(X_test)[:,1]
        recall_probs = recall_model.predict_proba(X_test)[:,1]
        fast_probs = fast_model.predict_proba(X_test)[:,1]

        best_precision, best_recall, _ = precision_recall_curve(y_test, best_probs)
        recall_precision, recall_recall, _ = precision_recall_curve(y_test, recall_probs)
        fast_precision, fast_recall, _ = precision_recall_curve(y_test, fast_probs)

        best_avg_precision = average_precision_score(y_test, best_probs)
        recall_avg_precision = average_precision_score(y_test, recall_probs)
        fast_avg_precision = average_precision_score(y_test, fast_probs)

        plt.plot(best_recall, best_precision, color='blue', label='Best Overall Model AP = {:.3f}'.format(best_avg_precision))
        plt.plot(recall_recall, recall_precision, color='green', label='Best Recall Model AP = {:.3f}'.format(recall_avg_precision))
        plt.plot(fast_recall, fast_precision, color='red', label='Best Testing Time Model AP = {:.3f}'.format(fast_avg_precision))

        plt.title('Precision-Recall Curve')
        plt.xlabel('Recall')
        plt.ylabel('Precision')

        plt.legend()
        plt.show()

    def save_model_to_pkl_file(self, classifier, dataset_file, pkl_file):
        X_train, y_train = self.model_sampling.separate_sets_from_csv(dataset_file)
        classifier.fit(X_train, y_train)
        pickle.dump(classifier, open(pkl_file, "wb"))  # Make pickle file of the model

    def save_models(self):
        # save the best overall classifier
        self.save_model_to_pkl_file(XGBClassifier(learning_rate=0.2, max_depth=7, n_estimators=400),
                                    "../data/training_SMOTEENN.csv", "../model/best_model.pkl")
        # save the best recall classifier
        self.save_model_to_pkl_file(XGBClassifier(), "../data/training_RandomOverSampling.csv",
                                    "../model/recall_model.pkl")
        # save the fastest classifier
        self.save_model_to_pkl_file(DecisionTreeClassifier(), "../data/training_TomekLink.csv",
                                    "../model/fast_model.pkl")


mc = ModelComparison()
# mc.basic_comparison() # test set = 20% of data
# mc.basic_comparison(0.3,'../results/basics2.csv') # test set = 30% of data
# mc.basic_cross_validation_comparison()
# mc.basic_comparison_on_sample_data()
# mc.tuning_comparison_XGBoost("../data/training_SMOTETomek.csv", "../results/tuning_xgb_smote_tomek.csv")
# mc.tuning_comparison_XGBoost("../data/training_SMOTEENN.csv", "../results/tuning_xgb_smote_enn.csv")
# mc.tuning_comparison_XGBoost("../data/training_ENN.csv", "../results/tuning_xgb_enn.csv")
# mc.save_models()
# mc.roc_final_models()
# mc.prc_final_models()

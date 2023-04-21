from collections import Counter

import pandas as pd
from imblearn.combine import SMOTETomek, SMOTEENN
from imblearn.over_sampling import RandomOverSampler, SMOTE
from imblearn.under_sampling import RandomUnderSampler, TomekLinks, EditedNearestNeighbours

from app.utils.data_preprocessing import DataPreprocessing


class ModelSampling:
    """
    Based on preprocessed data
    Use multiple methods for sampling only the training data and save to csv file
    Also save testing data to csv file
    """

    def __init__(self):
        self.samplers = [RandomUnderSampler(), RandomOverSampler()]#, TomekLinks(), EditedNearestNeighbours(), SMOTE(),
                         #SMOTETomek(), SMOTEENN()]
        self.sampler_names = ['RandomUnderSampling', 'RandomOverSampling']#, 'TomekLink', 'ENN', 'SMOTE',
                              # 'SMOTETomek', 'SMOTEENN']
        self.data_preprocessing = DataPreprocessing()
        self.X_train, self.X_test, self.y_train, self.y_test = self.data_preprocessing.split_train_test_data()

    def save_set_to_csv(self, X_set, y_set, filename):
        """
        Combine X set and y set into a single DataFrame
        And save it to csv
        :param X_set: features set
        :param y_set: target label set
        :param filename: path of the csv file
        :return: -
        """
        df_combined_set = pd.concat([pd.DataFrame(X_set), pd.DataFrame(y_set)], axis=1)
        df_combined_set.columns = self.data_preprocessing.get_column_names()
        df_combined_set.to_csv(filename, index=False)

    def separate_sets_from_csv(self, filename):
        """
        Load data from the given csv file
        And separate it into feature set and target label set
        :param filename: path of the csv file
        :return: feature set and target label set
        """
        df_set = pd.read_csv(filename)
        X_set, y_set = self.data_preprocessing.partition_data(df_set)
        return X_set, y_set

    def save_testing_set(self):
        """
        Save testing set to csv file
        :return: -
        """
        self.save_set_to_csv(self.X_test, self.y_test, "../data/testing_data.csv")

    def sample_training_set(self):
        """
        Apply multiple sampling methods on the training data
        And save each resulting dataset into csv file
        :return: -
        """
        for sampler, name in zip(self.samplers, self.sampler_names):

            X_resampled, y_resampled = sampler.fit_resample(self.X_train, self.y_train)

            print(f"Sampling method: {name}")
            print(f"Original dataset shape: {Counter(self.y_train)}")
            print(f"Resampled dataset shape: {Counter(y_resampled)}")

            self.save_set_to_csv(X_resampled, y_resampled, f"../data/training_{name}.csv")

    def prepare_model_sampling(self):
        """
        Based on the original dataset
        Save X_test and y_test in a csv
        And for each sampling method used, save the new X_train and y_train in a csv
        :return: -
        """
        self.save_testing_set()
        self.sample_training_set()


import pandas as pd
import numpy as np
from sklearn.preprocessing import StandardScaler
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, confusion_matrix, average_precision_score
import matplotlib.pyplot as plt
from imblearn.over_sampling import SMOTE, RandomOverSampler
from imblearn.pipeline import Pipeline
from xgboost import XGBClassifier
import pickle
from faker import Faker
import random

# Setting to display all columns
pd.set_option('display.max_columns', None)

# Import data
class DataProcess:
    def __init__(self, filename='transactions_syn_tool.csv', user_filename='user_data.csv'):
        # Load the csv file
        self.df = pd.read_csv(filename)
        self.df_user = pd.read_csv(user_filename)

    def clean(self):
        # Rename original column headers to be more clear
        # self.df.rename(columns={'nameOrig':'ibanSource', 'oldbalanceOrg':'oldBalanceSource', 'newbalanceOrig':'newBalanceSource',
        #                     'nameDest':'ibanTarget', 'oldbalanceDest':'oldBalanceTarget', 'newbalanceDest':'newBalanceTarget'})

        # Check if there are any missing values
        print("Any null values?", self.df.isnull().values.any())

        # Remove insignificant 'isFlaggedFraud' column
        # axis=1 -> that we remove columns, not rows
        # inplace=True -> remove from original object
        self.df = self.df.loc[(self.df['type'] == 'TRANSFER') | (self.df['type'] == 'CASH_OUT')]
        #self.df.drop(['type', 'nameOrig', 'nameDest', 'isFlaggedFraud'], inplace=True, axis=1)
        self.df.drop(['type'], inplace=True, axis=1) # for synthetic data

        # drop some rows
        #self.df.drop(self.df.index[700000:2770409], inplace=True)

        # Display first 5 rows
        #print(self.df.head(5))

    def prep_data(self, col1, col2):
        """
        X: data columns (nameOrig, oldBalanceOrig, ...)
        y: target column (isFraud)
        """
        #X = self.df.iloc[:,0:6].values # for visualization
        #X = self.df.iloc[:,1:6].values # train and test without 'step' and 'isFraud' column
        X = self.df.iloc[:,col1:col2].values
        y = self.df["isFraud"].values
        return X,y

    def plot_data(self, X, y, title):
        # X is 2-D vector and y is 1-D vector?
        # X[y==0,0] -> finds all the rows of X that have a y value of 0
        # (y==0) and are in the first column of X (0)

        plt.title(title)
        plt.xlabel("newbalanceOrg")
        plt.ylabel("newbalanceDest")

        # plt.scatter(X[y==0,3], X[y==0,5], label="isFraud #0", alpha=0.5, linewidth=0.15)
        # plt.scatter(X[y==1,3], X[y==1,5], label="isFraud #1", alpha=0.5, linewidths=0.15,c='r')

        # syn data
        plt.scatter(X[y==0,2], X[y==0,4], label="isFraud #0", alpha=0.5, linewidth=0.15)
        plt.scatter(X[y==1,2], X[y==1,4], label="isFraud #1", alpha=0.5, linewidths=0.15,c='r')


        #plt.scatter(X[:,2],y)
        # return plt.show()

    def smote_oversampling(self, X, y):
        method = SMOTE()
        X_resampled, y_resampled = method.fit_resample(X,y)
        return X_resampled, y_resampled

    def visualize(self):
        occ_user = self.df_user[self.df_user['class'] == 1].value_counts()
        print("no of fraud -USER: ", len(occ_user))

        #print(self.df.info())
        occ = self.df[(self.df['isFraud'] == 1)].value_counts()
        fraudWBalanaceDestZero = self.df[(self.df['isFraud'] == 1) & (self.df['newbalanceDest'] == 0)]
        print("no of fraud: ", len(occ))
        print("ratio\n", occ/len(self.df.index))
        print("fraudWBalanaceDestZero\n", fraudWBalanaceDestZero)
        print("Amount Range: ", self.df["amount"].min(), " - ", self.df["amount"].max(),
              " and mean: ", self.df["amount"].mean())
        print("Old Org Balance Range: ", self.df["oldbalanceOrg"].min(), " - ", self.df["oldbalanceOrg"].max(),
              " and mean: ", self.df["oldbalanceOrg"].mean())
        print("New Org Balance Range: ",  self.df["newbalanceOrig"].min(), " - ", self.df["newbalanceOrig"].max(),
              " and mean: ", self.df["newbalanceOrig"].mean())
        print("Old Dest Balance Range: ",  self.df["oldbalanceDest"].min(), " - ", self.df["oldbalanceDest"].max(),
              " and mean: ", self.df["oldbalanceDest"].mean())
        print("New Dest Balance Range: ",  self.df["newbalanceDest"].min(), " - ", self.df["newbalanceDest"].max(),
              " and mean: ", self.df["newbalanceDest"].mean())

        #X, y = self.prep_data(0,6)
        X, y = self.prep_data(0,5) # syn data
        X_resampled, y_resampled = self.smote_oversampling(X, y)
        #plt.subplot(1,2,1)
        self.plot_data(X,y,"Original Set")
        #plt.subplot(1,2,2)
        #self.plot_data(X_resampled,y_resampled,"SMOTE")

        plt.legend()
        return plt.show()

    def classification(self, model):
        #X, y = self.prep_data(1,6)
        X, y = self.prep_data(0,5) # for synthetic data
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=50)

        # test_data = pd.read_csv("transactions_synthetic.csv") # synthetic data test
        # X_test = test_data.iloc[:, 0:6].values
        # y_test = test_data["isFraud"].values

        weights = (y == 0).sum() / (1.0 * (y == 1).sum())
        model = XGBClassifier(max_depth=3,scale_pos_weight=weights,n_jobs=4)

        clf = model.fit(X_train,y_train)
        probabilities = clf.predict_proba(X_test)
        predicted = model.predict(X_test)
        print("report:\n", classification_report(y_test,predicted))
        conf_mat = confusion_matrix(y_true = y_test, y_pred = predicted)
        print("confMatrx:\n", conf_mat)
        print("AUPRC: ", average_precision_score(y_test, probabilities[:,1]))
        print("score: ", clf.score(X_test,y_test))
        print("k-fold cross validation: ", cross_val_score(clf,X,y,cv=5))

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

    def syntheticData(self):
        random.seed(44)
        fake = Faker()
        n_transactions = 1000
        transactions = []
        for i in range(n_transactions):
            step = random.randrange(1,799)
            if random.random() < 0.05: # 5% of transactions are fraudulent
                #type = random.choice(["TRANSFER","CASH_OUT"])
                isFraud = 1
                org_old_balance = np.random.normal(loc=5000, scale=1000)
                amount = np.random.normal(loc=5000, scale=1000)
                z = random.choices(["random", "zero"], weights=(70, 30), k=1)
                if z[0] == 0:
                    org_new_balance = 0
                else:
                    org_new_balance = org_old_balance - amount if org_old_balance > amount else 0
                z = random.choices(["random", "zero"], weights=(70, 30), k=1)
                dest_old_balance = np.random.normal(loc=5000, scale=1000)
                if z[0] == 0:
                    dest_new_balance = 0
                else:
                    dest_new_balance = dest_old_balance + amount if random.random() > 0.25 else 0
            else:
                #type = random.choice(["TRANSFER","CASH_OUT","CASH_IN","PAYMENT"])
                isFraud = 0
                amount = np.random.normal(loc=50, scale=10)
                org_old_balance = np.random.normal(loc=50, scale=10)
                org_new_balance = org_old_balance - amount if org_old_balance > amount else 0
                dest_old_balance = np.random.normal(loc=50, scale=10)
                dest_new_balance = dest_old_balance + amount
            transaction_data = {"step": step, "amount": amount, "oldbalanceOrg": org_old_balance, "newbalanceOrig":
                org_new_balance, "oldbalanceDest": dest_old_balance, "newbalanceDest": dest_new_balance, "isFraud": isFraud}
            transactions.append(transaction_data)
        transaction_df = pd.DataFrame(transactions)
        transaction_df.to_csv('transactions_synthetic.csv', index=False)


data = DataProcess()
#data.syntheticData()
data.clean()
data.visualize()
print("Logistic Regression")
#data.classification(None)
print("Logistic Regression with SMOTE")
# resampling = SMOTE()
# model = RandomForestClassifier()
# pipeline = Pipeline([('SMOTE', resampling), ('Random Forest Classifier', model)])
# data.classification(pipeline)
#data.createModel()

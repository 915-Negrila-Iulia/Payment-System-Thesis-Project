from sklearn.metrics import make_scorer, accuracy_score, precision_score, recall_score, f1_score, \
    average_precision_score, roc_auc_score
from sklearn.model_selection import GridSearchCV


class ModelTuning:

    def __init__(self, model, param_grid, cv=3):
        self.model = model
        self.param_grid = param_grid
        self.cv = cv
        self.scoring = {'accuracy': make_scorer(accuracy_score), 'precision': make_scorer(precision_score),
                   'recall': make_scorer(recall_score), 'f1': make_scorer(f1_score),
                   'average_precision': make_scorer(average_precision_score), 'roc_auc': make_scorer(roc_auc_score)}

    def tune(self, X_train, y_train):
        grid_search = GridSearchCV(self.model, param_grid=self.param_grid, cv=5, scoring=self.scoring, refit=False,
                                   n_jobs=-1)
        grid_search.fit(X_train, y_train)
        return grid_search.cv_results_

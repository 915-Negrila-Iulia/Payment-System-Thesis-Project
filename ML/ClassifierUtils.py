from imblearn.pipeline import Pipeline, make_pipeline


class ClassifierUtils:
    def __init__(self, model, param_grid):
        self.model = model
        self.param_grid = param_grid

    def get_model(self):
        return self.model

    def get_param_grid(self):
        return self.param_grid

    def __str__(self):
        return str(self.model)
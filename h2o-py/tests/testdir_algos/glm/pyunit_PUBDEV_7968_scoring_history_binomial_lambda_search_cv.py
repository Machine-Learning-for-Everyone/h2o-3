from builtins import range
import sys
sys.path.insert(1,"../../../")
import h2o
from tests import pyunit_utils
from h2o.estimators.glm import H2OGeneralizedLinearEstimator as glm

# Verify scoring history generation for binomial with IRLSM, lambda_search, cross-validation and validation dataset
def testGLMBinomialScoringHistoryLambdaSearch():
    h2o_data = h2o.import_file(path=pyunit_utils.locate("smalldata/glm_test/binomial_20_cols_10KRows.csv"))
    for ind in range(10):
        h2o_data[ind] = h2o_data[ind].asfactor()
    h2o_data["C21"] = h2o_data["C21"].asfactor()
    splits_frames = h2o_data.split_frame(ratios=[.8], seed=1234)
    train = splits_frames[0]
    valid = splits_frames[1]
    Y = "C21"
    X = list(range(0,20))

    print("Building model with score_interval=1 and lambda search on.  Should generate same model as "
          "score_each_iteration turned on.")
    h2o_model = glm(family="binomial", score_iteration_interval=1, lambda_search=True, nlambdas=10, 
                    generate_scoring_history=True)
    h2o_model.train(x=X, y=Y, training_frame=train, validation_frame=valid)
    print("Building model with score_each_iteration turned on, with lambda search.")
    h2o_model_score_each = glm(family="binomial", score_each_iteration=True, lambda_search=True, nlambdas=10, 
                               generate_scoring_history=True)
    h2o_model_score_each.train(x=X, y=Y, training_frame=train, validation_frame=valid)

    print("Building model with score_each_iteration turned on, with lambda search and CV.")
    h2o_model_score_each_cv = glm(family="binomial", score_each_iteration=True, lambda_search=True, nlambdas=10,
                                  nfolds=2, fold_assignment='modulo', generate_scoring_history=True)
    h2o_model_score_each_cv.train(x=X, y=Y, training_frame=train, validation_frame=valid)
    print("Building model with score_interval=1, lambda search on and CV.  Should generate same model as "
          "score_each_iteration turned on, with lambda search and CV.")
    h2o_model_cv = glm(family="binomial", score_iteration_interval=1, lambda_search=True, nlambdas=10, nfolds=2,
                       fold_assignment='modulo', generate_scoring_history=True)
    h2o_model_cv.train(x=X, y=Y, training_frame=train, validation_frame=valid)
    print("Done")

if __name__ == "__main__":
    pyunit_utils.standalone_test(testGLMBinomialScoringHistoryLambdaSearch)
else:
    testGLMBinomialScoringHistoryLambdaSearch()

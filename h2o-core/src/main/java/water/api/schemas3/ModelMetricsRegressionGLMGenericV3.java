package water.api.schemas3;

import hex.ModelMetricsRegressionGLMGeneric;
import water.api.API;
import water.api.API.Direction;

/**
 *
 */
public class ModelMetricsRegressionGLMGenericV3 extends ModelMetricsRegressionV3<ModelMetricsRegressionGLMGeneric, ModelMetricsRegressionGLMGenericV3> {

  @API(help = "residual deviance", direction = Direction.OUTPUT)
  public double residual_deviance;

  @API(help = "null deviance", direction = Direction.OUTPUT)
  public double null_deviance;

  @API(help = "AIC", direction = Direction.OUTPUT)
  public double AIC;

  @API(help = "null DOF", direction = Direction.OUTPUT)
  public long null_degrees_of_freedom;

  @API(help = "residual DOF", direction = Direction.OUTPUT)
  public long residual_degrees_of_freedom;


  @Override
  public ModelMetricsRegressionGLMGenericV3 fillFromImpl(ModelMetricsRegressionGLMGeneric modelMetrics) {
    super.fillFromImpl(modelMetrics);
    this.AIC = modelMetrics._AIC;
    this.residual_deviance = modelMetrics._resDev;
    this.null_deviance = modelMetrics._nullDev;
    this.null_degrees_of_freedom = modelMetrics._nullDegressOfFreedom;
    this.residual_degrees_of_freedom = modelMetrics._residualDegressOfFreedom;
    return this;
  }

}

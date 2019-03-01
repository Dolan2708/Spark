import org.apache.spark.SparkConf;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaPairRDD;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;

import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.classification.SVMWithSGD;

import org.apache.spark.mllib.regression.LabeledPoint;

import org.apache.spark.mllib.linalg.Vectors;

import scala.Tuple2;

public class Classifier
{
	public static void main(String[] args)
	{
		// args[0] input path to data csv
		// args[1] output path in order to save model (optional)

		JavaSparkContext sc = new JavaSparkContext( 
			new SparkConf().setAppName("Image Gender Classifier")//.setMaster("local")
		);

		System.out.println("Reading trainging data...");
		JavaRDD data = sc.textFile( args[0] )
			.distinct()
			.cache()
			.map(
				new Function<String, LabeledPoint> ()
				{
					public LabeledPoint call(String v)
					{
						double label = Double.parseDouble(
							v.substring( 0, v.indexOf(",") )
						);
						String[] featureStr = v.substring( v.indexOf(",") + 1 )
							.trim().split(" ");
						double[] feature = new double[ featureStr.length ];
						int i = 0;
						for ( String str : featureStr )
						{
							if ( str.contains(",") )
							{
								feature[i] = Double.parseDouble( 
									str.trim().split(",")[1]
								);
							}
							else
							{
								feature[i] = Double.parseDouble( str.trim() );
							}
							++i;
						}
						return new LabeledPoint(label, Vectors.dense(feature));
					}
				}
		);
		System.out.println( "rows: " + data.count() );

		JavaRDD[] splits = data.randomSplit( 
			new double[]{0.7, 0.3} 
		);
		JavaRDD training = splits[0];
		JavaRDD testing = splits[1];

		System.out.println("Training model...");
		int numIterations = 100;
		SVMModel model = SVMWithSGD.train(
			training.rdd(), 
			numIterations
		);

		if ( args.length > 1 )
		{
			System.out.println("Saving model...");
			model.toPMML(
				JavaSparkContext.toSparkContext(sc), 
				args[1]
			);
		}

		System.out.println("Making predictions...");
		JavaPairRDD<Double, Double> prediction = testing.mapToPair(
			new PairFunction<LabeledPoint, Double, Double> ()
			{
				public Tuple2<Double, Double> call(LabeledPoint labeledPoint)
				{
					return new Tuple2<Double, Double>(
						model.predict(
							labeledPoint.features()
						),
						labeledPoint.label()
					);
				}
			}
		);
		
		System.out.println("Calculating accuracy...");
		double accuracy = 1.0 * prediction.filter(
			new Function<Tuple2<Double, Double>, Boolean> ()
			{
				public Boolean call(Tuple2<Double, Double> v)
				{
					return v._1().intValue() == v._2().intValue();
				}
			}
		).count() / (double)testing.count();
		System.out.println("Accuracy: " + accuracy);
	}
}

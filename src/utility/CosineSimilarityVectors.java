package utility;

public class CosineSimilarityVectors {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//double cosineValue=cosineSimilarityVector({1,2,3},{1,4,5});
		CosineSimilarityVectors obj=new CosineSimilarityVectors();
		double[] v1={11.0, 22.0, 3.0};
		double[] v2={1.0,2.0,6.0};
		
		System.out.println(obj.getCosineSimilarityVector(v1,v2));
	}
	
	public static double getCosineSimilarityVector(double[] vectorA, double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
}

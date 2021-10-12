public class Main {

	public static void main(String[] args) {
//		double[][] matrix = new double[][]{{1, 2}};
//		double[] vec = new double[] {1, 1};
//
//		System.out.println(Arrays.toString(Util.mul(matrix, vec)));

		NetworkMatrices network = new NetworkMatrices(2, 1, 4);

		double[][] input = new double[][] {{1, 1}, {1, 0}, {0, 1}, {0, 0}};
		double[][] labels = new double[][] {{0}, {1}, {1}, {0}};

		network.train(input, labels, 100000, 0.1);
	}
}

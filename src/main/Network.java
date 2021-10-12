package main;

public class Network implements INetwork {
	Neuron[] outputLayer;
	Neuron[][] hiddenLayers;

	public Network(int numInUnit, int numOutUnit, double[][][] weights, int... numHidUnit) {
		if(numHidUnit.length != weights.length - 1) {throw new IllegalArgumentException("Illegal weight matrices");}

		outputLayer = new Neuron[numOutUnit];
		hiddenLayers = new Neuron[numHidUnit.length][];

		// Hidden layers
		for(int i = 0; i < hiddenLayers.length; i++) {
			hiddenLayers[i] = new Neuron[numHidUnit[i]];

			for(int j = 0; j < numHidUnit[i]; j++)
				hiddenLayers[i][j] = new Neuron(weights[i][j]);
		}

		// Output layer
		for(int i = 0; i < numOutUnit; i++)
			outputLayer[i] = new Neuron(weights[weights.length - 1][i]);
	}

	public Network(int numInUnit, int numOutUnit, int... numHidUnit) {
		outputLayer = new Neuron[numOutUnit];
		hiddenLayers = new Neuron[numHidUnit.length][];

		// Hidden layers
		for(int i = 0; i < hiddenLayers.length; i++) {
			hiddenLayers[i] = new Neuron[numHidUnit[i]];

			for(int j = 0; j < numHidUnit[i]; j++)
				hiddenLayers[i][j] = new Neuron(Util.random(i == 0 ? numInUnit : numHidUnit[i - 1]));
		}

		// Output layer
		for(int i = 0; i < numOutUnit; i++)
			outputLayer[i] = new Neuron(Util.random(numHidUnit.length == 0 ? numInUnit : numHidUnit[numHidUnit.length - 1]));
	}

	@Override
	public double[] compute(double[] input) {
		double[] output = new double[outputLayer.length];
		double[][] hidden = new double[hiddenLayers.length][];

		forwardPropagation(input, hidden, output);

		return output;
	}

	@Override
	public void train(double[][] input, double[][] labels, int repetitions, double learnRate){
		double[] output = new double[outputLayer.length];
		double[][] hidden = new double[hiddenLayers.length][];

		for(int i = 0; i < repetitions; i++) {
			double cost = 0d;
			int correct = 0;
			System.out.println("Repetition " + i);
			for(int j = 0; j < input.length; j++) {
				forwardPropagation(input[j], hidden, output);
				cost += cost(output, labels[j]);
				correct += correct(output, labels[j]) ? 1 : 0;
				backpropagation(output, hidden, labels[j], learnRate);
			}
			System.out.printf("Cost: %f\nCorrect: %d of %d\n\n", cost, correct, input.length);
		}
	}

	private void forwardPropagation(double[] input, double[][] hidden, double[] output) {
		for(int i = 0; i < hiddenLayers.length; i++) // Initialize second layer of hidden matrix.
			hidden[i] = new double[hiddenLayers[i].length];

		// Hidden layers
		for(int i = 0; i < hiddenLayers.length; i++)
			for(int j = 0; j < hiddenLayers[i].length; j++)
				hidden[i][j] = hiddenLayers[i][j].fire(j == 0 ? input : hidden[i - 1]);

		// Output layer
		for(int i = 0; i < outputLayer.length; i++)
			output[i] = outputLayer[i].fire(hiddenLayers.length == 0 ? input : hidden[hidden.length - 1]);
	}

	private void backpropagation(double[] output, double[][] hidden, double[] label, double learnRate) {
		double[][] delta = new double[hiddenLayers.length + 1][];
		delta[0] = new double[output.length];
		for(int i = 0; i < outputLayer.length; i++) {
			delta[0][i] = 2 * (output[i] - label[i]);
			double deltaLearn = -learnRate * delta[0][i];

			for(int j = 0; j < outputLayer[i].weights.length; j++)
				outputLayer[i].weights[j] += deltaLearn * output[i];

			outputLayer[i].bias += deltaLearn;
		}

		for(int i = hiddenLayers.length - 2; i >= 0; i--)
			for(int j = 0; j < hiddenLayers[i].length; j++) {
				delta[j + 1][i] = 2 * (output[i] - label[i]);
				double deltaLearn = -learnRate * delta[j + 1][i];

				for(int k = 0; k < hiddenLayers[i][j].weights.length; k++)
					hiddenLayers[i][j].weights[k] += deltaLearn * hidden[i][j];

				hiddenLayers[i][j].bias += deltaLearn;
			}
	}

	private double cost(double[] output, double[] label) {
		double sum = 0d;
		for(int i = 0; i < output.length; i++) {
			double diff = output[i] - label[i];
			sum += diff * diff;
		}

		return sum;
	}

	private boolean correct(double[] output, double[] label) {
		return Util.argmax(output) == Util.argmax(label);
	}
}

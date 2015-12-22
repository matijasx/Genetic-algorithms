import java.util.Random;

/**
	@author Matiss Baldonis
	@version 1.0, 6 Dec 2015
 */

public class Init {
	static boolean usingIDE = false;
	
	static final String TARGET = "ONE GENERAL LAW, LEADING TO THE ADVANCEMENT OF ALL ORGANIC BEINGS, NAMELY, MULTIPLY, VARY, LET THE STRONGEST LIVE AND THE WEAKEST DIE. - CHARLES DARWIN";

	static char[] alphabet = new char[30];

	static double startingTime = System.currentTimeMillis();

	
	
	public static void main(String[] args)  throws InterruptedException  {
		int popSize = 1000;
		for (char c = 'A'; c <= 'Z'; c++) {
			alphabet[c - 'A'] = c;
		}
		alphabet[26] = ' ';
		alphabet[27] = '-';
		alphabet[28] = ',';
		alphabet[29] = '.';

		Random generator = new Random(System.currentTimeMillis());
		Individual[] population = new Individual[popSize];

		// Initialize the population with random characters
		for (int i = 0; i < popSize; i++) {
			char[] tempChromosome = new char[TARGET.length()];
			for (int j = 0; j < TARGET.length(); j++) {
				tempChromosome[j] = alphabet[generator.nextInt(alphabet.length)]; //choose a random letter in the alphabet
			}
			population[i] = new Individual(tempChromosome);
		}
		// Start the evolution algorithm on the population
		new GenePoolAlg(population);
		
		if(!(usingIDE)) Thread.sleep(1000000);

		System.out.println("Running time: " + (System.currentTimeMillis() - startingTime) + " ms");
	}
}

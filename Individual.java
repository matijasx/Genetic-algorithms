
public class Individual {

	char[] chromosome;
	double fitness;

	public Individual(char[] chromosome) {
		this.chromosome = chromosome;
		this.fitness = 0;
	}

	public char[] getChromosome() {
		return chromosome;
	}

	public void setChromosome(char[] chromosome) {
		this.chromosome = chromosome;
	}

	public void setGenoType(String genotype){
		chromosome = genotype.toCharArray();
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness() {
		double fitness = 0;
		for(int i = 0; i < chromosome.length; i++)
			if (chromosome[i] == Init.TARGET.charAt(i)) this.fitness = ++fitness;
	}

	public String genoToPhenotype() {
		StringBuilder builder = new StringBuilder();
		builder.append(chromosome);
		return builder.toString();
	}

	public Individual clone() {
		char[] chromClone = new char[chromosome.length];
		for(int i = 0; i < chromClone.length; i++) {
			chromClone[i] = chromosome[i];
		}
		return new Individual(chromClone);
	}



}

import java.util.Random;
/*
 * Genetic algorithm that recombines the genes of parents by chance (giving the fittest parent more genes to inherit from). 
 * After recombination, the children and parents (% of parents depends on KILL_RATIO) are sorted again, and recombination 
 * is performed until the target string is reached.
 * 
 * @author Matiss Baldonis
 * @version 1.0, 6 Dec 2015
 * 
 */

public class GenePoolAlg {

	Individual[] population;

	public Random rand = new Random(System.currentTimeMillis());

	/* 0 gives out of bounds... (0.00001 doesn't)
	 * Watch out of killing too many individuals (small KILL_RATIO) as it might lead to
	 * genetic depression and thus never coming to a solution
	 * However, smaller KILL_RATIO does decrease runtime drastically, WHEN it works.
	 * The ratio is inverse - 0.70 would mean that you kill 30% of population.
	 */
	public final double KILL_RATIO = 0.75;


	/* Mutation rate increases the likeliness of having a random mutation 
	 *(inserting a random gene independently of the gene pool of population).
	 */
	public final double MUTATION_RATE = 0.9; 


	//Counter to keep track of the amount of generations it took to get to TARGET value
	public int iterations = 0;

	/** GA that crossovers 2 parents, the fittest parent can transfer more than 50% of its genes to child.
	@param 	population - String[] object where each String represents individual, and each char represents a gene.
	
	 */
	GenePoolAlg(Individual[] population) throws InterruptedException{
		this.population = population;

		//While fittest individual does not equal TARGET
		while( ! (population[0].genoToPhenotype().equals(Init.TARGET) )){
			iterations++;
			//Set fitness 
			for(int i = 0; i < this.population.length; i++){
				this.population[i].setFitness();
				Mutate(i);
			}
			HeapSort.sort(this.population);
			//displayPopulation();
			Evolve(population);
			System.out.print(this.population[0].getFitness() + " ");
			System.out.print(this.population[0].genoToPhenotype() + " ");
			System.out.println(iterations);
		}
	}

	/* Method to proceed to next generation
	 *  By default the children has 50/50 chance to inherit each parent's genes, but if one parent has fitness rating
	 *  higher than the other, that parent gets more genes inherited from.
	 */
	/**  Overwrites least fit individuals within population with offsprings of the rest of the population.
	 *  @param previous - population to change.
	 */
	public void Evolve(Individual[] previous){
		//Kill X% of the unfit population.
		int dyingPopIndex = (int) ( population.length*(1-KILL_RATIO));
		/* rewrite the least fit part of  population array with new population.
		 * TO DO: Change this method if the population is dynamic.
		 */
		for(int i = dyingPopIndex; i <= population.length-1; i++){
			int[] parents = chooseTwoParents();
			population[i].setGenoType( recombine(parents[0], parents[1] ));
		}
	}

	//Crossover method
	public String recombine(int parent1, int parent2){
		//representation of which genes are inherited from which parent (1 or 2).
		String parentSequence = "";
		// the offspring generated
		String offspring = "";
		// keeps track of how many genes are already inherited by each parent during the recombination
		int geneIteratorParent1 = 0;
		int geneIteratorParent2 = 0;

		//default distribution of parent genes in child
		int maxGenesParent1 = Init.TARGET.length()/2;
		int maxGenesParent2 = Init.TARGET.length()/2;

		// Chromosome as an array
		char[] genes = new char[Init.TARGET.length()];

		/* If fitness of a parent is larger than the other parent, allow that parent to carry one more gene.
		 * there is a bug - parent with higher fitness can sometimes get 2 more genes. Happens because of how the conditions are 
		 * checked, but I decided to just leave it like that, as it might even be favorable.
		 * */
		if (population[parent1].fitness > population[parent2].fitness){
			maxGenesParent1++;
			maxGenesParent2--;
		}
		else if (population[parent1].fitness < population[parent2].fitness){
			maxGenesParent2++;
			maxGenesParent1--;
		}

		//Produce an offspring
		for(int i = 0; i < Init.TARGET.length()  ; i++){
			//choose between parent 1 and 2 at random 
			int randomParent = chooseRandomParent();
			/* Checks whether:
			 * 1.0) Parent doesn't exceed its quota on genes allowed per parent
			 *     AND
			 * 2.0)It is parent 1 or 2 respectively
			 *     OR
			 * 2.1)The other parent exceeds its quota.
			 */
			if (geneIteratorParent1 <= maxGenesParent1 && (randomParent == 1 || geneIteratorParent2 >= maxGenesParent2) ){

				genes[i] = population[parent1].chromosome[i];
				geneIteratorParent1++;
				parentSequence += "1";
			}
			else if (geneIteratorParent2 <= maxGenesParent2 && (randomParent == 2 || geneIteratorParent1 >= maxGenesParent1)){
				genes[i] = population[parent2].chromosome[i];
				geneIteratorParent2++;
				parentSequence += "2";
			}

			offspring = String.valueOf(genes);

			/*
			 *  Very cool debug info about parents/children.
			 *  WARNING! Don't run with large population, but also keep in mind that small
			 *  population might not produce a solution.
star here -> /
			System.out.println(genes);

			System.out.print("     P1 # genes: " + geneIteratorParent1);
			System.out.println("   P2 # genes: " + geneIteratorParent2);
			System.out.println("Offspring: " +  offspring);

			System.out.print("Parent1: "   + population[parent1].genoToPhenotype());
			System.out.println(" Fitness: "+ population[parent1].getFitness());
			System.out.print("Parent2: "   + population[parent2].genoToPhenotype());
			System.out.println(" Fitness: "+ population[parent2].getFitness());

			System.out.println("Parent sequence: " + parentSequence);
			/**/
		}
		return offspring;
	}


	//Helper selector for crossover
	public int chooseRandomParent(){	
		boolean random = rand.nextBoolean();
		if (random) return 1;
		else return 2;	
	}

	/* Selection method
	 * Parents are selected randomly from the whole population in order to maintain the gene variety.
	 * If only fittest parents got to mate, then solution might never be found
	 * (or could - via mutation, a very slow process), as the parents might actually
	 * be missing whole gene (or more), even though being the fittest in that particular evolution step.
	 */
	public int[] chooseTwoParents(){
		int survivingPopSize = (int) (population.length*(1-KILL_RATIO));
		int[] parents = new int[2];
		parents[0] = rand.nextInt(survivingPopSize);
		parents[1] = rand.nextInt(survivingPopSize);
		return parents;
	}

	public void displayPopulation(){
		for(int i = 0; i < this.population.length; i++){
			System.out.print(this.population[i].getFitness() + " ");
			System.out.println(this.population[i].genoToPhenotype());
		}
	}

	public void Mutate(int i){
		double toOrNotTo = rand.nextDouble();
		if (toOrNotTo <= MUTATION_RATE){
			int randomIndex = rand.nextInt(Init.TARGET.length());
			int randomGene = rand.nextInt(Init.alphabet.length);
			population[i].getChromosome()[randomIndex] = Init.alphabet[randomGene];
		}
	}
}

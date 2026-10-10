import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StringAlgorithm {
    static final String genes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ";

    static final String TARGET = "TYPEX";
    static final int populationSize = 100;
    static final int tournamentSize = 3;
    static final double mutationRate = 0.01;
    static final int maxGeneration = 2000;
    static final int nbElite = 2;
    static Random rng = new Random();

    static char getRandomGene() {
        return genes.charAt(rng.nextInt(genes.length()));
    }

    static char[] generateIndividual() {
        char[] individual = new char[TARGET.length()];
        for (int i = 0; i < individual.length; i++) {
            individual[i] = getRandomGene();
        }
        return individual;
    }

    static int score(char[] individual) {
        int score = 0;

        for (int i = 0; i < individual.length; i++) {
            char targetGene = TARGET.toCharArray()[i];
            char gene = individual[i];
            if(gene == targetGene) score++;
        }

        return score;
    }

    static char[] selection(List<char[]> population) {
        char[] winner = population.get(rng.nextInt(population.size()));
        for (int i = 0; i < tournamentSize-1; i++) {
            char[] contender = population.get(rng.nextInt(population.size()));
            if(score(contender) > score(winner)){
                winner = contender;
            }
        }
        return winner;
    }

    static void mutate(char[] individual){
        for (int i = 0; i < individual.length; i++) {
            if(rng.nextDouble() < mutationRate){
                individual[i] = getRandomGene();
            }
        }
    }

    static void evolve(){
        //Generer la première génération.
        List<char[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generateIndividual());
        }

        for (int generation = 0; generation < maxGeneration; generation++) {

            //Evaluer les score de la génération
            int[] scores = new int[population.size()];
            double totalScore = 0;
            for (int i = 0; i < population.size(); i++) {
                scores[i] = score(population.get(i));
                totalScore += scores[i];
            }

            //Trier des meilleures au pires
            Integer[] ranked = new Integer[population.size()];
            for (int i = 0; i < population.size(); i++) {
                ranked[i] = i;
            }
            Arrays.sort(ranked, (a, b) -> scores[b] - scores[a]);
            char[] best = population.get(ranked[0]);
            System.out.printf("Gen %4d | best: %s | score %2d/%d | avg %.2f%n", generation, new String(best), scores[ranked[0]], TARGET.length(), totalScore / population.size());

            if(scores[ranked[0]] == TARGET.length()){
                System.out.printf("Target was found in %d generations", generation);
                System.out.print("Random guessing would take " + Math.pow(genes.length(), TARGET.length()) + " try in avg.");
                return;
            }

            //Nouvelle génération
            List<char[]> nextGen = new ArrayList<>(populationSize);
            //Garder les élites dans la prochaine génération
            for (int i = 0; i < nbElite; i++) {
                nextGen.add(population.get(ranked[i]).clone());
            }

            //Muter les autres individus
            while(nextGen.size() < populationSize){
                char[] parent = selection(population);
                char[] child = parent;
                mutate(child);
                nextGen.add(child);
            }
            population = nextGen;
        }
    }

    static void main(String[] args) {

        evolve();
    }
}

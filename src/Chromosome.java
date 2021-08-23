import java.util.ArrayList;
import java.util.Random;

public class Chromosome {
    ArrayList<Double> genes;
    public double chromosomeFitness;

    public Chromosome(){
        genes = new ArrayList<>();
        chromosomeFitness = 0;
    }

    public double getChromosomeFitness() {
        return chromosomeFitness;
    }
    public void correct(ArrayList<MarketingChannel> marketingChannels){
        double budget=0;
        while(true) {
            budget = this.getTotalBudget();
            if (budget > 100) {
                    int index = new Random().nextInt(genes.size());
                    this.genes.set(index,marketingChannels.get(index).lB);
                }

            else if (budget <= 100){ break; }
        }
    }
    public double getTotalBudget(){
        double sum = 0;
        for(int i = 0; i < genes.size(); i++){
            sum += this.genes.get(i);
        }
       return sum;


    }

}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MBA {
    public ArrayList<MarketingChannel> marketingChannels;
    public ArrayList<Chromosome> chromosomes, matingPool, tempChromosomes, experments;
    public double pOfCrossOver, pOfMutation;
    public int populationSize, marketingBudget, nMarketingChannels, currentGeneration, numberOfGenerations, matingPoolSize ,typeOfMutation;
    /* -------------------------------------------------------------------------------------------------------------- */
    //Constructors
    public MBA(){
        marketingBudget =0;
        nMarketingChannels = 0;
        marketingChannels = new ArrayList<>();
        experments=new ArrayList<>();
        chromosomes = new ArrayList<>();
        tempChromosomes = new ArrayList<>();
        matingPool = new ArrayList<>();
        pOfMutation = 0.5;
        pOfCrossOver = 0.05;
        currentGeneration = 0;
        numberOfGenerations = 100;
        populationSize = 100;
        matingPoolSize = new Random().nextInt((populationSize - (populationSize / 2))) + (populationSize/2);
        if(matingPoolSize % 2 != 0) matingPoolSize += 1;
    }
    /* ---------------------------------------------------------------------------------------------- */
    public void startProgram(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the marketing budget (in thousands): ");
        this.marketingBudget = scan.nextInt();
        System.out.print("\n");
        System.out.println("Enter the number of marketing channels: ");
        this.nMarketingChannels = scan.nextInt();
        System.out.print("\n");
        System.out.println("Enter the name and ROI (in %) of each channel separated by space:");
        for(int i = 0; i < nMarketingChannels; i++){
            MarketingChannel marketingChannel = new MarketingChannel();
            marketingChannel.name = scan.next();
            marketingChannel.ROI = scan.nextInt();
            marketingChannels.add(marketingChannel);
        }
        System.out.print("\n");
        System.out.println("Enter the lower (k) and upper bounds (%) of investment in each channel: (enter x if there is no bound)");
        for(int i = 0; i < nMarketingChannels; i++){
            String str1 = scan.next();
            if(str1.equals("x")) marketingChannels.get(i).lB = 0.0;
            else marketingChannels.get(i).lB = Double.parseDouble(str1);

            String str2 = scan.next();
            if(str2.equals("x")) marketingChannels.get(i).uB = new Random().nextDouble() * (marketingBudget - marketingChannels.get(i).lB) + marketingChannels.get(i).lB;
            else marketingChannels.get(i).uB = (Double.parseDouble(str2)/100) * marketingBudget;
        }

    }
    /* ---------------------------------------------------------------------------------------------- */
    public void runProgram() throws InterruptedException {
        tournamentSelection();
        crossover();
        if(typeOfMutation==1) {
            uniformMutation(matingPool);
        }
        else{
            nonUniformMutation(matingPool);
        }
        correctAfterMutation(matingPool);
        fitnessEvaluation(matingPool);
        replacement();
    }
    /* ---------------------------------------------------------------------------------------------- */
    public void populationInit(){
        for(int j = 0; j < populationSize; j++) {
            Chromosome chromosome = new Chromosome();
            for (int i = 0; i < nMarketingChannels; i++) {
                double gene = new Random().nextDouble() * (marketingChannels.get(i).uB - marketingChannels.get(i).lB) + marketingChannels.get(i).lB;
                chromosome.genes.add(gene);
            }
            if(uniqueChromosome(chromosomes, chromosome) && checkFeasibilityBudget(chromosome)){
                chromosomes.add(chromosome);
            }
            else if(uniqueChromosome(chromosomes, chromosome) && !checkFeasibilityBudget(chromosome)){
                //j--;
                chromosome.correct(marketingChannels);
                chromosomes.add(chromosome);
            }
            else{
                j--;
            }

        }
        fitnessEvaluation(chromosomes);
    }
    /* ---------------------------------------------------------------------------------------------- */
    public boolean uniqueChromosome(ArrayList<Chromosome> tmp, Chromosome tempChromosome){
        if(tmp.size() == 0){
            return true;
        }
        if(tmp.contains(tempChromosome)){
            return false;
        }
        return true;
    }
    /* ---------------------------------------------------------------------------------------------- */
    public boolean checkFeasibilityBound(int index, double value){
        if((marketingChannels.get(index).lB > value) || (marketingChannels.get(index).uB < value)){
            return false;
        }
        return true;
    }
    /* ---------------------------------------------------------------------------------------------- */
    public boolean checkFeasibilityBudget(Chromosome tmp){
        double sum = 0;
        for(int i = 0; i < tmp.genes.size(); i++){
            sum += tmp.genes.get(i);
        }
        if(sum > marketingBudget)
            return false;
        return true;
    }
    /* ---------------------------------------------------------------------------------------------- */
    public void fitnessEvaluation(ArrayList<Chromosome> temp){
        for(int i = 0; i < temp.size(); i++){
            temp.get(i).chromosomeFitness=0;
            for(int j = 0; j < temp.get(i).genes.size(); j++){
                temp.get(i).chromosomeFitness += ((temp.get(i).genes.get(j)) * (marketingChannels.get(j).ROI / 100));
            }
        }
    }
    /* ---------------------------------------------------------------------------------------------- */
    public ArrayList<Chromosome> tournamentSelection(){
        if(matingPool.size() == matingPoolSize)
            return matingPool;
        ArrayList<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < matingPoolSize; i++){
            indexes.add(new Random().nextInt(chromosomes.size()));
        }
        ArrayList<Chromosome> chromosomesArrayList = new ArrayList<>();
        for ( int y = 0; y < indexes.size(); y++) {
            chromosomesArrayList.add(chromosomes.get(indexes.get(y)));
        }
        int index = getFittest(chromosomesArrayList);
        matingPool.add(chromosomesArrayList.get(index));
        tempChromosomes.add(chromosomesArrayList.get(index));
        chromosomes.remove(chromosomesArrayList.get(index));
        return tournamentSelection();
    }
    /* ---------------------------------------------------------------------------------------------- */
    public int getFittest(ArrayList<Chromosome> tmp) {
        double max = tmp.get(0).chromosomeFitness;
        int index = 0;
        for(int i = 1; i < tmp.size(); i++){
            if(max < tmp.get(i).chromosomeFitness){
                max = tmp.get(i).chromosomeFitness;
                index = i;
            }
        }
        return index;
    }
    /*-----------------------------------------------------------------------------------------------*/
    /* ---------------------------------------------------------------------------------------------- */
    public void crossover(){
        double pCompare = new Random().nextDouble();
        if(pCompare > pOfCrossOver){
            int k1 = new Random().nextInt(nMarketingChannels-1);
            int k2 = new Random().nextInt(nMarketingChannels - k1) + k1;
            for(int i = 0; i < matingPool.size(); i++) {
                for (int j = k1; j < k2; j++) {
                    double tmp = matingPool.get(i).genes.get(j);
                    matingPool.get(i).genes.set(j , matingPool.get(i + 1).genes.get(j));
                    matingPool.get(i + 1).genes.set(j, tmp);
                }
                i++;
            }
        }
        /*System.out.println("Enter The Number Of Mutation Technique To be Applied: ");
        System.out.println(" 1- Uniform Mutation. \n 2-Non-Uniform Mutation. \n");
        System.out.print(" ");
        int choice = new Scanner(System.in).nextInt();
        if(choice == 1)
            uniformMutation(matingPool);
        else if(choice == 2)
            nonUniformMutation(matingPool);
        else
            System.exit(0);
        */
    }
    /* ---------------------------------------------------------------------------------------------- */
    public void uniformMutation(ArrayList<Chromosome> mp){
        for(int i = 0; i < mp.size(); i++){
            for(int j = 0; j < mp.get(i).genes.size(); j++){
                double rd = new Random().nextDouble();
                if(rd > pOfMutation) {
                    double rd1 = new Random().nextDouble();
                    double delta, rd2;
                    if(rd1 <= 0.5){
                        delta = (mp.get(i).genes.get(j) - marketingChannels.get(j).lB);
                        rd2 = new Random().nextDouble() * (delta - 0) + 0;
                        mp.get(i).genes.set(j , (mp.get(i).genes.get(j) - rd2));
                    }
                    else {
                        delta = (marketingChannels.get(j).uB - mp.get(i).genes.get(j));
                        rd2 = new Random().nextDouble() * (delta - 0) + 0;
                        mp.get(i).genes.set(j , (mp.get(i).genes.get(j) + rd2));
                    }
                }
            }
        }

    }
    /* ---------------------------------------------------------------------------------------------- */
    public void nonUniformMutation(ArrayList<Chromosome> mp) {
        for (int i = 0; i < mp.size(); i++) {
            for (int j = 0; j < mp.get(i).genes.size(); j++) {
                double rd = new Random().nextDouble();
                if (rd > pOfMutation) {
                    double rd1 = new Random().nextDouble();
                    double delta, rd2, dependencyFactor, x, r, finalResult;
                    if (rd1 <= 0.5){
                        delta = (mp.get(i).genes.get(j) - marketingChannels.get(j).lB);
                        rd2 = new Random().nextDouble();
                        dependencyFactor = new Random().nextDouble() * (5 - 0.5) + 0.5;
                        x = Math.pow(((1 - (currentGeneration / numberOfGenerations) )) , dependencyFactor);
                        r = Math.pow(rd2, x);
                        finalResult = delta * (1 - r);
                        mp.get(i).genes.set(j , mp.get(i).genes.get(j) - finalResult);
                    }
                    else{
                        delta = (marketingChannels.get(j).uB - mp.get(i).genes.get(j));
                        rd2 = new Random().nextDouble();
                        dependencyFactor = new Random().nextDouble() * (5 - 0.5) + 0.5;
                        x = Math.pow(((1 - (currentGeneration / numberOfGenerations) )) , dependencyFactor);
                        r = Math.pow(rd2, x);
                        finalResult = delta * (1 - r);
                        mp.get(i).genes.set(j , mp.get(i).genes.get(j) + finalResult);
                    }
                }
            }
        }

    }
    /* ---------------------------------------------------------------------------------------------- */
    public void replacement(){
        tempChromosomes.addAll(chromosomes);
        int k = populationSize - matingPool.size();
        Collections.sort(tempChromosomes, Comparator.comparing(Chromosome::getChromosomeFitness));
        Collections.reverse(tempChromosomes);
        chromosomes.clear();
        chromosomes.addAll(matingPool);

        for (int i = 0; i < k; i++) chromosomes.add(tempChromosomes.get(i));
        matingPool.clear();
        tempChromosomes.clear();
        currentGeneration++;
    }
    /* ---------------------------------------------------------------------------------------------- */
    public void output() throws IOException {
        int index = getFittest(chromosomes);
        experments.add(chromosomes.get(index));
        File f1 = new File("output.txt");
        if(!f1.exists()) {
            f1.createNewFile();
        }

        FileWriter fileWritter = new FileWriter(f1.getName(),true);
        BufferedWriter bw = new BufferedWriter(fileWritter);
        for (int t = 0; t < nMarketingChannels; t++){

            String data = marketingChannels.get(t).name + " -> " + chromosomes.get(index).genes.get(t) + " K" +"\n";
           // System.out.println(data);
            bw.write(data);

        }

        String data2="The Total Profit is " + chromosomes.get(index).chromosomeFitness + " K" +"\n";
        //System.out.println(data2);
        bw.write(data2);
        bw.write("\n \n"+"--------------------------------------------------------------------"+"\n");
        bw.close();

    }
    /* ---------------------------------------------------------------------------------------------- */
    public void correctAfterMutation(ArrayList<Chromosome> matingPool){
        for(int i =0 ;i<matingPool.size();i++){
            if(!checkFeasibilityBudget( matingPool.get(i))){
                matingPool.get(i).correct(marketingChannels);
            }
        }
    }

}

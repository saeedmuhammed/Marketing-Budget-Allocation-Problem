import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
            MBA mba = new MBA();
            mba.startProgram();
            System.out.println("Enter The Number Of Mutation Technique To be Applied: ");
            System.out.println(" 1- Uniform Mutation. \n 2-Non-Uniform Mutation.");
            System.out.print(" ");
            Scanner scan = new Scanner(System.in);
            mba.typeOfMutation = scan.nextInt();
            System.out.println("Please wait while running the GA…");
            Thread.sleep(4000);
            for(int s=0 ; s<20;s++ ) {
                    mba.populationInit();
                    for (int g = 0; g < mba.numberOfGenerations; g++) {
                            mba.runProgram();
                    }
                    mba.output();
                    mba.chromosomes.clear();
                    mba.currentGeneration=0;

            }
            System.out.println("Best Solution from 20 experiments is ----> "+mba.experments.get(mba.getFittest(mba.experments)).chromosomeFitness);

    }

}

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Clustering{
    static int M[] = new int[]{3,6,9,12};

    public static void main(String[] args){
        String errorFilePath = new String("Errors.txt");
        try{
            File errorFile = new File(errorFilePath);
            FileWriter errorFW = new FileWriter(errorFilePath);


            for(int i = 0; i < M.length; i++){
                System.out.println("\nM: " + M[i]);
                Point[] bestCentres = new Point[M[i]];
                double minError = 1000000000;
                for(int j = 0; j < 15; j++){

                    KMeans km = new KMeans(M[i]);
                
                    km.LoadData();
                    km.InitializeCentres();
                    km.Run();
                    km.CalculateErrors();
                    //km.PrintCentres();
                    km.PrintError();

                    if(km.error < minError){
                        minError = km.error;
                        bestCentres = km.GetCentres();
                    }
                }
                String path = new String("CentresM" + M[i] + ".txt");
                File centresFile = new File(path);
                FileWriter fw = new FileWriter(path);
                for(int j = 0; j < M[i]; j++){
                    fw.write(bestCentres[j].toString() + "\n");
                }
                fw.close();
                errorFW.write(minError + "\n");
            }
            errorFW.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
} 

class KMeans{
    int CLUSTERS; //M
    final int N_EXAMPLES = 1200;

    Point examples[];
    Point centres[];
    double errors[];
    double error;

    private Point prevCentres[];

    private Random rand;

    KMeans(int M){
        CLUSTERS = M;
        examples = new Point[N_EXAMPLES];
        centres = new Point[CLUSTERS];
        prevCentres = new Point[CLUSTERS];
        errors = new double[CLUSTERS];
        error = 0;
        rand = new Random();
    }

    void Run(){
        //K-means Algorithm
        for(int i = 0; TerminationCondition(); i++){
            for(int j = 0; j < N_EXAMPLES; j++){ 
                AssignCluster(examples[j]);
                System.out.print("M: " + this.CLUSTERS + " Iteration " + (i+1) + ": " + (j+1) + "/" + N_EXAMPLES + "\r");
            }
            CalculateNewCentres();
        }
    }

    void InitializeCentres(){
        //Randomly initialize centres with values from the examples
        for(int i = 0; i < CLUSTERS; i++){
            int randomNumber = rand.nextInt(N_EXAMPLES);
            centres[i] = new Point(examples[randomNumber].coords[0], examples[randomNumber].coords[1]);
            prevCentres[i] = new Point();
            errors[i] = 0;
        }
    }

    void PrintCentres(){
        for(int i = 0; i < CLUSTERS; i++){
            System.out.println("\nCentre " + (i+1) + ": " + centres[i].coords[0] + ", " + centres[i].coords[1]);
        }
    }

    boolean TerminationCondition(){
        boolean ret = false;
        for(int i = 0; i < CLUSTERS; i++){
            if(!centres[i].Compare(prevCentres[i])){
                ret = true;
                break;
            }
        }
        return ret;
    }

    void LoadData(){
        try{
            Scanner scanner = new Scanner(new File("TrainingSet2.txt"));

            for(int i = 0; i < N_EXAMPLES; i++){
                examples[i] = new Point(scanner.nextDouble(), scanner.nextDouble());
                scanner.nextLine();
            }
        }
        catch(FileNotFoundException e){
            System.out.println("Examples file not found. Exiting...");
            System.exit(1);
        }
        catch(InputMismatchException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    double CalculateDistance(Point point, Point centre){
        double sum = 0;
        double diff = 0;
        for(int i = 0; i < point.coords.length; i++){
            diff = point.coords[i] - centre.coords[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    void AssignCluster(Point point){
        double minDist = 1000000000;
        int closestCentre = -1;
        double dist = 0;
        for(int i = 0; i < CLUSTERS; i++){
            dist = CalculateDistance(point, centres[i]);
            
            if(dist < minDist){
                minDist = dist;
                closestCentre = i;
            }
        }
        point.cluster = closestCentre;
        point.distToCentre = minDist;
    }

    void CalculateNewCentres(){
        for(int i = 0; i < CLUSTERS; i++){
            prevCentres[i] = centres[i];
            centres[i] = new Point();
        }

        int elemsPerCentre[] = new int[CLUSTERS];
        for(int i = 0; i < CLUSTERS; i++){
            elemsPerCentre[i] = 0;
        }
        for(int i = 0; i < N_EXAMPLES; i++){
            centres[examples[i].cluster].coords[0] += examples[i].coords[0];
            centres[examples[i].cluster].coords[1] += examples[i].coords[1];
            elemsPerCentre[examples[i].cluster] += 1;
        }
        for(int i = 0; i < CLUSTERS; i++){
            if(elemsPerCentre[i] == 0){
                continue;
            }
            centres[i].coords[0] /= elemsPerCentre[i];
            centres[i].coords[1] /= elemsPerCentre[i];
        }
    }

    void CalculateErrors(){
        for(int i = 0; i < N_EXAMPLES; i++){
            errors[examples[i].cluster] += examples[i].distToCentre;
            error += examples[i].distToCentre;
        }
    }

    void PrintErrors(){
        for(int i = 0; i < CLUSTERS; i++){
            System.out.println("\rError for cluster " + (i+1) + ": " + errors[i]);
        }
    }

    void PrintError(){
        System.out.println("\nError: " + this.error);
    }

    Point[] GetCentres(){
        return this.centres;
    }
}

class Point{
    double coords[] = new double[]{0,0};
    double distToCentre = 0;
    int cluster;
    Point(){}
    Point(double x, double y){
        coords[0] = x;
        coords[1] = y;
        cluster = -1;
    }
    Point(Point p){
        coords[0] = p.coords[0];
        coords[1] = p.coords[1];
        cluster = p.cluster;
    }

    boolean Compare(Point p){
        if(coords[0] == p.coords[0] && coords[1] == p.coords[1]){
            return true;
        }
        return false;
    }

    public String toString(){
        return new String(coords[0] + " " + coords[1]);
    }
}
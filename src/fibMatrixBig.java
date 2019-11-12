import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigInteger;

public class fibMatrixBig {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static int numberOfTrials = 100000;
    static int MAXINPUTSIZE  = 100;
    static int MININPUTSIZE  =  1;

    static String ResultsFolderPath = "/home/nicolocker/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        verifyWorks();
        System.out.println("\n");

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        System.out.println("Running first full experiment...");
        runFullExperiment("FibMatrixBig-Exp1.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("FibMatrixBig-Exp2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("FibMatrixBig-Exp3.txt");
    }

    public static void verifyWorks(){
        System.out.println("\n------- Test Run ------");
        for(int i = 0; i <=20; i++){
            System.out.println(fibMatrixBig(i));
        }
    }


    public static void runFullExperiment(String resultsFileName){

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#X(value)         N(size)        T(time)"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize++) {
            // progress message...
            System.out.println("Running test for input size "+inputSize+" ... ");

            long batchElapsedTime = 0;
            System.gc();

            // run the tirals
            for (long trial = 0; trial < numberOfTrials; trial++) {

                TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                /* run the function we're testing on the trial input */
                //    long foundIndex = binarySearch(testSearchKey, testList);

                fibMatrixBig.fibMatrixBig(inputSize);

                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }


            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            long N = (long)(Math.floor(Math.log(inputSize)/Math.log(2)));
            /* print data for this size of input */
            resultsWriter.printf("%6d  %15d %15.2f\n",inputSize, N, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");

        }
    }

    public static BigInteger fibMatrixBig(long x) {
        BigInteger[][] fib = new BigInteger[][]{{BigInteger.valueOf(1), BigInteger.valueOf(1)},
                                                {BigInteger.valueOf(1), BigInteger.valueOf(0)}};

        if(x == 0){
            return new BigInteger(String.valueOf(0));
        }

        matrixPowerBig(fib, x-1);
        return fib[0][0];
    }

    // calculates powers of the matrix
    public static void matrixPowerBig(BigInteger[][] fib, long x) {

        BigInteger fib2[][] = new BigInteger[][]{{BigInteger.valueOf(1), BigInteger.valueOf(1)},
                                                 {BigInteger.valueOf(1), BigInteger.valueOf(0)}};

        for (int i = 2; i <= x; i++)
            multiplyBig(fib, fib2);
    }

    //multiplies the two matrices and puts the result back into the first one
    public static void multiplyBig(BigInteger[][] fib, BigInteger[][] fib2){

        BigInteger a = fib[0][0].multiply(fib2[0][0]);
        BigInteger b = fib[0][1].multiply(fib2[1][0]);
        BigInteger x = a.add(b);
        BigInteger y = (fib[0][0].multiply(fib2[0][1])).add(fib[0][1].multiply(fib2[1][1]));
        BigInteger z = (fib[1][0].multiply(fib2[0][0])).add(fib[1][1].multiply(fib2[1][0]));
        BigInteger w = (fib[1][0].multiply(fib2[0][1])).add(fib[1][1].multiply(fib2[1][1]));

        fib[0][0] = x;
        fib[0][1] = y;
        fib[1][0] = z;
        fib[1][1] = w;
    }
}

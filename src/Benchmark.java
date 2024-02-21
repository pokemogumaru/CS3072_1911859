import java.io.IOException;

public class Benchmark {
    public static void main(String[] args) throws IOException {
    	int arraySize = 1000; // Adjust for different sizes
        double[] array_1D = CS3072_1911859.new_TSP(arraySize);
        final long REPETITIONS = 1_000_000L; // One million

        // Benchmark original method
        double[][] result1 = null;
        long startTime = System.nanoTime();
        for (long i = 0; i < REPETITIONS; i++) {
            result1 = SolveTSP.convert_1D_to_2D_old(array_1D);
        }
        long endTime = System.nanoTime();
        long durationOriginal = endTime - startTime;

        // Benchmark optimized method
        double[][] result2 = null;
        startTime = System.nanoTime();
        for (long i = 0; i < REPETITIONS; i++) {
            result2 = SolveTSP.convert_1D_to_2D(array_1D);
        }
        endTime = System.nanoTime();
        long durationOptimized = endTime - startTime;

        // Format and print results
        double originalSeconds = (double) durationOriginal / 1_000_000_000.0;
        double optimizedSeconds = (double) durationOptimized / 1_000_000_000.0;

        System.out.printf("Original Time: %.3f seconds, %.3f milliseconds\n", originalSeconds, originalSeconds * 1000);
        System.out.printf("Optimized Time: %.3f seconds, %.3f milliseconds\n", optimizedSeconds, optimizedSeconds * 1000);
        
        //System.out.println("Original Time: " + durationOriginal + " nanoseconds");
        //CS3072_1911859.print_2D(result1);
        //System.out.println("Optimized Time: " + durationOptimized + " nanoseconds");
        //CS3072_1911859.print_2D(result2);
    }

    
}

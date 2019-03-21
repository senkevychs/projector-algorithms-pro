import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Solution {

    private static int nextInt() throws IOException  {
        streamTokenizer.nextToken();
        return (int)streamTokenizer.nval;
    }
    
    private static StreamTokenizer streamTokenizer;
    
    public static void main(String[] args) throws IOException {
        streamTokenizer = new StreamTokenizer(new BufferedReader(new InputStreamReader(System.in)));
        // Scanner scanner = new Scanner(System.in);
        int n = nextInt(); // scanner.nextInt();
        int[] par = new int[n];
        par[0] = -1;
        for (int i = 1; i < n; ++i) {
            par[i] = nextInt() - 1; // scanner.nextInt() - 1;
        }
        System.out.println(par[n-1]);
    }
}
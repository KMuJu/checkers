package checkers.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Zobrist {
    static final long seed = 2361912;
    static String randomNumbersFilePath = "src/main/java/checkers/game/text/RandomNumbers.txt";
    static File randomNumbersFile = new File(randomNumbersFilePath);

    // [pieceIndex][colour][index]
    public static long[][][] piecesArray = new long[2][2][64];

    static Random prng = new Random(seed);

    public static void WriteRandomNumbers(){
        prng = new Random(seed);
        String randomNumberString = "";
        int numRandomNumbers = 64 * 2 * 2;
        for (int i = 0; i < numRandomNumbers; i++) {
            randomNumberString += RandomLong();
            if (i != numRandomNumbers-1){
                randomNumberString += "\n";
            }
        }
        try {
            Files.write(randomNumbersFile.toPath(), randomNumberString.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Queue<Long> ReadRandomNumbers(){
        Queue<Long> randomNumberQueue = new ConcurrentLinkedQueue<>();
        if (!Files.exists(randomNumbersFile.toPath())){
            System.out.println("Write files");
            WriteRandomNumbers();
        }
        Scanner scanner;
        try {
            scanner = new Scanner(randomNumbersFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return randomNumberQueue;
        }
        while(scanner.hasNextLine()){
            long number = Long.parseLong(scanner.nextLine());
            randomNumberQueue.add(number);
        }
        scanner.close();
        return randomNumberQueue;
    }

    static{
        Queue<Long> randomNumbers = ReadRandomNumbers();
        
        for (int index = 0; index < 64; index++) {
            for (int pieceIndex = 0; pieceIndex < 2; pieceIndex++) {
                piecesArray[pieceIndex][0][pieceIndex] = randomNumbers.poll();
                piecesArray[pieceIndex][1][pieceIndex] = randomNumbers.poll();
            }
        }
    }

    public static long RandomLong(){
        byte[] buffer = new byte[8];
        prng.nextBytes(buffer);
        long randomNumber = 0;
        for (int i = 0; i < 8; i++) {
            randomNumber += buffer[i] << (i*8);
        }
        return randomNumber;

    }
}

/**
 * Framework.java  - for the Longest Prefix Match challenge
 *
 *   Version: 2019-07-10
 * Copyright: University of Twente, 2015-2019
 *
 ************************************************************
 *                                                          *
 **                 DO NOT EDIT THIS FILE                  **
 ***         YOU DO NOT HAVE TO LOOK IN THIS FILE         ***
 **** IF YOU HAVE QUESTIONS PLEASE DO ASK A TA FOR HELP  ****
 *****                                                  *****
 ************************************************************
 */

package framework;

import lpm.LongestPrefixMatcher;

import java.io.*;
import java.util.*;

public class Framework {
    public static final File ROUTES_FILE = new File("routes.txt");
    public static final File LOOKUP_FILE = new File("lookup.txt");
    public static final File OUTPUT_FILE = new File("expected_output.txt");

    private final LongestPrefixMatcher lpm;

    public static void main(String[] args) {
        Framework client = new Framework();
        
        client.readRoutes(ROUTES_FILE);
        
        if (client.readLookup(LOOKUP_FILE, OUTPUT_FILE)) {
            System.out.println("All lookups done successfully.");
        }
    }

    public Framework() {
        lpm = new LongestPrefixMatcher();
    }

    /**
     * Reads IPs to look up from lookup.bin and passes them to this.lookup
     */
    private boolean readLookup(File lookupFile, File outputFile) {
        try (BufferedReader brLookup = new BufferedReader(new FileReader(lookupFile));
             BufferedReader brOutput = new BufferedReader(new FileReader(outputFile))) {

            String lineLookup, lineOutput;
            StringBuilder sb = new StringBuilder();

            List<Integer> input = new ArrayList<>();
            List<Integer> expectedOutput = new ArrayList<>();
            while ((lineLookup = brLookup.readLine()) != null && (lineOutput = brOutput.readLine()) != null) {
                Integer result = lpm.lookup(this.parseIP(lineLookup));
                Integer expected = Integer.parseInt(lineOutput);

                if (!expected.equals(result)) {
                    System.out.println("Error with lookup: " + lineLookup + ", expected: " + lineOutput + ", actual: " + result);
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("Could not open " + lookupFile + " or " + outputFile);
            return false;
        }
        
        return true;
    }

    /**
     * Reads routes from routes.txt and parses each
     */
    private void readRoutes(File routesFile) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(routesFile));
            String line;
            while ((line = br.readLine()) != null) {
                this.parseRoute(line);
            }
            lpm.finalizeRoutes();
        } catch (IOException e) {
            System.err.println("Could not open " + routesFile);
        } finally {
            if (br != null) {
                try { br.close(); }
                catch (IOException e) { }
            }
        }
    }

    /**
     * Parses a route and passes it to this.addRoute
     */
    private void parseRoute(String line) {
        String[] split = line.split("\t");
        int portNumber = Integer.parseInt(split[1]);

        split = split[0].split("/");
        byte prefixLength = Byte.parseByte(split[1]);

        int ip = this.parseIP(split[0]);

        lpm.addRoute(ip, prefixLength, portNumber);
    }

    /**
     * Parses an IP
     * @param ipString The IP address to convert
     * @return The integer representation for the IP
     */
    private int parseIP(String ipString) {
        String[] ipParts = ipString.split("\\.");

        int ip = 0;
        for (int i = 0; i < 4; i++) {
            ip |= Integer.parseInt(ipParts[i]) << (24 - (8 * i));
        }

        return ip;
    }
}
/*
 * This program aims at perfomring hashing to a set of input data structure.
 * The input file is read as a text stream, and based on the operation specified the task is carried out.
 * For INSERTION - A hash function is invoked for the input, and inserted to the table. If collision occurs, linear probing is implemented to handle collisions.
 * For FIND - A hash function is invoked for the input, and the hash value is used as index to find the value from the table. When Delete falg is encountered, 
 *      linear probing is used to locate the item.
 * For REMOVE - A hash function is invoked for the input, and the hash value is used as index to remove the value from the table. When Delete falg is encountered, 
 *      linear probing is used to locate the item, then delete it. Once an item is deleted, flag it as <DELETED>.
 * Output is a combination of transaction stream echo and the status of operation perfomed on the input. Also, a copy of hash table is displayed.
*/
package exhash;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * @author Ashwin Ravishankar
 */
public class ExHash {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        FileReader fileRead = new FileReader(args[0]);
        Scanner inputRead = new Scanner (fileRead);
        String[][] fileData = new String[105][8];
        String[][] hashTable = new String[100][9];
        char[] operations = {'I', 'F', 'R'};
        String strLine="", inputLine="", deleteFlag = "<DELETED>";
        char ch,operation;
        int flag=0, counter=0, breakCounter=0, hashValue=0, collisionCount=0, totalNoOfCollisions=0 ;
        boolean initial=false;
        
        while(inputRead.hasNext()) {
            inputLine=inputRead.nextLine();
            flag=0; breakCounter=0;
            if(!(Character.isWhitespace(inputLine.charAt(0)))) {
                for(int i=0;i<inputLine.length();i++) {
                    ch = inputLine.charAt(i);
                    if(flag == 0 && isOperation(ch)) {
                        fileData[counter][0] = String.valueOf(ch);
                    }
                    else if(Character.isWhitespace(ch)) {
                        if(flag==0) {
                            initial=true;
                            flag=1;
                        }
                        else if(flag==1 && !initial) {
                            initial=true;
                            flag=2;
                        }
                        else if(flag==2  && !initial) {
                            initial=true;
                            flag=3;
                        }
                        else if(flag==3  && !initial && breakCounter==3) {
                            initial=true;
                            flag=4;
                            breakCounter=0;
                        }
                        else if(flag==4  && breakCounter==1) {
                            initial=true;
                            flag=5;
                        }
                    }
                    else if(flag==1 && !(Character.isWhitespace(ch))) {
                        if(initial) {
                            fileData[counter][1]=String.valueOf(ch);
                            initial=false;
                            continue;
                        }
                        fileData[counter][1]+=String.valueOf(ch);
                    }
                    else if(flag==2 && !(Character.isWhitespace(ch))) {
                        if(initial) {
                            fileData[counter][2]=String.valueOf(ch);
                            initial = false;
                            continue;
                        }
                        fileData[counter][2]+=String.valueOf(ch);
                    }
                    else if(flag>=3 && !(Character.isWhitespace(ch))) {
                        if(flag==3  && !initial && breakCounter>3) {
                            initial=true;
                            flag=4;
                            breakCounter=0;
                        }
                        else if(flag==4  && breakCounter==1) {
                            initial=true;
                            flag=5;
                            breakCounter=0;
                        }
                        
                        if(flag==3) {
                            if(initial) {
                                fileData[counter][3]=String.valueOf(ch);
                                initial = false; breakCounter++;
                                continue;
                            }
                            fileData[counter][3]+=String.valueOf(ch); breakCounter++; 
                        }
                        else if(flag==4) {
                            fileData[counter][4]=String.valueOf(ch);
                            breakCounter++;
                        }
                        else if(flag==5) {
                            if(initial) {
                                fileData[counter][5]=String.valueOf(ch);
                                initial = false; breakCounter++;
                                continue;
                            }
                            fileData[counter][5]+=String.valueOf(ch); breakCounter++; 
                        }
                        
                    }
                    
                }
            }
        counter++;
        }
        
        System.out.format("%50s \t %45s\n", "Echo of Transaction", "Result of transaction");
        System.out.format("%50s \t %45s\n", "-----------------------------", "---------------------------");
        
        //Start with transaction operation
        for(int i=0;i<counter;i++) {
            operation = fileData[i][0].charAt(0);
            hashValue = HashFunction(fileData[i][1]+fileData[i][2]+fileData[i][3]+fileData[i][4]+fileData[i][5]);
            switch (operation) {
                case 'I':
                    collisionCount=0;
                    if(hashTable[hashValue][0] == null || hashTable[hashValue][0] == deleteFlag) {
                        hashTable[hashValue][0] = String.valueOf(hashValue);
                        hashTable[hashValue][1] = fileData[i][1];
                        hashTable[hashValue][2] = fileData[i][2];
                        hashTable[hashValue][3] = fileData[i][3];
                        hashTable[hashValue][4] = fileData[i][4];
                        hashTable[hashValue][5] = fileData[i][5];
                        hashTable[hashValue][6] = String.valueOf(0);
                        System.out.format("%50s \t %45s\n", "INSERT " + fileData[i][1] + " " + fileData[i][2] + " " + fileData[i][3], "Record Inserted to hash table, NO collision encountered");
                    }
                    else {
                        for (int j=hashValue+1; j<=100;j++) {
                            collisionCount++;
                            //System.out.println(j);
                            if(j==100)
                                j=1;
                            if(j==hashValue)
                                break;
                            if(hashTable[j][0] == null || hashTable[hashValue][0] == deleteFlag) {
                                hashTable[j][0] = String.valueOf(hashValue);
                                hashTable[j][1] = fileData[i][1];
                                hashTable[j][2] = fileData[i][2];
                                hashTable[j][3] = fileData[i][3];
                                hashTable[j][4] = fileData[i][4];
                                hashTable[j][5] = fileData[i][5];
                                hashTable[j][6] = String.valueOf(collisionCount);
                                System.out.format("%50s \t %45s\n", "INSERT " + fileData[i][1] + " " + fileData[i][2] + " " + fileData[i][3], "Record Inserted to hash table, encountered " + collisionCount + " collisions");
                                totalNoOfCollisions+=collisionCount;
                                break;
                            }
                        }
                        //System.out.format("%50s \t %45s\n", "INSERT " + fileData[i][1] + " " + fileData[i][2] + " " + fileData[i][3], "Error occured in inseting record to hash table");
                    }
                    break;
                case 'F':
                    if(hashTable[hashValue][0] != null) {
                        for (int j=hashValue; j<=100;j++) {
                            //System.out.println(j);
                            if(j==100)
                                j=1;
                            if(j==hashValue-1) {
                                System.out.format("%50s \t %45s\n", "FIND " + fileData[i][1] + " " + fileData[i][2] + " " + fileData[i][3], "Record not found in Hash Table");
                                break;
                            }
                            if(hashTable[j][0] != null) {
                                if(hashTable[j][0].equals(deleteFlag)
                                        && (hashTable[j][1].equals(deleteFlag))
                                        && (hashTable[j][2].equals(deleteFlag))
                                        && (hashTable[j][3].equals(deleteFlag))
                                        && (hashTable[j][4].equals(deleteFlag))
                                        && (hashTable[j][5].equals(deleteFlag))) {
                                    continue;
                                }
                                if(hashTable[j][0].equals(String.valueOf(hashValue).toString())
                                        && (hashTable[j][1].equals(fileData[i][1]))
                                        && (hashTable[j][2].equals(fileData[i][2]))
                                        && (hashTable[j][3].equals(fileData[i][3]))
                                        && (hashTable[j][4].equals(fileData[i][4]))
                                        && (hashTable[j][5].equals(fileData[i][5]))) {
                                     System.out.format("%50s \t %45s\n", "FIND " + fileData[i][1] + " " + fileData[i][2] + " " + fileData[i][3], "Record available in Hash Table");
                                     break;
                                }
                            }
                        }
                    }
                    else {
                        System.out.format("%50s \t %45s\n", "FIND " + fileData[i][1] + " " + fileData[i][2] + " " + fileData[i][3], "Record not found in Hash Table");
                    }
                    break;
                case 'R':
                    if(hashTable[hashValue][0] != null) {
                        for (int j=hashValue; j<=100;j++) {
                            //System.out.println(j);
                            if(j==100)
                                j=1;
                            if(j==hashValue-1) {
                                System.out.format("%50s \t %45s\n", "REMOVE " + fileData[i][1] + " " + fileData[i][2] + " " + fileData[i][3], "Record not found in Hash Table");
                                break;
                            }
                            if(hashTable[j][0] != null) {
                                if(hashTable[j][0].equals(deleteFlag)
                                        && (hashTable[j][1].equals(deleteFlag))
                                        && (hashTable[j][2].equals(deleteFlag))
                                        && (hashTable[j][3].equals(deleteFlag))
                                        && (hashTable[j][4].equals(deleteFlag))
                                        && (hashTable[j][5].equals(deleteFlag))) {
                                    continue;
                                }
                                if(hashTable[j][0].equals(String.valueOf(hashValue).toString())
                                        && (hashTable[j][1].equals(fileData[i][1]))
                                        && (hashTable[j][2].equals(fileData[i][2]))
                                        && (hashTable[j][3].equals(fileData[i][3]))
                                        && (hashTable[j][4].equals(fileData[i][4]))
                                        && (hashTable[j][5].equals(fileData[i][5]))) {
                                    
                                    hashTable[j][0] = deleteFlag;
                                    hashTable[j][1] = deleteFlag;
                                    hashTable[j][2] = deleteFlag;
                                    hashTable[j][3] = deleteFlag;
                                    hashTable[j][4] = deleteFlag;
                                    hashTable[j][5] = deleteFlag;
                                    hashTable[j][6] = deleteFlag;                                    
                                    System.out.format("%50s \t %45s\n", "REMOVE " + fileData[i][1] + " " + fileData[i][2] + " " + fileData[i][3], "Record removed from Hash Table");
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        System.out.format("%50s \t %45s\n", "REMOVE " + fileData[i][1] + " " + fileData[i][2] + " " + fileData[i][3], "Record not found in Hash Table");
                    }
                    break;
            }
        }
        System.out.println("Total Number of Collisions Encountered: " +totalNoOfCollisions);
        System.out.println("\n\n");
        System.out.format("%100s \n", "-------------------------------------------------------------------- HASH TABLE -----------------------------------------------------------------");
        System.out.println("");
        System.out.format("%10s \t %20s \t %20s \t %20s \t %10s \t %20s \t %10s\n", "Hash#", "Last Name", "First Name", "Year Of Birth", "Gender", "Major", "#Collisions");
        System.out.format("-------------------------------------------------------------------------------------------------------------------------------------------------\n");
        
        //Display the entire hash table
        for (int i=0;i<100;i++) {
            //System.out.println(fileData[i][0] + "  " + fileData[i][1] + "   " + fileData[i][2] + "    " +fileData[i][3] + "   " +fileData[i][4] + "   " + fileData[i][5] + "   ");
            if(hashTable[i][0]!=null)
                System.out.format("%10s \t %20s \t %20s \t %20s \t %10s \t %20s \t %10s\n", hashTable[i][0], hashTable[i][1], hashTable[i][2], hashTable[i][3], hashTable[i][4], hashTable[i][5], hashTable[i][6]);
        
        }
    }
    
    //Critical: Hash function
    public static int HashFunction(String value) {
        int hash = 7;
        for (int i=0;i<value.length();i++) {
            hash += value.charAt(i);
        }
        hash = hash * 23;
        hash = hash % 99;
        return hash;
    }

    public static boolean isOperation(char ch) {
        return ch == 'I' || ch == 'F' || ch == 'R';
    }   
    
}

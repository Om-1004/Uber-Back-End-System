// Om Patel
// Student ID: 501170337

import java.util.Arrays;
import java.util.Scanner;

// The city consists of a grid of 9 X 9 City Blocks

// Streets are east-west (1st street to 9th street)
// Avenues are north-south (1st avenue to 9th avenue)

// Example 1 of Interpreting an address:  "34 4th Street"
// A valid address *always* has 3 parts.
// Part 1: Street/Avenue residence numbers are always 2 digits (e.g. 34).
// Part 2: Must be 'n'th or 1st or 2nd or 3rd (e.g. where n => 1...9)
// Part 3: Must be "Street" or "Avenue" (case insensitive)

// Use the first digit of the residence number (e.g. 3 of the number 34) to determine the avenue.
// For distance calculation you need to identify the the specific city block - in this example 
// it is city block (3, 4) (3rd avenue and 4th street)

// Example 2 of Interpreting an address:  "51 7th Avenue"
// Use the first digit of the residence number (i.e. 5 of the number 51) to determine street.
// For distance calculation you need to identify the the specific city block - 
// in this example it is city block (7, 5) (7th avenue and 5th street)
//
// Distance in city blocks between (3, 4) and (7, 5) is then == 5 city blocks
// i.e. (7 - 3) + (5 - 4) 

public class CityMap
{
  // Checks for string consisting of all digits
  // An easier solution would use String method matches()
  private static boolean allDigits(String s)
  {
    for (int i = 0; i < s.length(); i++)
      if (!Character.isDigit(s.charAt(i)))
        return false;
    return  true;
  }

  // Get all parts of address string
  // An easier solution would use String method split()
  // Other solutions are possible - you may replace this code if you wish
  private static String[] getParts(String address)
  {
    String parts[] = new String[3];
    
    if (address == null || address.length() == 0)
    {
      parts = new String[0];
      return parts;
    }
    int numParts = 0;
    Scanner sc = new Scanner(address);
    while (sc.hasNext())
    {
      if (numParts >= 3)
        parts = Arrays.copyOf(parts, parts.length+1); // Makes a new array with the orginal content of parts ad increases the length by 1

      parts[numParts] = sc.next();
      numParts++;
    }
    if (numParts == 1)
      parts = Arrays.copyOf(parts, 1); // Copy the parts array and gives it the length 1
    else if (numParts == 2)
      parts = Arrays.copyOf(parts, 2); // Copy the parts array and gives it the length 2
    return parts;
  }

  // Checks for a valid address
  public static boolean validAddress(String address)
  {
    // Fill in the code
    // Make use of the helper methods above if you wish
    // There are quite a few error conditions to check for 
    // e.g. number of parts != 3
    String[] parts = getParts(address);
    if (parts == null || parts.length != 3) {
        return false;
    }

  // Part 1: Street/Avenue residence numbers are always 2 digits (e.g. 34).
    if (parts[0] == null || (!(parts[0].length() == 2 && Character.isDigit(parts[0].charAt(0)) && Character.isDigit(parts[0].charAt(1))))) {
      return false;
    }

  // Part 2: Must be 'n'th or 1st or 2nd or 3rd (e.g. where n => 1...9)
    if (parts[1] == null  || (!(parts[1].length() == 3 && Character.isDigit(parts[1].charAt(0)) && (parts[1].endsWith("st") || parts[1].endsWith("nd") || parts[1].endsWith("rd") || parts[1].endsWith("th"))))){
      return false;
    }

    // Part 3: Must be "Street" or "Avenue" (case insensitive)
    if (parts[2] == null  || (!(parts[2].equalsIgnoreCase("Street") || parts[2].equalsIgnoreCase("Avenue")))){
      return false;
    }

    return true;
  }

  // Computes the city block coordinates from an address string
  // returns an int array of size 2. e.g. [3, 4] 
  // where 3 is the avenue and 4 the street
  // See comments at the top for a more detailed explanation
  public static int[] getCityBlock(String address)
  {
    int[] block = {-1, -1};

    if (!(validAddress(address))){
      return block;
    }


    String[] parts = getParts(address);
    int first_num = Character.getNumericValue(parts[0].charAt(0)); // Gets the first character of the first string and stores it in first_num
    int second_num = Character.getNumericValue(parts[1].charAt(0)); // Gets the first character of the second string and stores it in second_num

    if(parts[2].equalsIgnoreCase("Street")){
      block[0] = first_num;
      block[1] = second_num;
    }

    else if (parts[2].equalsIgnoreCase("Avenue")){
      block[0] = second_num;
      block[1] = first_num;
    }

    return block;
  }
  
  
  // Calculates the distance in city blocks between the 'from' address and 'to' address
  // Hint: be careful not to generate negative distances
  
  // This skeleton version generates a random distance
  // If you do not want to attempt this method, you may use this default code
  public static int getDistance(String from, String to)
  {
    // Fill in the code or use this default code below. If you use
    // the default code then you are not eligible for any marks for this part
  
    int[] beg = getCityBlock(from); 
    int[] end = getCityBlock(to);


    if(beg[0] == -1 || beg[1] == -1 || end[0] == -1 || end[1] == -1){ // If the "from" or "to" have a -1, then they are invalid so return 0
      return 0;
    }

    int distance = Math.abs(beg[0] - end[0]) + Math.abs(beg[1] - end[1]); // Calculated the distance using the distance formula
    return distance;
  }
}

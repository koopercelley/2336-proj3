// Cooper Kelley (clk200002)

import java.io.*;
import java.nio.*;
import java.util.*;

public class Main {

  static double ADULT_PRICE = 10.00;
  static double CHILD_PRICE = 5.00;
  static double SENIOR_PRICE = 7.50;

  public static void main(String[] args) throws Exception {
    Scanner scnr = new Scanner(System.in);
    String fileName;
    Auditorium auditorium = new Auditorium();
    System.out.println("Enter the file name");
    fileName = scnr.nextLine();
    FileInputStream userFile;
    Scanner inSS;
    int numRows = 0;
    int numCols = 0;
    int numAdult = 0; // numAdult, numChild, numSenior hold the tickets for each type already reserved
    int numChild = 0;
    int numSenior = 0;
    int numAdultTickets = 0; // numAdultTickets, numChildTickets, numSeniorTickets hold the amount of tickets
    // the user wants to book
    int numChildTickets = 0;
    int numSeniorTickets = 0;
    int menuSelect = 0;
    try {
      userFile = new FileInputStream(fileName);
      inSS = new Scanner(userFile);
      int colNum = 1;
      int rowNum = 1;
      while (inSS.hasNextLine()) {
        colNum = 1;
        String rowS = inSS.nextLine();
        char seatChar = 'A';
        while (colNum <= rowS.length()) {
          char ch = rowS.charAt(colNum - 1);
          Seat newSeat = new Seat(rowNum, seatChar, ch);
          Node<Seat> newNode = new Node<>(); // new horizontal node
          newNode.setPayload(newSeat);
          if (colNum == 1) {
            auditorium.addNodeVert(newNode); // add new horizontal node
          } else {
            auditorium.addNodeHoriz(newNode); // add new vertical node
          }
          seatChar++;
          colNum++;

          // add number of tickets (numAdult, numSenior, numChild) to their totals
          if (ch == 'A') {
            numAdult++;
          } else if (ch == 'C') {
            numChild++;
          } else if (ch == 'S') {
            numSenior++;
          }
        }
        rowNum++;
      }
      numRows = rowNum - 1;
      numCols = colNum - 1;
      inSS.close();
    } catch (Exception e) {
      scnr.close();
      throw new Exception("File error.");
    }

    // display menu
    do {
      menuSelect = displayMenu(menuSelect, scnr);
      if (menuSelect == 1) {
        printCustAuditorium(scnr, auditorium, numCols);
        // ask user for row number
        int rowSelection = getRowSelection(scnr, numRows);
        // ask user for starting seat letter
        char startingSeat = getStartingSeat(scnr, numCols);
        // ask user for number of adult tickets
        numAdultTickets = getNumTickets(scnr, "adult");
        // ask user for number of child tickets
        numChildTickets = getNumTickets(scnr, "child");
        // ask user for number of senior tickets
        numSeniorTickets = getNumTickets(scnr, "senior");
        int ticketsReserving =
          numAdultTickets + numChildTickets + numSeniorTickets;

        // check if the seats the customer is wanting to reserve are open
        // pass startingSeat and rowSelection in order to loop through each row and
        // column
        Node<Seat> startSeat = checkDesiredSeatsAvailable(
          auditorium,
          rowSelection,
          startingSeat,
          ticketsReserving
        );
        // if startSeat is not null, then reserve those seats
        if (startSeat != null) {
          // function to reserve seats
          reserveSeats(
            auditorium,
            startSeat,
            numAdultTickets,
            numChildTickets,
            numSeniorTickets
          );
          printCustAuditorium(scnr, auditorium, numCols);
        } else {
          // find best starting seats and ask if they would like to reserve them
          // if so, reserve the best seats
          // if not, display no seats available
          Node<Seat> startClosest = findClosestSeats(
            auditorium,
            ticketsReserving,
            numRows,
            numCols
          );
          if (startClosest != null) {
            Seat cs = (Seat) startClosest.getPayload();
            System.out.println(
              "The closest seats are: " +
              cs.getRow() +
              cs.getSeat() +
              "-" +
              cs.getRow() +
              (char) (cs.getSeat() + ticketsReserving - 1)
            );
            System.out.println("Would you like to reserve these? (Y/N)");
            String userInput;
            userInput = scnr.next();
            userInput = userInput.toUpperCase();
            if (userInput.equals("Y")) {
              // reserve the closest seats
              reserveSeats(
                auditorium,
                startClosest,
                numAdultTickets,
                numChildTickets,
                numSeniorTickets
              );
              System.out.println("hello");
            } else {
              System.out.println("No seats available.");
            }
          } else {
            System.out.println("No seats available");
          }
        }
        // if seats wanting to be reserved are not available, display that those seats
        // are not available
        // ask if they would like to reserve the next best seats and find closest seats

      } else if (menuSelect == 2) {
        // write curent status of auditorium to A1.txt
        FileOutputStream outputFile = new FileOutputStream("A1.txt"); // open the output file
        PrintWriter outSS = new PrintWriter(outputFile); // printwriter to write to the output file
        printSeatsOutFile(auditorium, outSS);
        outSS.close();
        // display report to console
        displayStats(auditorium);
        return;
      }
    } while (menuSelect == 1);
  }

  // function to display the menu
  public static int displayMenu(int menuSelect, Scanner scnr) {
    do {
      System.out.println("Please select an option: ");
      System.out.println("\t1. Reserve Seats");
      System.out.println("\t2. Exit");
      if (scnr.hasNextInt()) {
        menuSelect = scnr.nextInt();
      } else {
        scnr.nextLine();
        menuSelect = displayMenu(menuSelect, scnr);
      }
    } while (menuSelect != 1 && menuSelect != 2);
    return menuSelect;
  }

  // function to display the current seating arrangement
  public static void printCustAuditorium(Scanner scnr, Auditorium a, int numC) {
    Node<Seat> head = new Node<>();
    head = a.getFirst();
    // print seat letters (ABCDEF....)
    // numC starts at 1, not 0. Therefore, using i < numC - 1 to print all seat
    // letters
    System.out.print("  ");
    for (int i = 0; i < numC; i++) {
      System.out.print((char) (i + 65));
    }
    System.out.println();
    int rowNum = 1;
    do {
      Node<Seat> cur = head;
      System.out.print(rowNum + " ");
      while (cur != null) {
        Seat seat = (Seat) cur.getPayload();
        if (seat.getType() != '.') {
          System.out.print('#');
        } else {
          System.out.print('.');
        }
        cur = cur.getNext();
      }
      System.out.println("");
      head = head.getDown();
      rowNum++;
    } while (head != null);
  }

  // function to get the amount of tickets of each type from the user with input
  // validation
  public static int getNumTickets(Scanner scnr, String ticketType) {
    int numTickets = 0;
    try {
      do {
        System.out.println("Enter the amount of " + ticketType + " tickets:");
        numTickets = scnr.nextInt();
        if (numTickets >= 0) break; else {
          System.out.println("Error. Incorrect " + ticketType + " number");
        }
      } while (numTickets < 0);
      return numTickets;
    } catch (Exception e) {
      scnr.nextLine();
      numTickets = getNumTickets(scnr, ticketType);
      return numTickets;
    }
  }

  // function tot get the row selection from the user
  // function to get row selection from user (assuming the input is of the correct
  // data type)
  public static int getRowSelection(Scanner scnr, int numRows) {
    int userSelection;
    try {
      do {
        System.out.println("Enter the row for the desired ticket(s):");
        userSelection = scnr.nextInt();
        // validate the input (make sure the row is between the range: 1 through
        // numRows, inclusive)
        if (
          userSelection >= 1 && userSelection <= numRows
        ) break; else userSelection = 0;
      } while (userSelection == 0);
      return userSelection;
    } catch (Exception e) {
      scnr.nextLine(); // clears the scanner buffer
      userSelection = getRowSelection(scnr, numRows);
      return userSelection;
    }
  }

  // function to get seat selection from user (assuming the input is of the
  // correct data type)
  public static char getStartingSeat(Scanner scnr, int numCols) {
    char userSelection;
    String buffer; // to hold user input
    do {
      System.out.println("Enter the starting seat for the desired ticket(s):");
      buffer = scnr.next();
      buffer = buffer.toUpperCase();
      userSelection = buffer.charAt(0);
      // validate the input (make sure the seat they entered is one of the seats that
      // exist in the theater)
      // after making the user selection upper case, add 65 to the number of columns
      // and compare the values
      char maxChar = (char) (numCols + 65);
      if (userSelection >= 'A' && userSelection <= maxChar) {
        break;
      } else userSelection = '0';
    } while (userSelection == '0');
    return userSelection;
  }

  /*
   * function to return the starting seat that the user wants to reserve IF the
   * seats they want to reserve are available
   * returns type Node<Seat>
   * if not, function returns null
   */
  public static Node<Seat> checkDesiredSeatsAvailable(
    Auditorium a,
    int rowSel,
    char startingSeat,
    int numDesiredTickets
  ) {
    Node<Seat> head = a.getFirst();
    Node<Seat> cur = new Node<>();
    Node<Seat> startSeat = new Node<>();
    for (int i = 0; i < rowSel - 1; i++) {
      if (head == null) {
        return null;
      }
      head = head.getDown();
    }
    cur = head;
    for (int i = 0; i < (int) (startingSeat - 65); i++) {
      if (cur == null) {
        return null;
      }
      cur = cur.getNext();
    }
    startSeat = cur;
    for (int i = 0; i < numDesiredTickets; i++) {
      if (cur == null) {
        return null;
      }
      Seat seat = (Seat) cur.getPayload();
      if (seat.getType() != '.') {
        return null;
      }
      cur = cur.getNext();
    }
    return startSeat;
  }

  /*
   * function to reserve the seats (sets type member of seat to the seats wanted)
   * will assign adult seats furthest left, with child and senior tickets
   * following
   * does not return anything
   */
  public static void reserveSeats(
    Auditorium a,
    Node<Seat> startSeat,
    int numAdultTickets,
    int numChildTickets,
    int numSeniorTickets
  ) {
    Node<Seat> head = new Node<>();
    head = a.getFirst();
    int totalTicketsToRes =
      numAdultTickets + numChildTickets + numSeniorTickets;
    do {
      Node<Seat> cur = head;
      while (cur != null && totalTicketsToRes > 0) {
        Seat seat = (Seat) cur.getPayload();
        if (seat == startSeat.getPayload()) {
          if (numAdultTickets > 0) {
            seat.setType('A');
            cur.setPayload(seat);
            numAdultTickets--;
            totalTicketsToRes--;
          } else if (numChildTickets > 0) {
            seat.setType('C');
            cur.setPayload(seat);
            numChildTickets--;
            totalTicketsToRes--;
          } else if (numSeniorTickets > 0) {
            seat.setType('S');
            cur.setPayload(seat);
            numSeniorTickets--;
            totalTicketsToRes--;
          }
          startSeat = startSeat.getNext(); // increase the starting seat by 1 to get the next seat
        }
        cur = cur.getNext();
      }
      head = head.getDown();
    } while (head != null);
  }

  /*
   * function to find the closest distance from the center of the auditorium if a
   * customer's desired seat is
   * not available. returns a node
   * once closest seat is found, it will return the starting seat of the closest
   * seating arrangement
   * inputs:
   * a - auditorium
   * totalSeats - total number of seats the customer wants to reserve
   * returns:
   * closest seat
   */

  public static Node<Seat> findClosestSeats(
    Auditorium a,
    int totalSeats,
    int numRows,
    int numCols
  ) {
    // to find distance from center, use sqrt((x1-x2)^2 + (y1-y2)^2) where:
    // (x1, y1) = center of auditorium
    // (x2, y1) = reference point from the seat in the middle of the selection
    // keep distance variables as doubles
    // if distance is tied, use row closest to middle of auditorium
    // if a tie for closest row, use the row with the smallest number
    // if there is a tie within a row, use the lower seat letter
    Node<Seat> closestSeat = null;
    // center point:
    double y1 = numRows / 2.0; // Y is rows
    double x1 = numCols / 2.0; // X is cols
    double y2 = 0;
    double x2 = 0;
    double minDist = Double.MAX_VALUE;
    Node<Seat> head = new Node<>();
    head = a.getFirst();
    Node<Seat> cur = head;
    int rowNum = 1;
    int colNum = 1;
    // check the row and seat number, if it is available (doesnt have #), then seat
    // can be booked
    // use this method to check every seat
    // find the beginning node (seat wanting to be checked) and assign it to head
    do {
      cur = head;
      while (cur != null) {
        double selectX1 = colNum - 1;
        double selectX2 = colNum + (totalSeats - 1);
        x2 = ((selectX2 - selectX1) / 2.0) + selectX1;

        y2 = rowNum - 0.5;

        Node<Seat> temp = checkDesiredSeatsAvailable(
          a,
          rowNum,
          (char) (colNum + 64),
          totalSeats
        );
        if ((temp != null)) {
          double dist = Math.sqrt(
            Math.pow((double) (x1 - x2), 2) + Math.pow((double) (y1 - y2), 2)
          );
          // 1) if distance is tied, use row closest to middle of auditorium
          // 2) if a tie for closest row, use the row with the smallest number
          // 3) if there is a tie within a row, use the lower seat letter
          if (dist < minDist) {
            minDist = dist;
            closestSeat = temp;
          } else if (dist == minDist) {
            minDist = dist;
            double closestSeatY2 =
              ((Seat) closestSeat.getPayload()).getRow() - 0.5;
            double tempSeatY2 = ((Seat) temp.getPayload()).getRow() - 0.5;
            double closestSeatRowDelt = Math.abs(closestSeatY2 - y1);
            double tempSeatRowDelt = Math.abs(tempSeatY2 - y1);
            if (tempSeatRowDelt < closestSeatRowDelt) {
              /** tempSeat's row is closer to middle, so tempSeat is the NEW closest seat. */
              closestSeat = temp;
            } else if (tempSeatRowDelt == closestSeatRowDelt) {
              char closestSeatX2 = ((Seat) closestSeat.getPayload()).getSeat();
              char tempSeatX2 = ((Seat) temp.getPayload()).getSeat();
              /** tempSeat's row AND closestSeat's row are equidistant from center of auditorium. */
              if (tempSeatY2 < closestSeatY2) {
                /** tempSeat's row is the smaller row #, so we'll take it. */
                closestSeat = temp;
              } else if (tempSeatY2 == closestSeatY2) {
                /** tempSeat and closestSeat are in the same row. */
                closestSeat = temp;
                if (tempSeatX2 < closestSeatX2) {
                  /** tempSeat has the smaller seat #, so tempSeat is the NEW closest seat. */
                  closestSeat = temp;
                }
              }
            }
          }
        }
        cur = cur.getNext(); // if the seat is not the starting seat, move horizontally to next seat
        colNum++;
      }
      head = head.getDown();
      rowNum++;
      colNum = 1;
    } while (head != null);
    return closestSeat;
  }

  // function to write to output file:
  public static void printSeatsOutFile(Auditorium a, PrintWriter outSS) {
    Node<Seat> head = new Node<>();
    head = a.getFirst();
    // print seat letters (ABCDEF....)
    // numC starts at 1, not 0. Therefore, using i < numC - 1 to print all seat
    // letters
    do {
      Node<Seat> cur = head;
      while (cur != null) {
        Seat seat = (Seat) cur.getPayload();
        outSS.print("" + seat.getType());
        cur = cur.getNext();
      }
      outSS.println("");
      head = head.getDown();
    } while (head != null);
  }

  // function for printing the statistic on the reservations for the theater
  public static void displayStats(Auditorium a) {
    // variables to store the number of adult, child, and senior tickets
    int numberOfAdult = 0;
    int numberOfChild = 0;
    int numberOfSenior = 0;
    int totalTickets = 0;
    int numSeats = 0; // to store total number of seats
    float totalSales = 0;
    // loop through array and count the number of adult, child and senior tickets
    Node<Seat> head = new Node<>();
    head = a.getFirst();
    do {
      Node<Seat> cur = head;
      while (cur != null) {
        Seat seat = (Seat) cur.getPayload();
        if (seat.getType() == 'A') {
          numberOfAdult++;
          totalTickets++;
        } else if (seat.getType() == 'C') {
          numberOfChild++;
          totalTickets++;
        } else if (seat.getType() == 'S') {
          numberOfSenior++;
          totalTickets++;
        }
        numSeats++;
        cur = cur.getNext();
      }
      head = head.getDown();
    } while (head != null);

    // calculate the total sales:
    totalSales =
      (float) (ADULT_PRICE * numberOfAdult) +
      (float) (CHILD_PRICE * numberOfChild) +
      (float) (SENIOR_PRICE * numberOfSenior);

    System.out.println("Total Seats: " + numSeats);
    System.out.println("Total Tickets: " + totalTickets);
    System.out.println("Adult Tickets: " + numberOfAdult);
    System.out.println("Child Tickets: " + numberOfChild);
    System.out.println("Senior Tickets: " + numberOfSenior);
    System.out.printf("Total Sales: $%.2f\n", totalSales);
  }
}

// Cooper Kelley (clk200002)

import java.util.*;
public class Auditorium {

    private Node<Seat> first;         // acts as head pointer of linked list

    Auditorium() {
        first = null;
    }

    void setFirst(Node<Seat> f) {  first = f; }
    Node<Seat> getFirst() { return first; }

    // member function to add node horizontally
    void addNodeHoriz(Node<Seat> n) {
        if (first == null) {
            first = n;
        }
        else {
            Node<Seat> head = first;
            while (head.getDown() != null) {
                head = head.getDown();
            }
            Node<Seat> curr = head;
            while (curr.getNext() != null) {
                curr = curr.getNext();
            }
            curr.setNext(n);
            n.setPrev(curr);
            n.setNext(null);
        }
    }

    // member function to add node vertically
    void addNodeVert(Node<Seat> n) {
        if (first == null) {
            first = n;
        } else {
            Node<Seat> head = first;
            while (head.getDown() != null) {
                head = head.getDown();
            }
            head.setDown(n);
            n.setDown(null);
        }
    }

    // member function for printing the auditorium 
    void printAuditorium(Scanner s, String client) {
        Node<Seat> head = new Node<>();
        head = first;
        do {
            Node<Seat> cur = head;
            while (cur.getNext() != null) {
                Seat seat = (Seat) cur.getPayload();
                // for printing the customer file to be seen in the terminal
                // otherwise, the data remains unchanged in order to write to the output file
                if (client == "c") {
                    if (seat.getType() == '.') {
                        seat.setType('#');
                        
                    }
                }
                System.out.print(seat.getType());   // print out A, C, S, or .
                cur = cur.getNext();
            }
            System.out.println("");
            head = head.getDown();
        } while (head != null);
    }

}

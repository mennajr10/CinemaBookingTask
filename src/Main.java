import java.util.*;

class Task_CinemaBooking {
    record Seat(char row, int number) {

        @Override
            public String toString() {
                return "" + row + number;
            }

            // TODO 1: override equals()
            //   Two Seat objects are the same seat when the row AND the number are the same.
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Seat other)) return false;
                return row == other.row && number == other.number;
            }

    }

    static void main(String[] args) {


        HashMap<Seat, String> bookings = new HashMap<>();
        HashSet<Seat> broken = new HashSet<>();

        bookings.put(new Seat('A', 1), "Ali");
        bookings.put(new Seat('A', 2), "Sara");
        bookings.put(new Seat('B', 5), "Omar");
        bookings.put(new Seat('C', 3), "Mona");
        System.out.println("Bookings:           " + bookings);


        System.out.println("Who booked C3:      " + bookings.get(new Seat('C', 3)));

        System.out.println("Is A2 taken:        " + bookings.containsKey(new Seat('A', 2)));
        System.out.println("Is E6 taken:        " + bookings.containsKey(new Seat('E', 6)));

        String previous = bookings.putIfAbsent(new Seat('B', 5), "Youssef");
        if (previous == null) {
            System.out.println("Youssef -> B5:      booked");
        } else {
            System.out.println("Youssef -> B5:      refused, taken by " + previous);
        }

        String ali = bookings.remove(new Seat('A', 1));
        bookings.put(new Seat('D', 2), ali);
        System.out.println("After Ali moved:    " + bookings);

        broken.add(new Seat('E', 1));
        broken.add(new Seat('E', 2));
        if (broken.contains(new Seat('E', 1))) {
            System.out.println("Hana -> E1:         refused, seat is broken");
        } else {
            bookings.put(new Seat('E', 1), "Hana");
            System.out.println("Hana -> E1:         booked");
        }

        System.out.println("Seats booked:       " + bookings.size());

        System.out.println();
        printChart(bookings, broken);
    }

    static void printChart(HashMap<Seat, String> bookings, HashSet<Seat> broken) {
        System.out.println("     1  2  3  4  5  6");
        for (char row = 'A'; row <= 'E'; row++) {
            System.out.print(row + "  ");
            for (int number = 1; number <= 6; number++) {
                Seat seat = new Seat(row, number);
                if (bookings.containsKey(seat)) {
                    System.out.print("  X");
                } else if (broken.contains(seat)) {
                    System.out.print("  #");
                } else {
                    System.out.print("  .");
                }
            }
            System.out.println();
        }
    }
}

/*
 QUESTIONS - answers

 Q1. Why did we have to override hashCode() and equals()?

   _ cause we use newSeat every time , this means new object in memory
     and default equals compares memory address not value so newSeat not equals
     the stored one .
   _ also hashmap distributes key among buckets according to hashcode
   _ equals compares row and num , and hashcode ensures that equal seats
     go to same bucket .


 Q2. Delete your hashCode() but keep equals(), and run again.
     What happens? Why?

 _ get for C3 returned null ,containsKey for A2 returned false,
   B5 was booked twice (by Omar and Youssef), moving Ali left A1 as is and added D2 with a null value,
   and the count became 7 instead of 4
 _ cause hashmap firstly calculates hashcode then to choose  bucket call equals inside it
   without right hashcode new seat goes to another bucket and equals not called .



 Q3. Why are row and number final? What could go wrong if the number
     of a booked seat could be changed after it was put in the map?

   _ final makes seat static ,
     hashcode is calculated from row and num while put
     if num changed the hashcode changes but element will stay
     in old bucket so booking lost inside map even though it exists .



 Q4. Change equals(Object o) to equals(Seat o). What happens?
     Then also remove @Override and run again. What happens now?

  _ with @Override, the code doesn't even compile and throws error ,
    because equals(Seat) doesn't match Object.equals(Object) .
  _ if we remove @Override, the code compiles fine,
    but the map breaks again
    hashmap always calls equals(Object) the default comparison and our
    overload isn't used at all.



 BONUS. Replace the whole Seat class with this one line:
            record Seat(char row, int number) {}

     _ Everything works as expected—bookings, lookups, and the seating
      chart all match the expected output (the only difference is that
      keys print as Seat[row=A, number=1] instead of A1).
      java record automatically makes row and number final and generates the constructor,
      equals(), hashCode(), and toString() behind the scenes using both fields.


*/

import java.util.*;

class Task_CinemaBooking {
    static class Seat {
        final char row;
        final int number;

        Seat(char row, int number) {
            this.row = row;
            this.number = number;
        }

        @Override
        public String toString() {
            return "" + row + number;
        }

        // TODO 1: override equals()
        //   Two Seat objects are the same seat when the row AND the number are the same.
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Seat)) return false;
            Seat other = (Seat) o;
            return row == other.row && number == other.number;
        }

        // TODO 2: override hashCode()
        //   Equal seats must return the same number.
        @Override
        public int hashCode() {
            return Objects.hash(row, number);                 // uses the same fields as equals()
        }
    }

    public static void main(String[] args) {


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

   Because every seat lookup in the task uses a NEW Seat object
   (new Seat('C', 3)), not the same object that was put in the map.
   The default equals() inherited from Object only compares references
   (memory addresses), so a new Seat('C', 3) would never be "equal" to
   the Seat('C', 3) already stored, and get()/containsKey() would fail.
   HashMap also groups keys into buckets using hashCode(); if two equal
   seats return different hash codes, the map looks in the wrong bucket
   and never even calls equals(). So we override equals() to compare the
   CONTENT (row and number), and hashCode() so equal seats always land
   in the same bucket. The rule: equal objects must have equal hash codes.


 Q2. Delete your hashCode() but keep equals(), and run again.
     What happens? Why?

   The map still misbehaves: get(C3) returns null and containsKey(A2)
   is false, exactly as if equals() were missing.
   Why: HashMap first computes hashCode() of the lookup key to pick a
   bucket, then only calls equals() on keys inside that bucket. With the
   default Object.hashCode(), the new Seat('C', 3) gets a different hash
   than the stored Seat('C', 3), so the map searches a different bucket
   and our equals() is never called. equals() alone is useless without a
   matching hashCode().


 Q3. Why are row and number final? What could go wrong if the number
     of a booked seat could be changed after it was put in the map?

   final makes a Seat immutable after construction. The hash code is
   computed from row and number at the moment the seat is put() into the
   map, and the map stores it in the bucket for that hash. If number
   could change Afterwards, the object's hashCode() would change, but
   the entry would stay in the OLD bucket. Any later get()/containsKey()
   would compute the NEW hash, search the NEW bucket, and never find the
   entry. The booking would be lost inside the map (a memory/logic leak)
   and two entries could even end up representing the same seat.


 Q4. Change equals(Object o) to equals(Seat o). What happens?
     Then also remove @Override and run again. What happens now?

   With equals(Seat o) and @Override still present, the code does NOT
   compile: "method does not override or implement a method from a
   supertype". Object declares equals(Object), so a parameter of type
   Seat is not an override; it is an OVERLOAD (a second, different
   method). @Override correctly catches the mistake.
   After removing @Override it compiles, but the map is broken again:
   HashMap always calls equals(Object), i.e. the default reference
   comparison from Object, and our equals(Seat) overload is never used
   by the map. Lookups with new Seat objects fail again.


 BONUS. Replace the whole Seat class with this one line:
            record Seat(char row, int number) {}

   The program compiles and produces the same correct output.
   A record automatically generates a constructor, accessors row() and
   number(), and correct implementations of equals(), hashCode() and
   toString() based on ALL its components (row and number). The fields
   are implicitly final, so the key is immutable. toString() changes
   slightly ("Seat[row=B, number=5]" instead of "B5"), which only
   affects how the map is printed, not the logic. So a record gives us
   exactly the value-based equality and hashing we wrote by hand, for
   free, which is why records are ideal as map keys.
*/

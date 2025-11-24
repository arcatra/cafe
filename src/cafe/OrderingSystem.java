package cafe;

// Imports -------------------------------
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
// ---------------------------------------

public class OrderingSystem {

    // console ANCI codes: constant variables
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String PURPLE = "\u001B[35m";

    public static final String BRIGHTPINK = "\u001B[35;1m";
    public static final String BRIGHTYELLOW = "\u001B[33;1m";
    public static final String BRIGHTRED = "\u001B[31;1m";

    // -------------------------------------------------------
    ArrayList<MenuItem> menu = new ArrayList<>();
    HashMap<Integer, Integer> userOrderList = new HashMap<>();
    int menuSize;
    String userName;
    // -------------------------------------------------------

    public void displayMenu() {
        System.out.println(RESET);
        this.colorConsole("\n\n\t\t--^-- ourMenu --^--\n", BRIGHTYELLOW, true);

        String formatString = PURPLE + "%-5s %-20s %-12s %-15s\n";
        System.out.printf(formatString, "ID", "Name", "Price", "Available?" + RESET);

        for (MenuItem item : this.menu) {
            formatString = CYAN + "%-5d %-20s %-12.2f %-15s\n";
            System.out.printf(formatString, item.id, item.itemName, item.price, item.available);
        }

        return;
    }

    private void greetUser(Scanner inputScanner) {
        System.out.print(RESET + "\nPlease enter your name: ");
        this.userName = inputScanner.nextLine();

        while (!(this.userName.matches("[a-zA-Z]+"))) {
            System.out.print("Looks like your name isn't valid!, enter a valid name: ");
            this.userName = inputScanner.nextLine();
        }

        this.userName = this.userName.toUpperCase();

        System.out.println(BRIGHTRED + "\nHello " + this.userName + ", How are you!" + RESET);
        return;
    }

    public void takeOrder(int breakPoint, Scanner userInput) {
        int orderId;

        System.out.println(BRIGHTPINK + "\nWhat do you like to order? Enter item's ID (ex: 2 [press enter]....)");
        System.out.print("or enter " + breakPoint + " to EXIT from ordering: " + RESET);

        while (true) {
            try {
                orderId = userInput.nextInt();
                userInput.nextLine();

                if (orderId == breakPoint) {
                    break;
                }

                if (this.userOrderList.containsKey(orderId)) {
                    this.userOrderList.replace(orderId, this.userOrderList.get(orderId) + 1);

                    System.out.println("Done!, added +1 to existing " + this.menu.get(orderId).itemName + " quantity");
                    System.out.printf("Now you have %d %s(s) in your oder\n\n", this.userOrderList.get(orderId),
                            this.menu.get(orderId).itemName);

                } else if (orderId <= this.menuSize && orderId >= 0) {
                    System.out.printf("how many %s's you want?: ", this.menu.get(orderId).itemName);

                    int quantity = userInput.nextInt();
                    userInput.nextLine();

                    if (quantity > 0) {
                        this.userOrderList.put(orderId, quantity);

                    } else {
                        System.out.println("No.. the qantity should be > 0\n");
                    }

                } else {
                    System.out.println("This isn't a valid item ID, should be >= 0 and <= " + (this.menuSize - 1)
                            + ", and exactly " + breakPoint + " to stop ordering");

                }

                System.out.print("Next: ");

            } catch (InputMismatchException e) {
                System.out.println("Invalid Input: item ID's are Numbers/Integers not String");
                userInput.nextLine(); // because when a mismatch input is provided for orderId then next
                                      // - line that is meant to consume the new line character is executing

                System.out.print("Try again: ");
            }
        }

        return;
    }

    public void confirmOrder(HashMap<Integer, Integer> orderList, Scanner userInput) {
        String choice;

        this.colorConsole("\nConfirm your order!\n", BRIGHTPINK, true);
        System.out.println("\nOrder list (ID=quty): " + orderList.toString());
        this.parseOrder(orderList);

        this.colorConsole("\nDo you want anything else? (y/n)('y' to add): ", BRIGHTYELLOW, true);
        choice = userInput.nextLine().toLowerCase();

        if ("y".equals(choice)) {
            this.displayMenu();
            this.takeOrder((this.menuSize), userInput);
            this.colorConsole("\nConfirm your order!\n", BRIGHTPINK, true);
            this.parseOrder(orderList);
        }

        this.colorConsole("\nDo you want to proceed for billing (y/n)('n' to update): ", BRIGHTYELLOW, true);
        choice = userInput.nextLine().toLowerCase();

        System.out.println("");

        if ("n".equals(choice)) {
            this.colorConsole("\nYou're updating order now\n", PURPLE, true);

            this.updateOrder(orderList, userInput, this.menuSize);
        }

        this.colorConsole("\nPrinting Bill\n", BRIGHTPINK, true);

        return;
    }

    public void updateOrder(HashMap<Integer, Integer> orderList, Scanner userInput, int breakPoint) {
        this.displayMenu();
        System.out.println(RESET + "\nYour ORDER LIST is(ID=quty): " + orderList.toString());
        System.out.println(BRIGHTPINK + "\nPlease enter the existing ITEM ID, to update with new ITEM ID");
        System.out.println("or enter " + breakPoint + " to EXIT from updating order\n" + RESET);

        while (true) {
            System.out.print("Existing item ID in your order list: ");
            int oldItemId = userInput.nextInt();
            userInput.nextLine();

            if (oldItemId == breakPoint) {
                break;
            }

            int itemQunty = orderList.get(oldItemId);
            String oldItemName = this.menu.get(oldItemId).itemName;
            if (orderList.containsKey(oldItemId)) {
                System.out.printf(
                        "\nOkay, %d %s%s in your order list, what do you want to do?\n%s\n%s\n%s\n\nEnter choice number: ",
                        itemQunty,
                        oldItemName,
                        itemQunty > 1 ? "'s" : "",
                        "1. Delete from order list",
                        "2. Replace with new (only one at a time)",
                        "3. Remove this entire item from order list");
                int choice = userInput.nextInt();
                userInput.nextLine();

                if (choice == 1) {
                    this.colorConsole("\nchoice: Delete item\n", CYAN, true);
                    if (itemQunty >= 1) {
                        System.out.printf("\nHow many %s's you want to delete?(should be <= %d, >= 1): ", oldItemName,
                                itemQunty);
                        int removeQunty = userInput.nextInt();
                        userInput.nextLine();

                        if (removeQunty < itemQunty && removeQunty > 0) {
                            orderList.replace(oldItemId, (itemQunty - removeQunty));

                        } else if (removeQunty == itemQunty) {
                            orderList.remove(oldItemId);

                        } else {
                            System.out.println("\nInvalid quantity, try again\n");
                        }

                        System.out.printf("Done, deleted %d %s%s from your order list\n\n", removeQunty, oldItemName,
                                removeQunty > 1 ? "'s" : "");
                    }

                } else if (choice == 2) {
                    this.colorConsole("\nchoice: Replace an item with new\n", CYAN, true);

                    System.out.print("New item ID: ");
                    int newItmeId = userInput.nextInt();
                    userInput.nextLine();

                    if (newItmeId < this.menuSize) {
                        if (itemQunty > 1) {
                            orderList.replace(oldItemId, itemQunty - 1);

                        } else {
                            orderList.remove(oldItemId);

                        }

                        orderList.put(newItmeId, 1);
                        // Every time user select to replace an item we need to reduce the quantity of
                        // -old order by 1 or completly remove the oldorder if it's current quantity is
                        // only 1 so in both cases we need to update the order list with the new order
                        // -replace exactly one, and remove exatly one

                        System.out.printf("Done!, replaced 1 %s with 1 %s\n\n", oldItemName,
                                this.menu.get(newItmeId).itemName);

                    } else {
                        System.out.printf("Nope, item ID's should always be < %d and >= 0\n\n", breakPoint);
                        System.out.printf(
                                "The current old item %s isn't been replaced or deleted from your order. \nSo try again\n\n",
                                oldItemName);
                    }

                } else {
                    this.colorConsole("\nchoice: Remove this entire item from order list\n", CYAN, true);
                    orderList.remove(oldItemId);
                    System.out.println("Done, this item is been removed from your order list\n");

                }

                System.out.println(RESET + "Your updated order list is(ID=quty): " + orderList.toString());

            } else {
                System.out.println("\nThe item ID " + oldItemId
                        + " didn't exist's in your order, check and enter again!\n");

            }
        }

        return;
    }

    public void printBill(HashMap<Integer, Integer> orderList) {

        System.out.println(BRIGHTRED);

        System.out.printf("%-18s %-1s\n", "Item Name", "Price");

        float total = this.parseOrder(orderList);

        System.out.println("--------------------------------");
        System.out.printf("%-19s", "Total");
        System.out.printf("Rs %-1.2f %-1s", total, "/-");
        System.out.println("\n--------------------------------");
        System.out.println(RESET);

        return;
    }

    // Helper methods ---------------------------------------

    private final float parseOrder(HashMap<Integer, Integer> orderList) {
        float total = 0.0f;
        System.out.println("");
        for (int id : orderList.keySet()) {
            MenuItem item = this.menu.get(id);

            int quantity = orderList.get(id);
            String itemNameQuty = item.itemName + " x " + quantity;
            String price = "Rs " + String.format("%.2f", (item.price * quantity)) + " /-";

            System.out.printf("%-18s %-1s\n", itemNameQuty, price);
            total += (item.price * quantity);
        }

        return total;
    }

    private final void colorConsole(String message, String color, Boolean resetAfter) {
        if (resetAfter) {
            System.out.printf("%s%s%s", color, message, RESET);

        } else {
            System.out.printf("%s%s", color, message);

        }
    }
    // ------------------------------------------------------

    public static void main(String[] args) {
        OrderingSystem cafeObj = new OrderingSystem();

        cafeObj.menu.add(new MenuItem(0, "Tea", true, 80.20f));
        cafeObj.menu.add(new MenuItem(1, "Matcha", true, 120.12f));
        cafeObj.menu.add(new MenuItem(2, "Coffee", true, 80.0f));
        cafeObj.menu.add(new MenuItem(3, "Filter Coffie", true, 120.12f));
        cafeObj.menu.add(new MenuItem(4, "Burger", true, 180.99f));
        cafeObj.menu.add(new MenuItem(5, "Sandwich", true, 150.60f));
        cafeObj.menu.add(new MenuItem(6, "Masala Tea", true, 90.14f));

        Scanner userIn = new Scanner(System.in);
        cafeObj.menuSize = cafeObj.menu.size();

        cafeObj.displayMenu();
        cafeObj.greetUser(userIn); // only limited to cafe class
        cafeObj.takeOrder((cafeObj.menuSize), userIn);

        if (cafeObj.userOrderList.size() > 0) {

            cafeObj.confirmOrder(cafeObj.userOrderList, userIn);
            cafeObj.printBill(cafeObj.userOrderList);

            System.out.println("Your order will get ready shortly " + cafeObj.userName + "\n");
        } else {
            System.out.println("Alright, visit us again, bye!");
        }
        userIn.close();
    }
}

// ----------------------------------------

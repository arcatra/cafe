package cafe;

// Imports -------------------------------
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import cafe.helpers.*;

import java.math.BigDecimal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class OrderingSystem {

    // console ANCI codes: constant variables
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String PURPLE = "\u001B[35m";

    public static final String BRIGHTPINK = "\u001B[95m";
    public static final String BRIGHTYELLOW = "\u001B[93m";
    public static final String BRIGHTRED = "\u001B[31m";

    // -------------------------------------------------------
    protected static ArrayList<MenuItem> menu = new ArrayList<>();
    protected static Map<Integer, Integer> userOrderList = new HashMap<>();
    protected static String userName;
    private int menuSize;
    // -------------------------------------------------------

    public void buildMenu() {
        Map<Integer, String[]> parsedMenuItems = this.extractMenuItems("src/menuItems.txt");

        if (parsedMenuItems.size() > 0) {
            for (int itemId : parsedMenuItems.keySet()) {
                String[] itemStrings = parsedMenuItems.get(itemId);

                MenuItem item = new MenuItem(
                        itemId, itemStrings[0].strip(),
                        Boolean.parseBoolean(itemStrings[1].strip()),
                        new BigDecimal(itemStrings[2].strip()));

                menu.add(item);
            }

            System.out.println("Completed building the Menu\nDisplaying the menu");

        } else {
            System.out.println("\nThere are no items in menuItems.txt file, add some to create menu\n");
        }

    }

    public void displayMenu() {
        System.out.println(RESET);
        this.colorConsole("\n\t\t--^-- ourMenu --^--\n", BRIGHTYELLOW, true);

        String formatString = PURPLE + "%-5s %-20s %-12s %-15s\n";
        System.out.printf(formatString, "ID", "Name", "Price", "Available?" + RESET);

        for (MenuItem item : menu) {
            formatString = CYAN + "%-5d %-20s %-12.2f %-15s\n";
            System.out.printf(formatString, item.id, item.itemName, item.price, item.available);
        }

    }

    public void greetUser(Scanner inputScanner) {
        System.out.print(RESET + "\nPlease enter your name: ");
        userName = inputScanner.nextLine();

        while (!(userName.matches("[a-zA-Z]+"))) {
            System.out.print("Looks like your name isn't valid!, enter a valid name: ");
            userName = inputScanner.nextLine();
        }

        userName = userName.toUpperCase();

        System.out.println(BRIGHTRED + "\nHello " + userName + ", How are you!" + RESET);

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

                if (userOrderList.containsKey(orderId)) {
                    userOrderList.replace(orderId, userOrderList.get(orderId) + 1);

                    System.out.println("Done!, added +1 to existing " + menu.get(orderId).itemName + " quantity");
                    System.out.printf(
                            "Now you have %d %s%s in your oder\n\n",
                            userOrderList.get(orderId),
                            menu.get(orderId).itemName,
                            userOrderList.get(orderId) > 1 ? "'s" : "");

                } else if (orderId <= this.menuSize && orderId >= 0) {
                    System.out.printf("how many %s's you want?: ", menu.get(orderId).itemName);

                    int quantity = userInput.nextInt();
                    userInput.nextLine();

                    if (quantity > 0) {
                        userOrderList.put(orderId, quantity);

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
                userInput.nextLine(); // Cosume the left over

                System.out.print("Try again: ");
            }
        }

    }

    public void confirmOrder(Map<Integer, Integer> orderList, Scanner userInput) {
        String choice;

        this.colorConsole("\nConfirm your order!\n", BRIGHTPINK, true);
        this.displayOrderList(orderList);

        this.colorConsole("\nDo you want anything else? (y/n)('y' to add): ", BRIGHTYELLOW, true);
        choice = userInput.nextLine().toLowerCase();

        if ("y".equals(choice)) {
            this.displayMenu();
            this.takeOrder((this.menuSize), userInput);
            this.colorConsole("\nConfirm your order!\n", BRIGHTPINK, true);
            this.displayOrderList(orderList);
            
        }

        this.colorConsole("\nDo you want to proceed for billing (y/n)('n' to update): ", BRIGHTYELLOW, true);
        choice = userInput.nextLine().toLowerCase();

        System.out.println("");

        if ("n".equals(choice)) {
            this.colorConsole("\nYou're updating order now\n", PURPLE, true);

            this.updateOrder(orderList, userInput, this.menuSize);
        }

        this.colorConsole("\nPrinting Bill\n", BRIGHTPINK, true);

    }

    public void updateOrder(Map<Integer, Integer> orderList, Scanner userInput, int breakPoint) {
        this.displayMenu();
        System.out.println(BRIGHTPINK + "\nPlease enter the existing ITEM ID, to update with new ITEM ID");
        System.out.println("or enter " + breakPoint + " to EXIT from updating order\n" + RESET);

        int oldItemId;

        while (true) {
            oldItemId = this.getItemId(userInput, "Enter old item ID: ");

            if (oldItemId == breakPoint)
                break;

            if (orderList.containsKey(oldItemId)) {
                MenuItem item = menu.get(oldItemId);
                int itemQunty = orderList.get(oldItemId);
                int choice = this.getUserUpdateChoice(item.itemName, itemQunty, userInput);

                switch (choice) {
                    case 1:
                        this.deleteFromOrderList(orderList, userInput, item, itemQunty);
                        break;

                    case 2:
                        this.replaceInOrderList(orderList, userInput, item, itemQunty);
                        break;

                    case 3:
                        this.removeEntireItemFromOrderList(orderList, oldItemId);
                        break;

                    default:
                        System.out.println("Invalid input, Try again\n");
                }

                this.displayOrderList(orderList);

            } else {
                System.out.println("\nThe item ID " + oldItemId
                        + " didn't exist's in your order, check and enter again!\n");

            }
        }
    }

    public void printBill(Map<Integer, Integer> orderList) {

        System.out.println(BRIGHTRED);

        System.out.printf("%-18s %-1s\n", "Item Name", "Price");

        this.displayOrderList(orderList);
        double total = this.getOrderTotal(orderList);

        System.out.println("--------------------------------------------");
        System.out.printf("%-26s", "Total");
        System.out.printf("Rs %-1.2f %-1s", total, "/-");
        System.out.println("\n--------------------------------------------");
        System.out.println(RESET);

    }

    // Helper methods ---------------------------------------

    private final Map<Integer, String[]> extractMenuItems(String sourceFile) {
        Map<Integer, String[]> parsedItems = new HashMap<>();

        if (sourceFile.isBlank())
            return parsedItems;

        try (BufferedReader buffer = new BufferedReader(new FileReader(sourceFile))) {

            String line;
            int id = 0;

            while ((line = buffer.readLine()) != null) {
                parsedItems.put(id, line.split(","));
                id++;

            }

            // auto closes buffer
        } catch (IOException e) {
            System.out.println("Error occured: " + e.getMessage());

        }

        return parsedItems;
    }

    // Displaying the order details before BILL ----------------------

    private void displayOrderList(Map<Integer, Integer> orderList) {
        if (orderList.size() < 1) {
            System.out.println("Error: no orders found to display\n");
            return;

        }

        System.out.println("Your order list: ");

        for (var itemEntry : orderList.entrySet()) {
          int id = itemEntry.getKey();

          MenuItem item = menu.get(id);
          BigDecimal quantity = new BigDecimal(itemEntry.getValue());

          String itemNameAndQuty = item.itemName + " x " + quantity;
          String price = "Rs " + String.format("%.2f", (item.price.multiply(quantity)).doubleValue()) + " /-";

          System.out.printf("ID: %d, %-18s %-1s\n", id, itemNameAndQuty, price);
        }

    }

    private final double getOrderTotal(Map<Integer, Integer> orderList) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (var itemEntry : orderList.entrySet()) {
            BigDecimal itemPrice = menu.get(itemEntry.getKey()).price;
            BigDecimal currentItemPrice = new BigDecimal(itemEntry.getValue()).multiply(itemPrice);

            totalPrice = totalPrice.add(currentItemPrice);

        }

        return totalPrice.doubleValue();
    }

    // --------------------------------------------------

    private final void colorConsole(String message, String color, Boolean resetAfter) {
        if (resetAfter) {
            System.out.printf("%s%s%s", color, message, RESET);

        } else {
            System.out.printf("%s%s", color, message);

        }
    }

    // helper methods of updateOrder --------

    public final int getItemId(Scanner userInput, String message) {
        int id;

        while (true) {
            System.out.print(message);
            String ItemId = userInput.nextLine();
            try {
                id = Integer.parseInt(ItemId);
                break;

            } catch (InputMismatchException e) {
                System.out.println("Nope.., The ID should be a number! try again\n");

            } catch (Exception e) {
                System.out.println("Error occured, try again\n");

            }
        }

        return id;
    }

    private final int getUserUpdateChoice(String itemName, int itemQunty, Scanner userInput) {
        System.out.printf(
                "\nOkay, %d %s%s in your order list, what do you want to do?\n1. %s\n2. %s\n3. %s\n\nEnter choice number: ",
                itemQunty,
                itemName,
                itemQunty > 1 ? "'s" : "",
                "Delete some from order list",
                "Replace with new (only one at a time)",
                "Remove this entire item from order list");
        int chId;

        while (true) {
            String choice = userInput.nextLine();

            try {
                chId = Integer.parseInt(choice);
                break;

            } catch (Exception e) {
                System.out.println("Nope, invalid choice!, try again\n");

            }

        }

        return chId;
    }

    private final void deleteFromOrderList(Map<Integer, Integer> orderList, Scanner userInput, MenuItem item,
            int itemQunty) {

        this.colorConsole("\nchoice: Delete item\n", CYAN, true);

        if (itemQunty >= 1) {
            System.out.printf("\nHow many %s's you want to delete?(<= %d, >= 1): ", item.itemName,
                    itemQunty);
            int removeQunty = userInput.nextInt();
            userInput.nextLine();

            if (removeQunty < itemQunty && removeQunty > 0) {
                orderList.replace(item.id, (itemQunty - removeQunty));

            } else if (removeQunty == itemQunty) {
                orderList.remove(item.id);

            } else {
                System.out.println("\nInvalid quantity, try again\n");

            }

            System.out.printf("Done, deleted %d %s%s from your order list\n\n", removeQunty, item.itemName,
                    removeQunty > 1 ? "'s" : "");
        }
    }

    private final void replaceInOrderList(Map<Integer, Integer> orderList, Scanner userInput, MenuItem item,
            int itemQunty) {

        this.colorConsole("\nchoice: Replace with new\n", CYAN, true);

        System.out.print("New item ID: ");
        int newItmeId = userInput.nextInt();
        userInput.nextLine();

        if (newItmeId < this.menuSize) {

            if (orderList.containsKey(newItmeId)) {
                orderList.replace(newItmeId, orderList.get(newItmeId) + 1);

            } else {
                orderList.put(newItmeId, 1);

            }

            orderList.replace(item.id, orderList.get(item.id) - 1);

            // Every time user select to replace an item we need to reduce the quantity of
            // -old order by 1 or completly remove the oldorder if it's current quantity is
            // only 1 so in both cases we need to update the order list with the new order
            // -replace exactly one, and remove exatly one

            System.out.printf("Done!, replaced 1 %s with 1 %s\n\n", item.itemName,
                    menu.get(newItmeId).itemName);

        } else {
            System.out.printf("Nope, item ID's should always be < %d and >= 0\n\n", this.menuSize);
            System.out.printf(
                    "The current old item %s isn't been replaced or deleted from your order. \nSo try again\n\n",
                    item.itemName);
        }
    }

    private final void removeEntireItemFromOrderList(Map<Integer, Integer> orderList, int itemId) {

        this.colorConsole("\nchoice: Remove this entire item from order list\n", CYAN, true);
        orderList.remove(itemId);
        System.out.println("Done, this item is been removed from your order list\n");
    }
    // ---------------------------------

    // ------------------------------------------------------

    public static void main(String[] args) {
        OrderingSystem cafeObj = new OrderingSystem();
        cafeObj.buildMenu();

        Scanner userIn = new Scanner(System.in);
        cafeObj.menuSize = OrderingSystem.menu.size();

        cafeObj.displayMenu();
        cafeObj.greetUser(userIn);
        cafeObj.takeOrder((cafeObj.menuSize), userIn);

        if (OrderingSystem.userOrderList.size() > 0) {

            cafeObj.confirmOrder(OrderingSystem.userOrderList, userIn);
            cafeObj.printBill(OrderingSystem.userOrderList);

            System.out.println("Your order will get ready shortly " + OrderingSystem.userName +
                    "\n");
        } else {
            System.out.println("Alright, visit us again, bye!");
        }
        userIn.close();
    }
}

// ----------------------------------------

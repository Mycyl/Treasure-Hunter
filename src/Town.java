/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */


public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private String treasure;
    private String[] treasureList;
    private boolean searchedForTreasure;
    public static String[] treasureCollected = new String[3];
    private boolean hasDugInCurrentTown = false;
    private static int numDig = 0;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        searchedForTreasure = false;
        treasureList = new String[] {"crown", "trophy", "gem", "dust"};
        treasure = treasureList[(int) (Math.random() * (4))];

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public void resetPrintMessage () {
        printMessage = "";
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    public void searchForTreasure () {
        resetPrintMessage();
        if (!searchedForTreasure) {
            searchedForTreasure = true;

            if (!treasureFoundAlready(treasure)) {
                if (freeIndex() != -1 && !treasure.equals("dust")) {
                    treasureCollected[freeIndex()] = treasure;
                }
                printMessage += "\nyou found a " + treasure + " !";
            } else {
                printMessage += "\nyou have found a " + treasure + " already!";
            }
        } else {
            printMessage += "\nyou have already searched this town";
        }
    }

    public int freeIndex () {
        for (int i = 0; i < treasureCollected.length; i++) {
            if (treasureCollected[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public boolean treasureFoundAlready (String treasure) {
        for (int i = 0; i < treasureCollected.length; i++) {
            if (treasureCollected[i] != null && treasureCollected[i].equals(treasure)) {
                return true;
            }
        }
        return false;
    }

    public boolean userWon () {
        for (int i = 0; i < treasureCollected.length; i++) {
            if (treasureCollected[i] == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown(boolean easyMode) {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (!easyMode && checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public boolean lookForTrouble(boolean hasSword) {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Color.ANSI_RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Color.ANSI_RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance || hasSword) {
                if (hunter.hasSword()) {
                    printMessage += "the brawler, seeing your sword, realizes he picked a losing fight and gives you his gold";
                } else {
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                }

                printMessage += "\nYou won the brawl and receive " + Color.ANSI_YELLOW + goldDiff + " gold." +Color.ANSI_RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + Color.ANSI_YELLOW + goldDiff + " gold." +Color.ANSI_RESET;
                hunter.changeGold(-goldDiff);
                if (hunter.getGold() < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasTreasure () {
        return treasureCollected[0] != null;
    }

    public void setDugInTown(boolean status){
        this.hasDugInCurrentTown = status;
    }
    public boolean hasDugInTown(){
        return this.hasDugInCurrentTown;
    }

  public void digForGold() {
        resetPrintMessage();
        if (!hunter.hasItemInKit("shovel")) {
            printMessage += "\nYou can't dig for gold without a shovel.";
        }
        if (hasDugInTown()) {
            printMessage += "\nYou already dug for gold in this town.";
        }
        double rnd = Math.random();
        double rnd2 = Math.random();
        if (hunter.hasItemInKit("shovel")) {
            if (!hasDugInTown()) {
                if (rnd > 0.5) {
                    int goldAmount = (int)(Math.random() * 20) + 1;
                    hunter.changeGold(goldAmount);
                    printMessage += "\nYou dug up " + goldAmount + " gold";
                } else {
                    printMessage += "\nYou dug but only found dirt.";
                }
            }
        } else {
            printMessage += "\nYou can't dig for gold without a shovel";
        }
        hasDugInCurrentTown = true;
    }


    public String infoString() {
        String infoString = "";
        if (hasTreasure()) {
            infoString += "Treasures found:";
            for (int i = 0; i < treasureCollected.length && !(treasureCollected[i] == null); i++) {
                infoString += " a " + treasureCollected[i];
            }
        } else {
            infoString += "Treasures found: none";
        }
        infoString += "\nThis nice little town is surrounded by " + terrain.getTerrainName() + ".";
        return infoString;
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .16) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .32) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .48) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .64) {
            return new Terrain("Desert", "Water");
        } else if (rnd < 0.8){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}
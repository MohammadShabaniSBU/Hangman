import java.util.Scanner;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Console;

public class HangMan {
    public static void main(String[] args) {

        Load.load();

        boolean status = true;

        while (status) {

            status = StartUpPage.display();
            Save.saveAll();
            Save.saveLeaderBoard();

        }
    }
}

class MyConsole {
    private static String[] colors = {"default", "red", "boldRed", "green", "boldGreen", "yellow", "boldYellow",
            "blue", "boldBlue", "purple", "boldPurple"};
    private static String[] colorCodes = {"[0m", "[0;31m", "[1;31m", "[0;32m", "[1;32m", "[0;33m", "[1;33m",
            "[0;34m", "[1;34m", "[0;35m", "[1;35m"};

    public static void changeColor(String color) {

        int index = searchColor(color);

        if (index == -1)
            return;

        System.out.printf("\033%s", colorCodes[index]);

    }

    public static void cursorGoTo(int x, int y) {

        char c = 0x1B;
        System.out.printf("%c[%d;%df", c, x, y);

    }

    public static void clearScreen() {

        System.out.print("\033[H\033[2J");

    }

    private static int searchColor(String color) {

        int index = -1;

        for (int i = 0; i < colors.length; i++)
            if (color.equals(colors[i]))
                index = i;

        return index;
    }

    public static void print(int x, int y, String color, String message) {

        MyConsole.cursorGoTo(x, y);
        MyConsole.changeColor(color);
        System.out.print(message);
        MyConsole.changeColor("default");

    }
/*
    public static int getLines() {
        return Integer.parseInt(System.getenv("LINES"));        // getting count of lines and columns of the window (only Unix system)
    }

    public static int getColumns() {
        return Integer.parseInt(System.getenv("COLUMNS"));
    }
 */
}

class User {
    private String username;
    private String password;
    private int score;

    User(String username, String password) {
        this(username, password, 0);
    }

    User(String username, String password, int score) {
        
        this.username = username;
        this.password = password;
        this.score = score;
        
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getScore() {
        return score;
    }

    public void win() {
        this.score += 5;
    }

    public boolean getARandomLetter() {
        
        if (this.score < 10)
            return false;
        
        this.score -= 10;
        
        return true;
    }
}

class Users {
    private static Users instance;
    private User[] users;
    private int size;
    private int count;

    private Users() {
        this.size = 2;
        this.count = 0;
        this.users = new User[this.size];
    }

    public static Users getInstance() {
        if (instance == null)
            instance = new Users();

        return instance;
    }

    public User[] getUsers() {
        
        User[] temp = new User[this.count];
        
        for (int i = 0; i < this.count; i++)
            temp[i] = this.users[i];
        
        return temp;
    
    }

    public int getCount() {
        return count;
    }

    public void add(User user) {
        
        if (size == count) {
            
            User[] temp = new User[2 * size];
            
            for (int i = 0; i < count; i++)
                temp[i] = this.users[i];
            
            this.users = temp;
            size *= 2;
            
        }
        
        this.users[count++] = user;
    }

    public boolean checkUsername(String username) {
        
        for (int i = 0; i < this.count; i++)
            if (username.equals(this.users[i].getUsername()))
                return false;
            
        return true;
    }

    private void sort() {
        
        for (int i = 0; i < count; i++)
            for (int j = 1; j < count; j++)
                if (this.users[j].getScore() > this.users[j - 1].getScore()) {
                    
                    User temp = this.users[j];
                    this.users[j] = this.users[j - 1];
                    this.users[j - 1] = temp;
                    
                }
    }

    public String showLeaderboard() {
        
        this.sort();
        
        MyConsole.clearScreen();
        
        String leaderBoard = "username-----------------score";
        
        for (int i = 0; i < this.count; i++) {
            
            int length = users[i].getUsername().length();
            
            leaderBoard += "\n" + users[i].getUsername();
            
            String dashes = "";
            for (int j = 0; j < 25 - length; j++)
                dashes += "-";
            
            leaderBoard += dashes + users[i].getScore();
        }
        
        return leaderBoard;
    }
}

class StartUpPage {
    public static boolean display() {
        
        MyConsole.clearScreen();
        MyConsole.print(3, 5,"blue", "1. Sign Up");
        MyConsole.print(4, 5, "blue", "2. Login");
        MyConsole.print(5, 5, "red", "3. Exit");
        MyConsole.print(7, 7, "green", "Enter your action : ");

        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
        
        switch (choice) {
            case 1:
                
                SignUp.displaySignUpPage();
                return true;
                
            case 2:
                
                Login.login();
                return true;
                
            default:
                
                return false;
        }
    }
}

class SignUp {
    private User[] users;

    public SignUp() {
        this.users = Users.getInstance().getUsers();
    }

    public static void displaySignUpPage() {
        
        Scanner input = new Scanner(System.in);
        MyConsole.clearScreen();

        MyConsole.print(3, 5,"blue", "Enter your username : ");
        String username = input.nextLine().trim();

        MyConsole.print(4, 5,"blue", "Enter your password : ");
        String password = String.copyValueOf(System.console().readPassword());

        signUpUser(username, password);
        
    }

    private static void signUpUser(String username, String password) {
        
        boolean status = true;

        if (!validateUsername(username)) {
            status = false;
            MyConsole.print(6, 7, "red", "Invalid username. Your username had been chosen.");
        }

        if (!validatePassword(password)) {
            if(status)
                MyConsole.print(6, 7, "red", "Invalid password. Your password has to contain " +
                    "alphabets, numbers and special characters and it must be more than 6 characters.");
            else
                MyConsole.print(8, 7, "red", "Invalid password. Your password has to contain " +
                        "alphabets, numbers and special characters and it must be more than 6 characters.");
        }
        
        if (!status)
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) { }
        else
            Users.getInstance().add(new User(username, password));
        
    }

    private static boolean validateUsername(String username) {

        return Users.getInstance().checkUsername(username);

    }

    private static boolean validatePassword(String password) {

        return (password.length() >= 6 && Pattern.compile("^[A-Za-z0-9]*[^A-Za-z0-9]+.*$").matcher(password).find());

    }
}

class Login {
    public static void login() {
        
        MyConsole.clearScreen();
        
        Scanner input = new Scanner(System.in);
        
        MyConsole.print(3, 5,"blue", "Enter your username : ");
        String username = input.nextLine();
        
        MyConsole.print(4, 5,"blue", "Enter your password : ");
        String password = String.copyValueOf(System.console().readPassword());
        
        User[] users = Users.getInstance().getUsers();     // getting users
        int count = Users.getInstance().getCount();
        
        for (int i = 0; i < count; i++)                    // searching for the user
            if (users[i].getUsername().equals(username))
                if (users[i].getPassword().equals(password)) {
                    
                    loginPage(users[i]);                   // redirecting to login page
                    return;

                }

        try {
            MyConsole.print(6, 7, "red", "Invalid username or password :(");
            Thread.sleep(4000);
        } catch (Exception e) {}
    }

    private static void loginPage(User user) {
        
        MyConsole.clearScreen();
        
        Scanner input = new Scanner(System.in);

        MyConsole.print(3, 5, "blue", "1. Start Game");
        MyConsole.print(4, 5, "blue", "2. Show Leaderboard");
        MyConsole.print(6, 7, "green", "Enter your choice : ");
        int choice = input.nextInt();
        
        switch (choice) {
            case 1:
                
                Game game = new Game(user);
                MyConsole.clearScreen();
                game.play();
                game = null;
                break;
                
            case 2:
                
                MyConsole.clearScreen();
                System.out.print(Users.getInstance().showLeaderboard());
                
                try {
                    
                    Thread.sleep(6000);
                    
                } catch (InterruptedException e) { }
        }
    }
}

class Game {
    private final String[] words = {"tehran", "pizza", "banana", "new york", "advanced programming", "michael jordan",
            "lionel messi", "apple", "macaroni", "university", "intel", "kitten", "python", "java",
            "data structures", "algorithm", "assembly", "basketball", "hockey", "leader", "javascript",
            "toronto", "united states of america", "psychology", "chemistry", "breaking bad", "physics",
            "abstract classes", "linux kernel", "january", "march", "time travel", "twitter", "instagram",
            "dog breeds", "strawberry", "snow", "game of thrones", "batman", "ronaldo", "soccer",
            "hamburger", "italy", "greece", "albert einstein", "hangman", "clubhouse", "call of duty",
            "science", "theory of languages and automata"};
    private final int countOfWords = 50;
    private User user;
    private String word;
    private int countOfMistakes;
    private int maxMistake;
    private boolean getRandomLetter;
    private boolean[] status;
    private boolean[] choosenCharacters;

    Game(User user) {

        this.user = user;
        int index = (int) (Math.random() * this.countOfWords);
        this.word = this.words[index];                                // choosing a random word
        this.status = new boolean[this.word.length()];
        varifyWord(this.word);
        int length = length(this.word);
        this.countOfMistakes = 0;
        this.maxMistake = length > 9 ? 14 : 7;
        this.getRandomLetter = true;
        this.choosenCharacters = new boolean[26];

    }

    private void varifyWord(String word) {

        for (int i = 0; i < word.length(); i++)
            if (word.charAt(i) == ' ')
                this.status[i] = true;

    }

    private int length(String word){

        int length = 0;

        for (int i = 0; i < word.length(); i++)
            if (word.charAt(i) != 0)
                length++;

        return length;
    }

    public static void print(int x, int y, String color, String message) {

        MyConsole.cursorGoTo(x, y);
        MyConsole.changeColor(color);
        System.out.print(message);
        MyConsole.changeColor("default");

    }

    private void showWord() {

        MyConsole.cursorGoTo(3, 5);
        MyConsole.changeColor("blue");

        for (int i = 0; i < this.word.length(); i++)
            if (status[i] || this.word.charAt(i) == ' ')
                System.out.printf("%c", this.word.charAt(i));
            else
                System.out.printf("-");

        MyConsole.changeColor("default");
    }

    private void showWood() {

        for (int i = 0; i < 5; i++)
            MyConsole.print(6 + i, 3,"boldBlue", "|");

    }

    private void showHuman() {

        int steps = this.maxMistake == 14 ? this.countOfMistakes / 2 : this.countOfMistakes;

        switch (steps) {
            case 7:
                print(9, 8, "boldGreen", "\\");
            case 6:
                print(9, 6, "boldGreen", "/");
            case 5:
                print(8, 8, "boldGreen", "\\");
            case 4:
                print(8, 7, "boldGreen", "|");
            case 3:
                print(8, 6, "boldGreen", "/");
            case 2:
                print(7, 7, "boldGreen", "O");
            case 1:
                print(6, 7, "boldRed", "|");

        }
    }

    private void showUsedChars() {

        MyConsole.cursorGoTo(9, 13);

        for (int i = 0; i < 26; i++)
            if (this.choosenCharacters[i])
                System.out.printf("%c ", i + 'a');
    }

    private void showTable() {

        String top = "\u2554\u2550";
        for (int i = 0; i < this.maxMistake - 1; i++)
            top += "\u2566\u2550";

        String middle = "\u2551";
        for (int i = 0; i < this.maxMistake; i++)
            middle += " \u2551";

        String buttom = "\u255A\u2550";
        for (int i = 0; i < this.maxMistake - 1; i++)
            buttom += "\u2569\u2550";

        print(11, 13, "yellow", top + "\u2557");
        print(12, 13, "yellow", middle);
        print(13, 13, "yellow", buttom + "\u255D");

        for (int i = 0; i < this.maxMistake; i++) {

            MyConsole.cursorGoTo(12, 14 + 2 * i);

            if (i < countOfMistakes) {

                MyConsole.changeColor("red");
                System.out.print("X");

            } else {

                MyConsole.changeColor("green");
                System.out.print("V");

            }
        }

        MyConsole.changeColor("default");

    }

    private void showScore() {

        MyConsole.print(4, 25,"purple", "Score : " + this.user.getScore());

    }

    public void play() {

        this.showWord();
        this.showWood();         // printing word, human, letters and etc
        this.showHuman();
        this.showTable();
        this.showUsedChars();
        this.showScore();

        print(6, 13, "", "Enter your character :  ");
        print(7, 13, "", "Enter 0 to get a random letter with 10 score.(just once)");
        MyConsole.cursorGoTo(6, 36);

        if (this.countOfMistakes == this.maxMistake) {   // checking user has lost 
            this.lose();
            return;
        }

        boolean isComplete = true;
        for (boolean status : this.status)
            if (!status)                       // checking user has won 
                isComplete = false;

        if (isComplete) {
            this.win();
            return;
        }

        String guess = (new Scanner(System.in)).next();  // getting user charcter
        
        if (this.validateChar(guess.charAt(0)) && guess.charAt(0) >= 'a' && guess.charAt(0) <= 'z') {  // checking input is valide

            if (!this.checkChar(guess.charAt(0)))
                this.countOfMistakes++;

            this.choosenCharacters[guess.charAt(0) - 'a'] = true;
            print(14, 13, "default", "                                    ");

        } else if (guess.charAt(0) == '0' && !this.getRandomLetter)   // checking for random character request

            print(14, 13, "red", "You have got your one random letter.");

        else if (guess.charAt(0) == '0') {

            if (!this.getLetter())
                print(14, 13, "red", "You don't have enough score.");
            else
                this.getRandomLetter = false;

        } else                                                                  // invalid input
            print(14, 13, "red", "Invalid character.");

        this.play();
    }

    private boolean getLetter() {

        if (!this.user.getARandomLetter())     // the user doesn't have enough score
            return false;

        int countOfLetters = 0;
        for (boolean status : this.status)     // counting how many letters remain
            if (!status)
                countOfLetters++;
            
        int index = (int) (Math.random() * countOfLetters);  // choosing a random letter from remain characters
        int temp = 0;

        char letter = 'a';
        for (int i = 0; i < this.status.length; i++)
            if (!this.status[i])
                if (index == temp++) {
                    
                    letter = this.word.charAt(i);
                    this.choosenCharacters[letter - 'a'] = true;
                    
                }

        for (int i = 0; i < this.word.length(); i++)
            if (letter == this.word.charAt(i)) 
                this.status[i] = true;

        return true;
    }

    private boolean validateChar(char guess) {
        
        for (int i = 0; i < 26; i++)
            if (this.choosenCharacters[i] && guess == 'a' + i)
                return false;
            
        return true;
        
    }

    private boolean checkChar(char guess) {
        
        boolean status = false;
        for (int i = 0; i < this.word.length(); i++)
            if (this.word.charAt(i) == guess) {
                
                this.status[i] = true;
                status = true;
        
            }
        
        return status;
            
    }

    private void win() {
        
        this.user.win();       // adding 5 score
        print(14, 13, "green", "YOU WON AND EARNED 5 SCORES :)");
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) { }
        
    }

    private void lose() {
        
        print(14, 13, "red", "YOU LOST :(");
        print(15, 13,"blue", "The word was : " + this.word);
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) { }
        
    }
}

class Save {
    public static void saveAll() {
        
        User[] users = Users.getInstance().getUsers();   // getting users
        int count = Users.getInstance().getCount();
        
        String written = "";
        written += count + "\n";
        for (User user : users)         // building a text of users
            written += user.getUsername() + " " + user.getPassword() + " " + user.getScore() + "\n";
        
        save("HangMan.txt", written);
    }

    public static void saveLeaderBoard() {
        
        save("LeaderBoard.txt", Users.getInstance().showLeaderboard());
        
    }

    private static void save(String fileName, String written) {
        
        try {
            
            File myFile = new File(fileName);
            myFile.createNewFile();
            
            FileWriter myWriter = new FileWriter(myFile);
            myWriter.write(written);
            
            myWriter.close();
            
        } catch (IOException e) { }
    }
}

class Load {
    public static void load() {
        
        try {
            
            File myFile = new File("HangMan.txt");
            Scanner input = new Scanner(myFile);
            
            int count = input.nextInt();
            
            for (int i = 0; i < count; i++)
                Users.getInstance().add(new User(input.next(), input.next(), input.nextInt()));
            
        } catch (FileNotFoundException e) { }
    }
}
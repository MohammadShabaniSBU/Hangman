import java.util.Scanner;
import java.lang.Thread;
import java.lang.InterruptedException;

public class HangMan {
    public static void main(String[] args) {
        while (true)
        StartUpPage.display();
    }
}

class MyConsole {
    private static String[] colors = {"default", "red", "boldRed", "green", "boldGreen", "yellow", "boldYellow", "blue", "boldBlue"};
    private static String[] colorCodes = {"[0m", "[0;31m", "[1;31m", "[0;32m", "[1;32m", "[0;33m", "[1;33m", "[0;34m", "[1;34m"};

    public static void changrColor(String color) {
        int index = searchColor(color);
        if (index == -1)
            return;

        System.out.printf("\033%s", colorCodes[index]);

    }

    public static void cursorGoTo(int x, int y) {
        char c = 0x1B;
        System.out.printf("%c[%d;%df", c, x, y);
        System.out.flush();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static int searchColor(String color) {
        int index = -1;
        for (int i = 0; i < colors.length; i++)
            if (color.equals(colors[i]))
                index = i;
        return index;
    }

    public static int getLines() {
        return Integer.parseInt(System.getenv("LINES"));
    }

    public static int getColumns() {
        return Integer.parseInt(System.getenv("COLUMNS"));
    }
}

class User {
    private String username;
    private String password;
    private int score;

    User(String username, String password) {
        this.username = username;
        this.password = password;
        this.score = 0;
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
        return users;
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

    public void showLeaderboard() {
        this.sort();
        MyConsole.clearScreen();
        System.out.printf("username-------score");
        for (int i = 0; i < this.count; i++) {
            System.out.println("");
            int length = users[i].getUsername().length();
            System.out.print(users[i].getUsername());
            String dashes = "";
            for (int j = 0; j < 15 - length; j++)
                dashes += "-";
            System.out.print(dashes + users[i].getScore());
        }
    }
}

class StartUpPage {
    public static void display() {
        MyConsole.clearScreen();
        System.out.println("1. Sign Up");
        System.out.println("2. Login");
        System.out.print("Enter your choice : ");
        System.out.flush();
        Scanner input = new Scanner(System.in);
        int choice = input.nextInt();
        switch (choice) {
            case 1:
                SignUp.displaySignUpPage();
                break;
            case 2:
                Login.login();

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

        System.out.print("Enter your username : ");
        System.out.flush();
        String username = input.nextLine().trim();

        System.out.print("Enter your password : ");
        System.out.flush();
        String password = input.nextLine().trim();

        signUpUser(username, password);
    }

    private static void signUpUser(String username, String password) {
        boolean status = true;

        if (!validateUsername(username)) {
            status = false;
            System.out.println("Invalid username. Your username had been chosen.");
        }

        if (!validatePassword(password)) {
            status = false;
            System.out.println("Invalid password. Your password has to contain alphabets, numbers and special characters and it must be more than 6 characters.");
        }

        System.out.flush();

        if (!status)
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {}
        else
            Users.getInstance().add(new User(username, password));

        StartUpPage.display();

    }

    private static boolean validateUsername(String username) {
        return Users.getInstance().checkUsername(username);
    }

    private static boolean validatePassword(String password) {
        return (password.length() >= 6);
    }
}

class Login {
    public static boolean login() {
        MyConsole.clearScreen();
        Scanner input = new Scanner(System.in);
        System.out.print("Enter your username : ");
        String username = input.nextLine();
        System.out.print("\nEnter your password : ");
        String password = input.nextLine();
        User[] users = Users.getInstance().getUsers();
        int count = Users.getInstance().getCount();
        for (int i = 0; i < count; i++) {
            if (users[i].getUsername().equals(username))
                if (users[i].getPassword().equals(password)) {
                    loginPage(users[i]);
                    return true;
                }
        }
        return false;
    }

    private static void loginPage(User user) {
        MyConsole.clearScreen();
        Scanner input = new Scanner(System.in);
        System.out.println("1. Start Game");
        System.out.println("2. Show Leaderboard");
        System.out.print("Enter your choice : ");
        System.out.flush();
        int choice = input.nextInt();
        switch (choice) {
            case 1:
                Game game = new Game(user);
                MyConsole.clearScreen();
                game.play();
                game = null;
                break;
            case 2:
                Users.getInstance().showLeaderboard();
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {}
        }
    }
}

class Game {
    private final String[] words = {"ahmad", "ali"};
    private final int countOfWords = 2;
    private User user;
    private String word;
    private int countOfMistakes;
    private int maxMistake;
    private boolean[] status;
    private boolean[] choosenCharacters;

    Game(User user) {
        this.user = user;
        int index = (int) (Math.random() * this.countOfWords);
        this.word = this.words[index];
        this.countOfMistakes = 0;
        this.maxMistake = this.word.length() > 9 ? 14 : 7;
        this.status = new boolean[this.word.length()];
        this.choosenCharacters = new boolean[26];
    }

    public void showWord() {
        MyConsole.cursorGoTo(1, 1);
        for (int i = 0; i < this.word.length(); i++)
            if (status[i] || this.word.charAt(i) == ' ')
                System.out.printf("%c", this.word.charAt(i));
            else
                System.out.printf("-");
        System.out.flush();
    }

    public void showWood() {
        MyConsole.cursorGoTo(3, 1);
        for (int i = 0; i < 5; i++)
            System.out.println("|");
    }

    public void showHuman() {
        int steps = this.maxMistake == 14 ? this.countOfMistakes / 2 : this.countOfMistakes;
        switch (steps) {
            case 7:
                MyConsole.cursorGoTo(6, 4);
                System.out.printf("\\");
            case 6:
                MyConsole.cursorGoTo(6, 3);
                System.out.printf("/");
            case 5:
                MyConsole.cursorGoTo(5, 5);
                System.out.printf("\\");
            case 4:
                MyConsole.cursorGoTo(5, 4);
                System.out.printf("|");
            case 3:
                MyConsole.cursorGoTo(5, 3);
                System.out.printf("/");
            case 2:
                MyConsole.cursorGoTo(4, 3);
                System.out.printf("O");
            case 1:
                MyConsole.cursorGoTo(3, 3);
                System.out.printf("|");
        }
    }

    public void showUsedChars() {
        MyConsole.cursorGoTo(5, 8);
        for (int i = 0; i < 26; i++)
            if (this.choosenCharacters[i])
                System.out.printf("%c ", i + 'a');
    }

    public void play() {
        if (this.countOfMistakes == this.maxMistake) {
            this.lose();
            return;
        }

        boolean isComplete = true;
        for (boolean status : this.status)
            if (!status)
                isComplete = false;

        if (isComplete) {
            this.win();
            return;
        }

        this.showWord();
        this.showWood();
        this.showHuman();
        this.showUsedChars();
        MyConsole.cursorGoTo(3, 8);
        System.out.print("Enter your character :  ");
        MyConsole.cursorGoTo(3, 31);
        String guess = (new Scanner(System.in)).next();
        if(this.validateChar(guess.charAt(0)) && guess.charAt(0) >= 'a' && guess.charAt(0) <= 'z') {
            if (!this.checkChar(guess.charAt(0)))
                this.countOfMistakes++;
            this.choosenCharacters[guess.charAt(0) - 'a'] = true;
            MyConsole.cursorGoTo(7, 8);
            System.out.printf("                 ");
//            System.out.flush();
        }else{
            MyConsole.cursorGoTo(7, 8);
            System.out.print("Invalid character");
            System.out.flush();
        }

        this.play();

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
        this.user.win();
    }

    private void lose() {
        MyConsole.cursorGoTo(7, 7);
        System.out.printf("YOU LOST :(");
        System.out.flush();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {}
    }
}
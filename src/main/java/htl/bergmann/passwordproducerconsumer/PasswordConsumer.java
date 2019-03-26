package htl.bergmann.passwordproducerconsumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordConsumer implements Runnable {

    private ArrayList<Password> passwords = new ArrayList<>();
    private static ArrayList<String> passwordList = new ArrayList<>();
    
    static {
        try (BufferedReader br = new BufferedReader(new FileReader(new File("passwords.txt")))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                passwordList.add(line);
            }
        } catch (Exception e) {

        }
    }

    public PasswordConsumer(ArrayList<Password> passwords) {
        this.passwords = passwords;
    }

    @Override
    public void run() {
        while (true) {
            Password password;
            synchronized (passwords) {
                if (passwords.size() > 0) {
                     password = passwords.remove(0);
                     System.out.println("Pulling "  + password.getPassword());
                     passwords.notifyAll();
                } else {
                    try {
                        System.out.println("List empty");
                        passwords.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PasswordConsumer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    continue;
                }
            }
            boolean cracked = false;
            for (String string : passwordList) {
                if (password.check(string)) {
                    System.out.println(password.getPassword() + " -> " + string);
                    cracked = true;
                    break;
                }
            }
            if (!cracked) {
                System.out.println(password.getPassword() + " -> The password has not been cracked");
            }

        }
    }

}

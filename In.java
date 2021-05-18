package column_generation;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.Socket;
//import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;


public final class In {
   
   private static final String CHARSET_NAME = "UTF-8";

   private static final Locale LOCALE = Locale.US;

   private static final Pattern WHITESPACE_PATTERN
       = Pattern.compile("\\p{javaWhitespace}+");

   private static final Pattern EMPTY_PATTERN
       = Pattern.compile("");

   private static final Pattern EVERYTHING_PATTERN
       = Pattern.compile("\\A");

   private Scanner scanner;

   public In() {
       scanner = new Scanner(new BufferedInputStream(System.in), CHARSET_NAME);
       scanner.useLocale(LOCALE);
   }

   public In(URL url) {
       if (url == null) throw new NullPointerException("argument is null");
       try {
           URLConnection site = url.openConnection();
           InputStream is     = site.getInputStream();
           scanner            = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
           scanner.useLocale(LOCALE);
       }
       catch (IOException ioe) {
           throw new IllegalArgumentException("Could not open " + url);
       }
   }

   public In(File file) {
       if (file == null) throw new NullPointerException("argument is null");
       try {
           scanner = new Scanner(file, CHARSET_NAME);
           scanner.useLocale(LOCALE);
       }
       catch (IOException ioe) {
           throw new IllegalArgumentException("Could not open " + file);
       }
   }

   public In(String name) {
      if (name == null) throw new NullPointerException("argument is null");
       try {
           // first try to read file from local file system
           File file = new File(name);
           if (file.exists()) {
               scanner = new Scanner(file, CHARSET_NAME);
               scanner.useLocale(LOCALE);
               return;
           }

           // next try for files included in jar
           URL url = getClass().getResource(name);

           if (url == null) {
               url = new URL(name);
           }

           URLConnection site = url.openConnection();


           InputStream is     = site.getInputStream();
           scanner            = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
           scanner.useLocale(LOCALE);
       }
       catch (IOException ioe) {
           throw new IllegalArgumentException("Could not open " + name);
       }
   }

   public In(Scanner scanner) {
       if (scanner == null) throw new NullPointerException("argument is null");
       this.scanner = scanner;
   }

   public boolean exists()  {
       return scanner != null;
   }
  
   public boolean isEmpty() {
       return !scanner.hasNext();
   }

   public boolean hasNextLine() {
       return scanner.hasNextLine();
   }

   public String readLine() {
       String line;
       try {
           line = scanner.nextLine();
       }
       catch (NoSuchElementException e) {
           line = null;
       }
       return line;
   }

  
   public String readAll() {
       if (!scanner.hasNextLine())
           return "";

       String result = scanner.useDelimiter(EVERYTHING_PATTERN).next();
       // not that important to reset delimeter, since now scanner is empty
       scanner.useDelimiter(WHITESPACE_PATTERN); // but let's do it anyway
       return result;
   }

   public String readString() {
       return scanner.next();
   }

   public int readInt() {
       return scanner.nextInt();
   }

   public void close() {
       scanner.close();  
   }

  
   
  

}


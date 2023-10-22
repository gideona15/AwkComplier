public class StringHandler {

    private String file;
    private int charposition = 0;

    // Constructor to initialize the StringHandler with an input text
    // Initialize the current index to the beginning
    public StringHandler(String inputText) {
        file = inputText;
        charposition = 0; 
    }

    // Peeks ahead by 'i' characters without moving the index
    // Return null character if index is out of bounds
    public Character Peek(int i) {
        if (charposition + i < file.length()) {
            return file.charAt(charposition + i);
        }
        return null; 
    }

    // Peeks a string of the next 'i' characters without moving the index
    // Return null if index is out of bounds
    public String PeekString(int i) {
        if (charposition + i <= file.length()) {
            return file.substring(charposition, charposition + i);
        }
        return null; 
    }

    // Gets the next character and moves the index forward
    // Return null if index is out of bounds
    public Character GetChar() {
        if (charposition < file.length()) {
            return file.charAt(charposition++);
        }
        return null; 
    }
    // Gets the current character and moves the index forward
    // Return null if index is out of bounds
    public Character CheckChar() {
        if (charposition < file.length()) {
            return file.charAt(charposition);
        }
        return null; 
    }

    // Moves the index ahead by 'i' positions
    public void Swallow(int i) {
        charposition++;
    }

    // Checks if the index has reached the end of the document
    public boolean IsDone() {
        return charposition >= file.length();
    }

    // Returns the remaining text from the current index to the end
    // Return null if index is at the end
    public String Remainder() {
        if (charposition < file.length()) {
            return file.substring(charposition);
        }
        return null; 
    }
}


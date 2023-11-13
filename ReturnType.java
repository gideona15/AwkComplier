 public class ReturnType {

        public enum Type {
              BREAK, CONTINUE, DELETE, DOWHILE, WHILE, FOR, FOREACH,IF, RETURN, NONE

        }
    
        private Type type;
        private String value;
    
        // Constructor with only the enum
        public ReturnType(Type type) {
            this.type = type;
        }
    
        // Constructor with the enum and a string
        public ReturnType(Type type, String value) {
            this.type = type;
            this.value = value;
        }

        // Getter for type
    public Type getType() {
        return type;
    }

    // Getter for value
    public String getMessage() {
        return value;
    }
    
        // toString method to represent the object as a string
        public String toString() {
            if (value != null && !value.isEmpty()) {
                return "ReturnType{type=" + type + ", message='" + value + "'}";
            } else {
                return "ReturnType{type=" + type + "}";
            }
        }
    
        
}

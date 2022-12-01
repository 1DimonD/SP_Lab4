import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Main {
    public static List<TokenPos> tokens = new ArrayList<>();
    public static List<String> keywords = Arrays.asList("abstract", "arguments",	"await", "boolean",
            "break", "byte", "case", "catch", "char", "class", "const",	"continue", "debugger",	"default",
            "delete", "do", "double", "else", "enum", "eval", "export",	"extends", "false", "final",
            "finally", "float",	"for", "function", "goto", "if", "implements", "import", "in", "instanceof",
            "int", "interface", "let", "long", "native", "new", "null",	"package", "private", "protected",
            "public", "return", "short", "static", "super",	"switch", "synchronized", "this", "throw",
            "throws", "transient", "true", "try", "typeof",	"var", "void", "volatile", "while",	"with",	"yield");
    public static HashMap<String, String> Rules = new HashMap<>();
    public static FileWriter fileWriter;
    public static String LengthWord(int length){
        String result = "";
        for (int i=0; i< length; i++){
            result += " ";
        }
        return result;
    }
    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get("resources/input.txt")));
        Rules.put("number", "(0x[A-Fa-f0-9]*)|([\\d]+[.]?[\\d]*)");
        Rules.put("identifier", "(?:[\\w_]+[\\w\\d_]*)");
        Rules.put("operator", "(\\+\\+|\\+|\\-\\-|\\-|\\*\\*|\\*|\\/|>=|<=|<>|&&|<|>|==|=|&|%|!=|!|\\.|~)");
        Rules.put("string constant", "(\"[^\"]*\")");
        Rules.put("char constant", "('[^']*')");
        Rules.put("punctuation", "(\\(|\\)|\\[|\\]|;|,|:|\\?|\\{|\\})");
        Rules.put("spaces", "[\n\t\r]");
        Rules.put("error", "[^\n\t\r\s]+");
        String comment = "//.*[\n\r\t]|/\\*.*\\*/";
        try{
            Pattern commentPattern = Pattern.compile(comment);
            Matcher commentMatcher = commentPattern.matcher(text);
            while (commentMatcher.find()) {
                String  matchText = commentMatcher.group();
                int matchIndex = commentMatcher.start();
                tokens.add(new TokenPos(matchIndex, matchText, "comment"));
                text = text.substring(0, matchIndex) + LengthWord(matchText.length()) + text.substring(matchIndex + matchText.length());
            }
            String[] order = {"string constant", "char constant", "number", "identifier", "punctuation", "operator",   "spaces" , "error"};
            for (var t : order){
                Pattern regex = Pattern.compile(Rules.get(t));
                Matcher regexMatcher = regex.matcher(text);
                while (regexMatcher.find()) {
                    String  matchText = regexMatcher.group();
                    int matchIndex = regexMatcher.start();
                    Main.tokens.add(new TokenPos(matchIndex, matchText, t));
                    text = text.substring(0, matchIndex) + LengthWord(matchText.length())+ text.substring(matchIndex + matchText.length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(tokens);
        try {
            fileWriter = new FileWriter("output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (TokenPos lexem : tokens){
            if(lexem.TokenType=="identifier" && keywords.contains(lexem.Token)) {
                lexem.TokenType = "keyword";
            }
            if(lexem.TokenType!="spaces"&&lexem.TokenType!="comment") {
                fileWriter.write("< "+lexem.Token +" >"+ " : " + "< "+lexem.TokenType +" >"+ "\n");
            }
        }
        fileWriter.close();
    }
}
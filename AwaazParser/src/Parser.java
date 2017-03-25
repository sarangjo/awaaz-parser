import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeVisitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static final String INPUT_FILE = "C:\\Users\\saran\\Google Drive\\Awaaz\\UW Awaaz 16-17 Music\\Arranging Committee\\CheapThrillsMalangPart2\\CheapThrillsMalangPart2.html";
    private static final String OUTPUT_FILE = "output.html";

    private static Map<String, String> flatMap;

    private static String[] noteArray = {
            "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#"
    };

    private static final int startNote = Arrays.binarySearch(noteArray, "B");
    private static final int endNote = Arrays.binarySearch(noteArray, "F#");
    private static final int N = endNote - startNote;

    public static void main(String[] args) {
        try {
            File inputFile = new File(INPUT_FILE);
            Scanner input = new Scanner(inputFile);
            PrintWriter output = new PrintWriter(OUTPUT_FILE);

            buildFlats();

            Document doc = Jsoup.parse(inputFile, "UTF-8");

            doc.body().traverse(new NodeVisitor() {
                @Override
                public void head(Node node, int depth) {
                    if (node instanceof TextNode) {
                        TextNode textNode = (TextNode) node;
                        textNode.text(process(textNode.text()));
                    }
                }

                @Override
                public void tail(Node node, int depth) {
                    // TODO Auto-generated method stub
                }
            });

            output.println(doc.outerHtml());

            output.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void buildFlats() {
        flatMap = new HashMap<>();
        flatMap.put("Ab", "G#");
        flatMap.put("Bb", "A#");
        flatMap.put("Cb", "B");
        flatMap.put("Db", "C#");
        flatMap.put("Eb", "D#");
        flatMap.put("Fb", "E");
        flatMap.put("Gb", "F#");
    }

    private static String process(String text) {
        String[] words = text.trim().split(" ");
        List<String> outputWords = new ArrayList<>();
        for (String word : words) {
            Matcher overallMatcher = Pattern.compile("\\(?[A-G][#b]?\\)?,?").matcher(word);
            if (overallMatcher.matches()) {
                // Get rid of parens and comma
                Pattern noteP = Pattern.compile("[A-G][#b]?");
                Matcher noteM = noteP.matcher(word);

                // We MUST MUST MUST find the GODDAMN note
                // TODO: replace with while() to handle multiple notes per word
                if (noteM.find()) {
                    int startIndex = noteM.start();
                    int endIndex = noteM.end();
                    String transposedNote = transpose(word.substring(startIndex, endIndex));
                    String transposedWord = word.substring(0, startIndex) + transposedNote + word.substring(endIndex, word.length());

                    outputWords.add(transposedWord);
                    continue;
                }
            }
            outputWords.add(word);
        }
        return String.join(" ", outputWords);
    }

    private static String transpose(String note) {
        // Convert all flats to sharps
        if (note.endsWith("b")) {
            note = flatMap.get(note);
        }
        int start = Arrays.binarySearch(noteArray, note);
        return noteArray[(start + N + (noteArray.length * 5)) % noteArray.length];
    }
}

import java.util.HashMap;
import java.util.Map;

/**
 * CS 351: Word Game Solver
 *
 * A Trie (prefix tree) data structure for storing and searching dictionary words.
 * Provides efficient word lookup, prefix checking, and insertion operations.
 *
 * @author Kiana Tarter
 */
public class Trie {

    /**
     * Nested Node class representing each node in the trie.
     * Each node contains child nodes for each possible next character
     * and a flag indicating if it represents the end of a valid word.
     */
    public class Node {
        /** Map of child nodes, keyed by the next character */
        public Map<Character, Node> children;

        /** True if this node represents the end of a valid word in the dictionary */
        public boolean isEndOfWord;

        /**
         * Constructs a new Node with an empty child map and end-of-word flag set to false.
         */
        public Node(){
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }

    /** Root node of the trie */
    public Node root;

    /**
     * Constructs an empty Trie with a single root node.
     */
    public Trie(){
        root = new Node();
    }

    /**
     * Inserts a word into the trie.
     * Converts the word to lowercase before insertion.
     *
     * @param word the word to insert into the trie
     */
    public void insert(String word){
        word = word.toLowerCase();
        Node current = root;
        for (char c : word.toCharArray()){
            current.children.putIfAbsent(c, new Node());
            current = current.children.get(c);
        }
        current.isEndOfWord = true;
    }

    /**
     * Searches for a word in the trie.
     * Returns true only if the word exists as a complete word in the dictionary.
     *
     * @param word the word to search for
     * @return true if the word exists in the trie, false otherwise
     */
    public boolean search(String word){
        word = word.toLowerCase();
        Node current = root;

        for (char c : word.toCharArray()){
            if (current.children.get(c) == null){
                return false;
            }
            current = current.children.get(c);
        }

        return current.isEndOfWord;
    }

    /**
     * Checks if any word in the trie starts with the given prefix.
     *
     * @param prefix the prefix to check
     * @return true if there is at least one word with the given prefix, false otherwise
     */
    public boolean startsWith(String prefix){
        prefix = prefix.toLowerCase();
        Node current = root;

        for (char c : prefix.toCharArray()){
            if (current.children.get(c) == null){
                return false;
            }
            current = current.children.get(c);
        }

        return true;
    }

    /**
     * Gets the child node for a given character from the specified node.
     *
     * @param node the parent node
     * @param c the character to look up
     * @return the child node, or null if no child exists for the given character
     */
    public Node getChild(Node node, char c){
        return node.children.get(c);
    }

    /**
     * Checks whether the given node represents the end of a valid word.
     *
     * @param node the node to check
     * @return true if the node marks the end of a word, false otherwise
     */
    public boolean isEnd(Node node){
        return node.isEndOfWord;
    }

    /**
     * Gets the root node of the trie.
     *
     * @return the root node
     */
    public Node getRoot(){
        return root;
    }
}

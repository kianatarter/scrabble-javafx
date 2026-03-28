import java.util.HashMap;
import java.util.Map;

public class Trie {
    public class Node {
        public Map<Character, Node> children;
        public boolean isEndOfWord;

        public Node(){
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }

    public Node root;

    public Trie(){
        root = new Node();
    }

    public void insert(String word){
        word = word.toLowerCase();
        Node current = root;
        for (char c : word.toCharArray()){
            current.children.putIfAbsent(c,new Node());
            current = current.children.get(c);
        }
        current.isEndOfWord = true;
    }

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

    public Node getChild(Node node, char c){
        return node.children.get(c);
    }

    public boolean isEnd(Node node){
        return node.isEndOfWord;
    }

    public Node getRoot(){
        return root;
    }
}

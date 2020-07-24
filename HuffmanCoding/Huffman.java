package huffman;

import java.util.*;
import java.io.ByteArrayOutputStream; 

/**
 * Huffman instances provide reusable Huffman Encoding Maps for
 * compressing and decompressing text corpi with comparable
 * distributions of characters.
 */
public class Huffman {
    
    // -----------------------------------------------
    // Construction
    // -----------------------------------------------

    private HuffNode trieRoot;
    private TreeMap<Character, String> encodingMap; 
    
    /**
     * Creates the Huffman Trie and Encoding Map using the character
     * distributions in the given text corpus
     * @param corpus A String representing a message / document corpus
     *        with distributions over characters that are implicitly used
     *        throughout the methods that follow. Note: this corpus ONLY
     *        establishes the Encoding Map; later compressed corpi may
     *        differ.
     */
    Huffman (String corpus) {
        Queue<HuffNode> frontier = generateFrontier(corpus); 
        trieRoot = generateTrie(frontier); 
        encodingMap = new TreeMap<Character, String>();
        populateEncodingMap(trieRoot, "");     
    }
    
    /**
     * Generates a frontier to order characters based on the frequency they appear
     * in a text corpus   
     * @param corpus String message desired to compress 
     * @return Queue of HuffNodes which order characters based on frequency
     */
    private Queue<HuffNode> generateFrontier(String corpus) {
        Map<Character, Integer> mapFrequencies = new HashMap<Character, Integer>();
        Queue<HuffNode> frontier = new PriorityQueue<HuffNode>();
        char[] characters = corpus.toCharArray();
        for (char character : characters) {
            if (mapFrequencies.containsKey(character)) {
                mapFrequencies.replace(character, mapFrequencies.get(character) + 1);
            } else {
                mapFrequencies.put(character, 1);
            }
        }

        for (Map.Entry<Character, Integer> entry : mapFrequencies.entrySet()) {
            frontier.add(new HuffNode(entry.getKey(), entry.getValue()));
        }
        
        return frontier;
    }
    
    /**
     * Simulates the generation of a tree in order to arrange the characters 
     * in a parented fashion to aid the population of the encoding map 
     * @param frontier Queue of HuffNodes which order characters based on frequency
     * @return HuffNode Root of the huffman trie
     */
    private HuffNode generateTrie(Queue<HuffNode> frontier) {
        while (frontier.size() > 1) {
            HuffNode firstNode = frontier.poll();
            HuffNode secondNode = frontier.poll();
            HuffNode parent = new HuffNode('\0', firstNode.count + secondNode.count);

            parent.right = secondNode;
            parent.left = firstNode;
            frontier.add(parent);
        }
        return frontier.poll();
    }
    
    /**
     * Populates the encoding map based on huffman trie 
     * @param node Root of the huffman trie to encode
     * @param map TreeMap encoding map to populate
     * @param encoding String ending that serves as a compression code  
     */
    private void populateEncodingMap(HuffNode node, String encoding) {
        if (node.isLeaf()) {
            encodingMap.put(node.character, encoding);
            return;
        }

        populateEncodingMap(node.right, encoding + "1");
        populateEncodingMap(node.left, encoding + "0");
    }
       
    // -----------------------------------------------
    // Compression
    // -----------------------------------------------
    
    /**
     * Compresses the given String message / text corpus into its Huffman coded
     * bitstring, as represented by an array of bytes. Uses the encodingMap
     * field generated during construction for this purpose.
     * @param message String representing the corpus to compress.
     * @return {@code byte[]} representing the compressed corpus with the
     *         Huffman coded bytecode. Formatted as 3 components: (1) the
     *         first byte contains the number of characters in the message,
     *         (2) the bitstring containing the message itself, (3) possible
     *         0-padding on the final byte.
     */
    public byte[] compress(String message) {
        String buffer = "";

        for (int i = 0; i < message.length(); i++) {
            buffer += encodingMap.get(message.charAt(i));
        }

        byte[] byteArr = new byte[(buffer.length() / 8) + 2];
        byteArr[0] = (byte) message.length();

        while (buffer.length() % 8 != 0) {
            buffer += "0";
        }

        for (int i = 1; i < byteArr.length; i++) {
            String byteString = buffer.substring((i - 1) * 8, i * 8);
            byteArr[i] = (byte) Integer.parseInt(byteString, 2);
        }
        
        return byteArr;
    }
    
    
    // -----------------------------------------------
    // Decompression
    // -----------------------------------------------
    
    /**
     * Decompresses the given compressed array of bytes into their original,
     * String representation. Uses the trieRoot field (the Huffman Trie) that
     * generated the compressed message during decoding.
     * @param compressedMsg {@code byte[]} representing the compressed corpus with the
     *        Huffman coded bytecode. Formatted as 3 components: (1) the
     *        first byte contains the number of characters in the message,
     *        (2) the bitstring containing the message itself, (3) possible
     *        0-padding on the final byte.
     * @return Decompressed String representation of the compressed bytecode message.
     */
    public String decompress(byte[] compressedMsg) {

        int actualLength = compressedMsg[0];
        String message = "";
        String bitString = "";

        for (int i = 1; i < compressedMsg.length; i++) {
            bitString += String.format("%8s", Integer.toBinaryString(compressedMsg[i] & 0xFF)).replace(' ', '0');
        }

        HuffNode start = trieRoot;

        for (int i = 0; i < bitString.length(); i++) {

            if (start.isLeaf()) {
                if (message.length() < actualLength) {
                    message = message + start.character;
                }
                start = trieRoot;
            }
            if (bitString.charAt(i) == '0') {
                start = start.left;
            } else if (bitString.charAt(i) == '1') {
                start = start.right;
            }
            if (message.length() == actualLength) {
                return message;
            }
        }
        return message;
    }
    
    
    // -----------------------------------------------
    // Huffman Trie
    // -----------------------------------------------
    
    /**
     * Huffman Trie Node class used in construction of the Huffman Trie.
     * Each node is a binary (having at most a left and right child), contains
     * a character field that it represents (in the case of a leaf, otherwise
     * the null character \0), and a count field that holds the number of times
     * the node's character (or those in its subtrees, in the case of inner 
     * nodes) appear in the corpus.
     */
    private static class HuffNode implements Comparable<HuffNode> {
        
        HuffNode left, right;
        char character;
        int count;
        
        HuffNode (char character, int count) {
            this.count = count;
            this.character = character;
        }
        
        public boolean isLeaf () {
            return left == null && right == null;
        }
        
        public int compareTo (HuffNode other) {
            return this.count - other.count;
        }
        
    }

}

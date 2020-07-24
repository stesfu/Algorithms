package spellex;

import java.util.*;

public class SpellEx {

    // Note: Not quite as space-conscious as a Bloom Filter,
    // nor a Trie, but since those aren't in the JCF, this map
    // will get the job done for simplicity of the assignment
    private Map<String, Integer> dict;

    // For your convenience, you might need this array of the
    // alphabet's letters for a method
    private static final char[] LETTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * Constructs a new SpellEx spelling corrector from a given "dictionary" of
     * words mapped to their frequencies found in some corpus (with the higher
     * counts being the more prevalent, and thus, the more likely to be suggested)
     * 
     * @param words The map of words to their frequencies
     */
    SpellEx(Map<String, Integer> words) {
        dict = new HashMap<>(words);
    }

    /**
     * Returns the edit distance between the two input Strings s0 and s1 based on
     * the minimal number of insertions, deletions, replacements, and transpositions
     * required to transform s0 into s1
     * 
     * @param s0 A "start" String
     * @param s1 A "destination" String
     * @return The minimal edit distance between s0 and s1
     */
    public static int editDistance(String s0, String s1) {

        int[][] table = new int[s0.length() + 1][s1.length() + 1];

        for (int c = 0; c <= s0.length(); c++) {
            for (int r = 0; r <= s1.length(); r++) {
                if (c == 0) {
                    table[c][r] = r;
                } else if (r == 0) {
                    table[c][r] = c;
                } else {
                    table[c][r] = minimum(table[c - 1][r] + 1, table[c][r - 1] + 1,
                            table[c - 1][r - 1] + repCost(s0.charAt(c - 1), s1.charAt(r - 1)));
                    if (c >= 2 && r >= 2 && s0.charAt(c - 1) == s1.charAt(r - 2)
                            && s0.charAt(c - 2) == s1.charAt(r - 1)) {
                        table[c][r] = Math.min(table[c][r], table[c - 2][r - 2] + 1);
                    }
                }
            }
        }

        return table[s0.length()][s1.length()];
    }

    /**
     * Returns the n closest words in the dictionary to the given word, where
     * "closest" is defined by:
     * <ul>
     * <li>Minimal edit distance (with ties broken by:)
     * <li>Largest count / frequency in the dictionary (with ties broken by:)
     * <li>Ascending alphabetic order
     * </ul>
     * 
     * @param word The word we are comparing against the closest in the dictionary
     * @param n    The number of least-distant suggestions desired
     * @return A set of up to n suggestions closest to the given word
     */
    public Set<String> getNLeastDistant(String word, int n) {

        PriorityQueue<CompareWord> leastDistant = new PriorityQueue<>();

        for (Map.Entry<String, Integer> eachWord : dict.entrySet()) {
            int distance = editDistance(word, eachWord.getKey());
            leastDistant.add(new CompareWord(eachWord.getKey(), distance));
        }

        return queueToSet(leastDistant, n);
    }

    /**
     * Returns the set of n most frequent words in the dictionary to occur with edit
     * distance distMax or less compared to the given word. Ties in max frequency
     * are broken with ascending alphabetic order.
     * 
     * @param word    The word to compare to those in the dictionary
     * @param n       The number of suggested words to return
     * @param distMax The maximum edit distance (inclusive) that suggested /
     *                returned words from the dictionary can stray from the given
     *                word
     * @return The set of n suggested words from the dictionary with edit distance
     *         distMax or less that have the highest frequency.
     */
    public Set<String> getNBestUnderDistance(String word, int n, int distMax) {

        Set<String> candidates = new HashSet<String>();
        Set<String> newWords = new HashSet<String>();
        PriorityQueue<CompareWord> leastDistant = new PriorityQueue<>();

        candidates.addAll(generateSet(word));
        for (int i = 1; i < distMax; i++) {
            for (String candidate : candidates) {
                newWords.addAll(generateSet(candidate));
            }

            candidates.addAll(newWords);
        }

        for (String candidate : candidates) {
            if (dict.containsKey(candidate)) {
                leastDistant.add(new CompareWord(candidate, 1));
            }
        }
        
        return queueToSet(leastDistant, n);
    }

    /**
     * Given a word generates all possible words that result from one insertion
     * 
     * @param word String to generate words based on insertion
     * @return A set of all words created from an insertion
     */
    private Set<String> generateInsertion(String word) {
        Set<String> generatedWords = new HashSet<String>();
        for (int i = 0; i <= word.length(); i++) {
            String begWord = word.substring(0, i);
            for (char letter : LETTERS) {
                String insertWord = begWord + letter + word.substring(i);
                generatedWords.add(insertWord);
            }
        }
        
        return generatedWords;
    }

    /**
     * Given a word generates all possible words that result from one deletion
     * 
     * @param word String to generate words based on deletion
     * @return A set of all words created from a deletion
     */
    private Set<String> generateDeletion(String word) {
        Set<String> generatedWords = new HashSet<String>();
        for (int i = 0; i < word.length(); i++) {
            String begWord = word.substring(0, i);

            if (word.length() >= 2) {
                generatedWords.add(begWord + word.substring(i + 1));
            }
        }
        
        return generatedWords;
    }

    /**
     * Given a word generates all possible words that result from one replacement
     * 
     * @param word String to generate words based on replacement
     * @return A set of all words created from a replacement
     */
    private Set<String> generateReplacement(String word) {
        Set<String> generatedWords = new HashSet<String>();
        for (int i = 0; i < word.length(); i++) {
            String begWord = word.substring(0, i);
            String endWord = word.substring(i + 1);
            for (char letter : LETTERS) {
                generatedWords.add(begWord + letter + endWord);
            }
        }
        
        return generatedWords;
    }

    /**
     * Given a word generates all possible words that result from one transposition
     * 
     * @param word String to generate words based on transposition
     * @return A set of all words created from a transposition
     */
    private Set<String> generateTransposition(String word) {
        Set<String> generatedWords = new HashSet<String>();
        for (int i = 0; i < word.length(); i++) {
            String begWord = word.substring(0, i);

            String switchEnd = "";
            if (i + 2 < word.length()) {
                switchEnd = word.substring(i + 2);
            }

            if (i < word.length() && i + 1 < word.length() && word.length() > 1) {
                String transWord = begWord + word.substring(i + 1, i + 2) + word.substring(i, i + 1) + switchEnd;
                generatedWords.add(transWord);
            }
        }
        
        return generatedWords;
    }

    /**
     * Given a word generates all possible words that result from all transitions
     * 
     * @param word String to generate words based on all transitions
     * @return A set of all words created from all transitions
     */
    private Set<String> generateSet(String word) {
        Set<String> generatedWords = new HashSet<String>();
        
        generatedWords.addAll(generateInsertion(word));
        generatedWords.addAll(generateDeletion(word));
        generatedWords.addAll(generateTransposition(word));
        generatedWords.addAll(generateReplacement(word));
        
        return generatedWords;
    }

    /**
     * Given a priority queue, generates a set of the items in the queue maintaining
     * priority
     * 
     * @param queue A priority queue of CompareWords
     * @param n     The number of least-distant suggestions desired
     * @return A set of strings
     */
    private static Set<String> queueToSet(PriorityQueue<CompareWord> queue, int n) {
        Set<String> wordsListed = new HashSet<String>();
        while (n > 0) {
            if (queue.size() > 0) {
                wordsListed.add(queue.poll().word);
            }
            n--;
        }
        
        return wordsListed;
    }

    /**
     * Generates the replacement cost of two different characters
     * 
     * @param a First desired char to calculate replacement cost
     * @param b Second desired char to calculate replacement cost
     * @return int 0 if they are equal, 1 otherwise
     */
    private static int repCost(char a, char b) {
        return a == b ? 0 : 1;
    }

    /**
     * Calculates the minimum of three numbers
     * 
     * @param a First desired integer to calculate minimum
     * @param b Second desired integer to calculate minimum
     * @param c Third desired integer to calculate minimum
     * @return The minimum number of integers a, b, and c
     */
    private static int minimum(int a, int b, int c) {
        return Math.min((Math.min(a, b)), c);
    }

    /**
     * CompareWord is used in the method to sort words based on edit distance,
     * frequency, and alphabetic ordering
     */
    private class CompareWord implements Comparable<CompareWord> {
        String word;
        int editDistance;

        /**
         * Constructs a new CompareNode
         * 
         * @param word         string of the word to sort
         * @param editDistance The amount of changes needed to get to that word
         */
        CompareWord(String word, int editDistance) {
            this.word = word;
            this.editDistance = editDistance;
        }

        public int compareTo(CompareWord compare) {
            if (this.editDistance > compare.editDistance) {
                return 1;
            } else if (this.editDistance < compare.editDistance) {
                return -1;
            } else {
                if (dict.get(this.word) < dict.get(compare.word)) {
                    return 1;
                } else if (dict.get(this.word) > dict.get(compare.word)) {
                    return -1;
                } else {
                    return this.word.compareTo(compare.word);
                }
            }
        }
    }
}
import java.io.*;
import java.util.PriorityQueue;

public class HuffmanCompression {

    private static class HuffmanNode implements Comparable<HuffmanNode> {
        char data;
        int frequency;
        HuffmanNode left, right;

        public HuffmanNode(char data, int frequency) {
            this.data = data;
            this.frequency = frequency;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(HuffmanNode o) {
            return this.frequency - o.frequency;
        }
    }

    private static HuffmanNode buildHuffmanTree(int[] frequencies) {
        PriorityQueue<HuffmanNode> priorityQueue = new PriorityQueue<>();

        for (char i = 0; i < frequencies.length; i++) {
            if (frequencies[i] > 0) {
                priorityQueue.offer(new HuffmanNode(i, frequencies[i]));
            }
        }

        while (priorityQueue.size() > 1) {
            HuffmanNode left = priorityQueue.poll();
            HuffmanNode right = priorityQueue.poll();

            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;

            priorityQueue.offer(parent);
        }

        return priorityQueue.poll();
    }

    private static void generateHuffmanCodes(HuffmanNode root, String code, String[] huffmanCodes) {
        if (root == null) {
            return;
        }

        if (root.isLeaf()) {
            huffmanCodes[root.data] = code;
        }

        generateHuffmanCodes(root.left, code + "0", huffmanCodes);
        generateHuffmanCodes(root.right, code + "1", huffmanCodes);
    }

    private static String encodeText(String text, String[] huffmanCodes) {
        StringBuilder encodedText = new StringBuilder();

        for (char c : text.toCharArray()) {
            encodedText.append(huffmanCodes[c]);
        }

        return encodedText.toString();
    }

    private static String decodeText(String encodedText, HuffmanNode root) {
        StringBuilder decodedText = new StringBuilder();
        HuffmanNode current = root;

        for (int i = 0; i < encodedText.length(); i++) {
            char bit = encodedText.charAt(i);

            if (bit == '0') {
                current = current.left;
            } else if (bit == '1') {
                current = current.right;
            }

            if (current.isLeaf()) {
                decodedText.append(current.data);
                current = root;
            }
        }

        return decodedText.toString();
    }

    private static double calculateCompressionRatio(String originalText, String encodedText) {
        int originalSize = originalText.length() * 8; // Tamaño original en bits
        int compressedSize = encodedText.length(); // Tamaño comprimido en bits

        return (double) compressedSize / originalSize;
    }

    public static void main(String[] args) {
        String inputFilename = "input.txt"; // Archivo de entrada
        String encodedFilename = "encoded.txt"; // Archivo codificado
        String decodedFilename = "decoded.txt"; // Archivo decodificado

        try {
            // Paso 1: Leer el archivo de entrada
            BufferedReader reader = new BufferedReader(new FileReader(inputFilename));
            StringBuilder inputText = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                inputText.append(line);
                inputText.append(System.lineSeparator());
            }
            reader.close();

            // Paso 2: Calcular la frecuencia de aparición de cada símbolo
            int[] frequencies = new int[256]; // Array para almacenar las frecuencias
            for (char c : inputText.toString().toCharArray()) {
                frequencies[c]++;
            }

            // Paso 3: Construir el árbol de Huffman
            HuffmanNode root = buildHuffmanTree(frequencies);

            // Paso 4: Generar los códigos de Huffman
            String[] huffmanCodes = new String[256]; // Array para almacenar los códigos
            generateHuffmanCodes(root, "", huffmanCodes);

            // Paso 5: Codificar el texto
            String encodedText = encodeText(inputText.toString(), huffmanCodes);

            // Paso 6: Escribir el texto codificado y el árbol en un archivo
            BufferedWriter writer = new BufferedWriter(new FileWriter(encodedFilename));
            writer.write(encodedText);
            writer.close();

            // Paso 7: Leer el archivo codificado y decodificar el texto
            BufferedReader encodedReader = new BufferedReader(new FileReader(encodedFilename));
            StringBuilder encodedTextBuilder = new StringBuilder();
            String encodedLine;
            while ((encodedLine = encodedReader.readLine()) != null) {
                encodedTextBuilder.append(encodedLine);
                encodedTextBuilder.append(System.lineSeparator());
            }
            encodedReader.close();
            String savedEncodedText = encodedTextBuilder.toString();

            // Decodificar el texto
            String decodedText = decodeText(savedEncodedText, root);

            // Paso 8: Escribir el texto decodificado en un archivo
            BufferedWriter decodedWriter = new BufferedWriter(new FileWriter(decodedFilename));
            decodedWriter.write(decodedText);
            decodedWriter.close();

            // Paso 9: Calcular la tasa de compresión
            double compressionRatio = calculateCompressionRatio(inputText.toString(), savedEncodedText);

            // Mostrar los resultados en la consola
            System.out.println("Texto codificado: " + savedEncodedText);
            System.out.println("Árbol de Huffman: " + root);
            System.out.println("Texto decodificado: " + decodedText);
            System.out.println("Tasa de compresión: " + compressionRatio);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

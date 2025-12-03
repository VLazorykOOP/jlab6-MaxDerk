import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Lab62 extends JFrame {
    private static final int MAX_SIZE = 20;
    private static final int THRESHOLD = 1000;
    private JTable matrixTable;
    private DefaultTableModel tableModel;
    private JTextField sizeField;
    private JLabel resultLabel;
    private JButton loadFromFileButton;
    private JButton processButton;

    public Lab62() {
        super("Lab 6.2: Matrix Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        initComponents();
        layoutComponents();
        
        setSize(700, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        sizeField = new JTextField("5", 5);
        loadFromFileButton = new JButton("Load from file");
        processButton = new JButton("Calculate");
        resultLabel = new JLabel("Result:");
        
        tableModel = new DefaultTableModel(5, 5); 
        matrixTable = new JTable(tableModel);
        matrixTable.setPreferredScrollableViewportSize(new Dimension(650, 300));
        
        loadFromFileButton.addActionListener(e -> loadMatrixFromFile());
        processButton.addActionListener(e -> processMatrix());
    }
    
    private void layoutComponents() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.add(new JLabel("Size N (<=20):"));
        controlPanel.add(sizeField);
        controlPanel.add(loadFromFileButton);
        controlPanel.add(processButton);
        
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(matrixTable), BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);
    }

    private void loadMatrixFromFile() {
        JFileChooser fileChooser = new JFileChooser(".");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Scanner fileScanner = new Scanner(file)) {
                
                if (!fileScanner.hasNextInt()) {
                    throw new IllegalArgumentException("File structure error: Matrix size N is missing or invalid.");
                }
                int n = fileScanner.nextInt();
                
                if (n <= 0 || n > MAX_SIZE) {
                    throw new IllegalArgumentException("File structure error: Size N (" + n + ") is outside the valid range (1-" + MAX_SIZE + ").");
                }
                
                sizeField.setText(String.valueOf(n)); 
                
                int[][] A = readMatrixData(n, fileScanner);
                updateTableModel(n, A);
                resultLabel.setText("Result: Matrix loaded from " + file.getName());
                
            } catch (FileNotFoundException ex) {
                showError("File Error", "Error: File not found.");
            } catch (IllegalArgumentException ex) {
                showError("Input/Format Error", ex.getMessage());
            }
        }
    }
    
    private int[][] readMatrixData(int n, Scanner fileScanner) throws NumberFormatException {
        int[][] A = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!fileScanner.hasNextInt()) {
                    throw new NumberFormatException("Data error: Expected " + (n*n + 1) + " numbers total, but content ended prematurely at element [" + (i + 1) + "][" + (j + 1) + "].");
                }
                A[i][j] = fileScanner.nextInt();
            }
        }
        return A;
    }

    private void processMatrix() {
        if (matrixTable.isEditing()) {
            matrixTable.getCellEditor().stopCellEditing();
        }
        
        try {
            int n = Integer.parseInt(sizeField.getText());
            
            if (n <= 0 || n > MAX_SIZE) {
                throw new IllegalArgumentException("Input error: Matrix size N must be between 1 and " + MAX_SIZE + ".");
            }
            
            int[][] A = readMatrixFromTable(n);
            executeMatrixLogic(n, A);
            
        } catch (IllegalArgumentException ex) { 
            String message = ex.getMessage();
            if (message == null || message.contains("Matrix size N")) {
                showError("Input Error", message);
            } else {
                showError("Input Error", "Check N and matrix cells for valid integers. Details: " + message);
            }
        } catch (MatrixSumOverflowException ex) {
            showError("Calculation Error (Threshold)", ex.getMessage());
        }
    }

    private void executeMatrixLogic(int n, int[][] A) throws MatrixSumOverflowException {
        int targetColumn = 0;
        int maxSum = Integer.MIN_VALUE;

        for (int j = 0; j < n; j++) {
            int currentSum = 0;
            for (int i = 0; i < n; i++) {
                currentSum += Math.abs(A[i][j]);
            }
            
            if (currentSum > THRESHOLD) {
                 throw new MatrixSumOverflowException(
                     "Column " + (j + 1) + " sum of modules (" + currentSum + ") exceeds the limit " + THRESHOLD);
            }
            
            if (currentSum > maxSum) {
                maxSum = currentSum;
                targetColumn = j;
            }
        }

        int minVal = A[0][targetColumn];
        for (int i = 1; i < n; i++) {
            if (A[i][targetColumn] < minVal) {
                minVal = A[i][targetColumn];
            }
        }

        String result = String.format("Column with max sum module: %d (Sum: %d). Minimal element in that column: %d", 
                                        targetColumn + 1, maxSum, minVal);
        resultLabel.setText("Result: " + result);
    }
    
    private int[][] readMatrixFromTable(int n) throws NumberFormatException {
        int[][] A = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Object val = tableModel.getValueAt(i, j);
                String sVal = (val == null) ? "" : val.toString().trim();
                
                if (sVal.isEmpty()) {
                     throw new NumberFormatException("Cell [" + (i + 1) + "][" + (j + 1) + "] is empty.");
                }
                
                try {
                    A[i][j] = Integer.parseInt(sVal);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Cell [" + (i + 1) + "][" + (j + 1) + "] must be an integer (Found: '" + sVal + "').");
                }
            }
        }
        return A;
    }

    private void updateTableModel(int n, int[][] A) {
        tableModel.setColumnCount(n);
        tableModel.setRowCount(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tableModel.setValueAt(A[i][j], i, j);
            }
        }
    }
    
    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Lab62::new);
    }
}
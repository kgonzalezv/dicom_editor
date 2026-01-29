package org.example.ui;

import org.example.model.DicomMetadata;
import org.example.service.DicomService;
import org.example.util.DicomUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DicomUI extends JFrame {

    private JTextField folderField;
    private JTextField accessionField;
    private JTextField patientIdField;
    private JTextArea logArea;
    private File selectedFolder;
    private final DicomService dicomService = new DicomService();



    public DicomUI() {
        setTitle("DICOM Editor");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {

        setLayout(new BorderLayout(10, 10));

        JPanel folderPanel = new JPanel(new BorderLayout(5, 5));
        folderPanel.setBorder(new TitledBorder("Carpeta DICOM"));

        folderField = new JTextField();
        folderField.setEditable(false);

        JButton browseBtn = new JButton("Seleccionar...");
        browseBtn.addActionListener(e -> chooseFolder());

        folderPanel.add(folderField, BorderLayout.CENTER);
        folderPanel.add(browseBtn, BorderLayout.EAST);

        add(folderPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Campos editables
        JPanel editPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        editPanel.setBorder(new TitledBorder("Editar Tags"));

        editPanel.add(new JLabel("Accession Number:"));
        accessionField = new JTextField();
        editPanel.add(accessionField);

        editPanel.add(new JLabel("Patient ID:"));
        patientIdField = new JTextField();
        editPanel.add(patientIdField);

        centerPanel.add(editPanel);

        // Logs
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(new TitledBorder("Resultado"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        logPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(logPanel);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        JButton previewBtn = new JButton("Preview");
        previewBtn.addActionListener(e -> loadPreview());

        JButton applyBtn = new JButton("Aplicar cambios a toda la carpeta");
        applyBtn.addActionListener(e -> applyChanges());

        bottomPanel.add(previewBtn);
        bottomPanel.add(applyBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }


    private void chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFolder = chooser.getSelectedFile();
            folderField.setText(selectedFolder.getAbsolutePath());
            logArea.setText("");
        }
    }

    private void loadPreview() {
        if (selectedFolder == null) {
            showError("Seleccione una carpeta primero");
            return;
        }

        try {

            File[] files = selectedFolder.listFiles();
            if (files == null || files.length == 0) {
                showError("Carpeta vacía");
                return;
            }
            DicomMetadata metadata = null;

            for (File file : files) {
                if (file.isFile() && DicomUtils.isDicom(file)) {
                    metadata = DicomUtils.readMetadata(file);
                    break;
                }
            }

            if (metadata == null) {
                showError("No se encontró ningún DICOM válido");
                return;
            }

            accessionField.setText(metadata.getAccessionNumber());
            patientIdField.setText(metadata.getPatientId());

            logArea.append("Cargado\n" + metadata);

        } catch (Exception e) {
            showError("ERROR LEYENDO LA DATA \n"+e.getMessage());
        }
    }

    private void applyChanges() {
        if (selectedFolder == null) {
            showError("Seleccione una carpeta primero");
            return;
        }

        logArea.setText("");

        String accession = accessionField.getText().trim();
        String patientId = patientIdField.getText().trim();

        if (!accession.isEmpty()) {
            List<String> logs = dicomService.updateAccessionNumber(selectedFolder, accession);
            logs.forEach(l -> logArea.append(l + "\n"));
        }

        if (!patientId.isEmpty()) {
            List<String> logs = dicomService.updatePatientId(selectedFolder, patientId);
            logs.forEach(l -> logArea.append(l + "\n"));
        }

        logArea.append("Proceso finalizado\n");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

}

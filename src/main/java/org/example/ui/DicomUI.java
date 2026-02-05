package org.example.ui;

import org.example.model.DicomMetadata;
import org.example.responses.DicomUpdateData;
import org.example.service.DicomService;
import org.example.util.DicomUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DicomUI extends JFrame {

    private JTextField folderField;
    private JTextField accessionField;
    private JTextField patientIdField;
    private JTextField patientNameField;
    private JTextField studyInstanceUIDField;
    private JTextField randomTag;
    private JTextField randomTagValue;
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
        JPanel editPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        editPanel.setBorder(new TitledBorder("Editar Tags"));

        editPanel.add(new JLabel("Accession Number:"));
        accessionField = new JTextField();
        editPanel.add(accessionField);

        editPanel.add(new JLabel("Patient ID:"));
        patientIdField = new JTextField();
        editPanel.add(patientIdField);

        editPanel.add(new JLabel("Patient Name:"));
        patientNameField = new JTextField();
        editPanel.add(patientNameField);

        editPanel.add(new JLabel("Study Instance UID:"));
        studyInstanceUIDField = new JTextField();
        editPanel.add(studyInstanceUIDField);

        editPanel.add(new JLabel("Random tag:"));
        JPanel randomTagPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        randomTag = new PlaceholderTextField("Escribe el tag aqui");
        randomTagValue = new PlaceholderTextField("Escribe el valor aqui");
        randomTagPanel.add(randomTag);
        randomTagPanel.add(randomTagValue);
        editPanel.add(randomTagPanel);

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

        JButton clearBtn = new JButton("Limpiar campos y logs");
        clearBtn.addActionListener(e -> clearFields());

        bottomPanel.add(previewBtn);
        bottomPanel.add(applyBtn);
        bottomPanel.add(clearBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }


    //    Elige la carpeta donde estan los archivos
    private void chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFolder = chooser.getSelectedFile();
            folderField.setText(selectedFolder.getAbsolutePath());
            logArea.setText("");
        }
    }

    //    Aqui hacemos solo una carga de los datos del primer estudio para verificar
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
                    DicomUtils.setTagRandom(randomTag.getText(), randomTagValue.getText());
                    metadata = DicomUtils.readMetadata(file);
                    break;
                }
            }

            if (metadata == null) {
                showError("No se encontró ningún DICOM válido");
                return;
            }

            // Mostramos los datos en los campos
            setText(accessionField, metadata.getAccessionNumber());
            setText(patientIdField, metadata.getPatientId());
            setText(patientNameField, metadata.getPatientName());
            setText(studyInstanceUIDField, metadata.getStudyInstanceUID());
            setText(randomTagValue, metadata.getRandomTagValue());
            logArea.append("Cargado\n" + metadata);

        } catch (Exception e) {
            showError("ERROR LEYENDO LA DATA \n" + e.getMessage());
        }
    }

    //    Aplicamos los cambios
    private void applyChanges() {
        if (selectedFolder == null) {
            showError("Seleccione una carpeta primero");
            return;
        }

        logArea.setText("");

        // Preparamos los datos a actualizar
        DicomUpdateData data = DicomUpdateData.fromFields(
                accessionField,
                patientIdField,
                patientNameField,
                studyInstanceUIDField,
                randomTagValue,
                randomTag
        );

        Map<String, Supplier<List<String>>> updates = getStringSupplierMap(data, dicomService, selectedFolder);
        
        updates.values().forEach(action -> {
            List<String> logs = action.get();
            logs.forEach(l -> logArea.append(l + "\n"));
        });
        
        logArea.append("Proceso finalizado\n");
    }

    // Mapeamos los campos a actualizar
    private static Map<String, Supplier<List<String>>> getStringSupplierMap(DicomUpdateData data, DicomService dicomService, File selectedFolder) {
        Map<String, Supplier<List<String>>> updates = new LinkedHashMap<>();

        if (!data.accession().isEmpty())
            updates.put("Accession Number", () -> dicomService.updateAccessionNumber(selectedFolder, data.accession()));

        if (!data.patientId().isEmpty())
            updates.put("Patient ID", () -> dicomService.updatePatientId(selectedFolder, data.patientId()));

        if (!data.patientName().isEmpty())
            updates.put("Patient Name", () -> dicomService.updatePatientName(selectedFolder, data.patientName()));

        if (!data.studyInstanceUID().isEmpty())
            updates.put("Study Instance UID", () -> dicomService.updateStudyInstanceUID(selectedFolder, data.studyInstanceUID()));

        if (!data.randomTagName().isEmpty() && !data.randomTagValue().isEmpty())
            updates.put("Random Tag " + data.randomTagName(),
                    () -> dicomService.updateRandomTag(selectedFolder, data.randomTagName(), data.randomTagValue()));
        return updates;
    }


    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setText(JTextField field, Object value) {
        field.setText(value != null ? value.toString() : "");
    }

    private void clearFields() {
        setText(accessionField, "");
        setText(patientIdField, "");
        setText(patientNameField, "");
        setText(studyInstanceUIDField, "");
        setText(randomTag, "");
        setText(randomTagValue, "");
        logArea.setText("");
        DicomUtils.setTagRandom("", "");
    }
}

// Clase para mostrar placeholder en JTextField
class PlaceholderTextField extends JTextField {
    private String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY);
            g2.drawString(placeholder, 5, 15);
            g2.dispose();
        }
    }
}

package org.example.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BackupService {

    public static void backup(File file) throws IOException {

        File backupFolder = new File("C:/DicomBackups");

        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        Path backupPath = backupFolder.toPath().resolve(file.getName() + ".bak");

        if (!Files.exists(backupPath)) {
            Files.copy(file.toPath(), backupPath, StandardCopyOption.COPY_ATTRIBUTES);
            System.out.println("Backup creado: " + backupPath);
        } else {
            System.out.println("Backup ya existe: " + backupPath);
        }
    }
}

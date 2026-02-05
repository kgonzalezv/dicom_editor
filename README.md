# DICOM Editor 

Aplicación de escritorio en **Java Swing** para la edición controlada de metadatos en archivos **DICOM**, con soporte para creación automática de respaldos antes de cualquier modificación.

##  Funcionalidades
- Edición de tags DICOM:
  - Accession Number
  - Patient ID
  - PatientName
  - Study Instance UID
  - Random Tag
- Procesamiento de **todos los archivos DICOM de una carpeta**
- Creación automática de **backup** antes de modificar los archivos
- Interfaz gráfica sencilla desarrollada en **Swing**

##  Tecnologías
- Java 21
- Java Swing
- dcm4che
- Maven

##  Ejecución

### Ejecutar desde JAR
```bash
java -jar dicom_editor-1.0-SNAPSHOT.jar

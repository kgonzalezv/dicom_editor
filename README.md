# 🩺 DICOM Editor

Aplicación de escritorio desarrollada en **Java Swing** para la edición **controlada y segura** de metadatos en archivos **DICOM**, diseñada para entornos clínicos, pruebas de integración y flujos de anonimización parcial.

La herramienta permite modificar tags específicos sin cargar la imagen completa en memoria y creando automáticamente respaldos antes de cualquier cambio para garantizar la integridad de los estudios.

---

## 📖 Manual de Usuario

👉 https://docs.google.com/document/d/1pYe67sc2VWu2_lzFWr_Uvd808wvMX5jKXA5aSaqy86I/edit?usp=sharing

---

## ✨ Funcionalidades

### 🧾 Edición de Metadatos DICOM

Permite modificar los siguientes tags:

- `Accession Number (0008,0050)`
- `Patient ID (0010,0020)`
- `Patient Name (0010,0010)`
- `Study Instance UID (0020,000D)`
- Tag configurable adicional ("Random Tag")

---

### 📁 Procesamiento Masivo

- Procesa automáticamente **todos los archivos DICOM dentro de una carpeta**
- Ideal para:
  - pruebas de integración PACS
  - migraciones de datos
  - normalización de estudios

---

### 💾 Sistema de Respaldo Automático

Antes de modificar cualquier archivo:

- se crea un backup automático
- se preserva el archivo original intacto
- se reduce el riesgo de corrupción accidental

---

### 🖥️ Interfaz Gráfica

- Desarrollada en **Java Swing**
- Interfaz simple y directa
- Pensada para usuarios técnicos

---

## ⚙️ Tecnologías

- Java 21
- Java Swing
- dcm4che
- Maven
---

## 🔒 Seguridad e Integridad DICOM

La aplicación:

- No modifica el Pixel Data
- Evita cargar imágenes completas en RAM
- Mantiene la Transfer Syntax original
- Realiza escritura segura mediante archivos temporales
- Preserva la estructura del dataset original

⚠️ **Importante:**  
Esta herramienta está pensada para edición de metadatos controlada.  
No realiza recomprensión ni modificación de imágenes médicas.

---

## 🚀 Ejecución

### Ejecutar desde JAR

```bash
java -jar dicom_editor-1.0-SNAPSHOT.jar


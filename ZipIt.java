import java.io.*;
import java.util.zip.*;
/**
 * Compresses a directory into a zip file.
 * 
 * @author Mordechai Schmutter
 * @version 1.0
 */
public class ZipIt {
    private String dir;
    private String output;
    public void zipStart(String dir) {
        try {
            System.out.println("Attempting to compress the specified files...");
            //set input and output directories
            this.dir = dir;
            output = this.dir + "/../out.zip";
            //open streams
            FileOutputStream fos = new FileOutputStream(output);
            CheckedOutputStream checksum = new CheckedOutputStream(fos, new CRC32());
            ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(checksum));
            //begin compression
            zipFolder("", dir, zos);
            //close stream
            zos.close();
            System.out.println("Successfully compressed all files!");
            //create checksum file 
            PrintWriter crcFile = new PrintWriter(dir + "/../crc.txt");
            crcFile.print("Checksum: " + checksum.getChecksum().getValue());
            crcFile.close();
        } catch (Exception e) {
            System.out.println("Error occurred while compressing files.");
        }
    }

    private void zipFolder(String path, String dir, ZipOutputStream zos) throws Exception {
        File folder = new File(dir);
        //check if directory is empty
        if (folder.list().length == 0) {
            zipFile(path, dir, zos, true);
        } else {
            //iterate over files in directory
            for (String fileName : folder.list()) {
                if (path.equals("")) {
                    zipFile(folder.getName(), dir + "/" + fileName, zos, false);
                } else {
                    zipFile(path + "/" + folder.getName(), dir + "/" + fileName, zos, false);
                }
            }
        }
    }

    private void zipFile(String path, String dir, ZipOutputStream zos, boolean empty) throws Exception {
        File folder = new File(dir);
        //if folder is empty, add empty folder to compressed directory
        if (empty == true) {
            zos.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
        } else {
            if (folder.isDirectory()) {
                //if folder contains another folder, use recursion
                zipFolder(path, dir, zos);
            } else {
                //add file to compressed directory
                byte[] buffer = new byte[2048];
                int length;
                FileInputStream fis = new FileInputStream(dir);
                zos.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                //close stream
                fis.close();
            }
        }
    }
}
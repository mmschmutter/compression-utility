import java.io.*;
import java.util.zip.*;
import java.util.jar.*;
/**
 * Compresses a directory into a jar file.
 * 
 * @author Mordechai Schmutter
 * @version 1.0
 */
public class JarIt {
    private String dir;
    private String output;
    public void jarStart(String dir) {
        try {
            System.out.println("Attempting to compress the specified files...");
            //set input and output directories
            this.dir = dir;
            output = this.dir + "/../out.jar";
            //open streams
            FileOutputStream fos = new FileOutputStream(output);
            CheckedOutputStream checksum = new CheckedOutputStream(fos, new CRC32());
            JarOutputStream jos = new JarOutputStream(new BufferedOutputStream(checksum));
            //begin compression
            jarFolder("", dir, jos);
            //close stream
            jos.close();
            System.out.println("Successfully compressed all files!");
            //create checksum file 
            PrintWriter crcFile = new PrintWriter(dir + "/../crc.txt");
            crcFile.print("Checksum: " + checksum.getChecksum().getValue());
            crcFile.close();
        } catch (Exception e) {
            System.out.println("Error occurred while compressing files.");
        }
    }

    private void jarFolder(String path, String dir, JarOutputStream jos) throws Exception {
        File folder = new File(dir);
        //check if directory is empty
        if (folder.list().length == 0) {
            jarFile(path, dir, jos, true);
        } else {
            //iterate over files in directory
            for (String fileName : folder.list()) {
                if (path.equals("")) {
                    jarFile(folder.getName(), dir + "/" + fileName, jos, false);
                } else {
                    jarFile(path + "/" + folder.getName(), dir + "/" + fileName, jos, false);
                }
            }
        }
    }

    private void jarFile(String path, String dir, JarOutputStream jos, boolean empty) throws Exception {
        File folder = new File(dir);
        //if folder is empty, add empty folder to compressed directory
        if (empty == true) {
            jos.putNextEntry(new JarEntry(path + "/" + folder.getName() + "/"));
        } else {
            if (folder.isDirectory()) {
                //if folder contains another folder, use recursion
                jarFolder(path, dir, jos);
            } else {
                //add file to compressed directory
                byte[] buffer = new byte[2048];
                int length;
                FileInputStream fis = new FileInputStream(dir);
                jos.putNextEntry(new JarEntry(path + "/" + folder.getName()));
                while ((length = fis.read(buffer)) > 0) {
                    jos.write(buffer, 0, length);
                }
                //close stream
                fis.close();
            }
        }
    }
}
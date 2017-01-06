import java.io.*;
/**
 * Compresses a directory into either a zip or jar file and outputs a report of all instances of a specified file type within the directory.
 * 
 * @author Mordechai Schmutter
 * @version 1.0
 */
public class CompressFile
{
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args)throws IOException {
        System.out.println("Would you like to compress to zip or to jar?");
        String type = reader.readLine();
        if(type.equals("zip")){
            System.out.println("Enter the path of the directory you would like to compress:");
            String dir = reader.readLine();
            generateReport(dir);
            ZipIt z = new ZipIt();
            z.zipStart(dir);
            //move all files into original directory
            File out = new File(dir + "/../out.zip");
            out.renameTo(new File(dir + "/out.zip"));
            File crc = new File(dir + "/../crc.txt");
            crc.renameTo(new File(dir + "/crc.txt"));
            File report = new File(dir + "/../report.txt");
            report.renameTo(new File(dir + "/report.txt"));
            return;
        }
        if(type.equals("jar")){
            System.out.println("Enter the path of the directory you would like to compress:");
            String dir = reader.readLine();
            generateReport(dir);
            JarIt j = new JarIt();
            j.jarStart(dir);
             //move all files into original directory
            File out = new File(dir + "/../out.jar");
            out.renameTo(new File(dir + "/out.jar"));
            File crc = new File(dir + "/../crc.txt");
            crc.renameTo(new File(dir + "/crc.txt"));
            File report = new File(dir + "/../report.txt");
            report.renameTo(new File(dir + "/report.txt"));
            return;
        }
        else{
            System.out.println("Invalid compression format.");
            return;
        }
    }

    private static void generateReport(String dir)throws IOException {
        //create report file
        File reportFile = new File(dir + "/../report.txt");
        String reportDirectory = dir + "/../report.txt";
        File directory = new File(dir);
        String baseDirectory = directory.getParent();
        System.out.println("Enter the file extension you would like to report instances of in the directory:");
        String fileType = reader.readLine();
        FileWriter fileWriter = new FileWriter(reportDirectory, true);
        searchFileType(directory, baseDirectory, fileType, fileWriter);
        fileWriter.close();
    }

    private static void searchFileType(File directory, String baseDirectory, String fileType, FileWriter fileWriter)throws IOException{
        //if directory, use recursion
        if(directory.isDirectory()) {
            for(File file : directory.listFiles()) {
                searchFileType(file, baseDirectory, fileType, fileWriter);
            }
        } else if(directory.isFile() && directory.getName().endsWith(fileType)) {
            //if file type matches, write to text file
            String relativePath = directory.getAbsolutePath().substring(baseDirectory.length());
            fileWriter.write(relativePath + System.getProperty("line.separator"));
        }
    }
}
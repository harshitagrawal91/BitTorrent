package bittorrent;

import bittorrent.beans.GlobalConstants;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author harsh
 */
public class FileUtility {

    public static String dir = "";
    private static final String suffix = ".splitPart";

    public static Stream<String> convertFileToStream(String location) throws IOException {
        return Files.lines(Paths.get(location));
    }

    public static void convertStreamToFile(Stream<String> data, Path path) throws IOException {
        Files.write(path, (Iterable<String>) data::iterator);
    }

    public static List<Path> splitFile(final String fileName, final long bperSplit) throws IOException {

        if (bperSplit <= 0) {
            throw new IllegalArgumentException("mBperSplit must be more than zero");
        }

        List<Path> partFiles = new ArrayList<>();
        final long sourceSize = Files.size(Paths.get(fileName));
        final long bytesPerSplit = bperSplit;
        final long numSplits = sourceSize / bytesPerSplit;
        final long remainingBytes = sourceSize % bytesPerSplit;
        int position = 0;

        try (RandomAccessFile sourceFile = new RandomAccessFile(fileName, "r");
                FileChannel sourceChannel = sourceFile.getChannel()) {

            for (; position < numSplits; position++) {
                //write multipart files.
                writePartToFile(bytesPerSplit, position * bytesPerSplit, sourceChannel, partFiles, position);
            }

            if (remainingBytes > 0) {
                writePartToFile(remainingBytes, position * bytesPerSplit, sourceChannel, partFiles, position);
            }
        }
        return partFiles;
    }

    private static void writePartToFile(long byteSize, long position, FileChannel sourceChannel, List<Path> partFiles, long nos) throws IOException {
        Path fileName = Paths.get(dir + File.separator + nos + suffix);
        Files.deleteIfExists(fileName);
        try (RandomAccessFile toFile = new RandomAccessFile(fileName.toFile(), "rw");
                FileChannel toChannel = toFile.getChannel()) {
            sourceChannel.position(position);
            toChannel.transferFrom(sourceChannel, 0, byteSize);
        }
        partFiles.add(fileName);
    }

    public static void mergeFiles(File[] files, String mergedFilePath) {
        
        File mergedFile = new File(mergedFilePath + File.separator + GlobalConstants.commonConfig.getFileName());
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(mergedFile, true);
            out = new BufferedWriter(fstream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for (File f : files) {
            System.out.println("merging: " + f.getName());
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                String aLine;
                while ((aLine = in.readLine()) != null) {
                    out.write(aLine);
                    out.newLine();
                }

                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

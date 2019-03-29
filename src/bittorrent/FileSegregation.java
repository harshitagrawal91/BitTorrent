
package bittorrent;
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
/**
 *
 * @author harsh
 */
public class FileSegregation {
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
        final long bytesPerSplit =  bperSplit;
        final long numSplits = sourceSize / bytesPerSplit;
        final long remainingBytes = sourceSize % bytesPerSplit;
        int position = 0;

        try (RandomAccessFile sourceFile = new RandomAccessFile(fileName, "r");
             FileChannel sourceChannel = sourceFile.getChannel()) {

            for (; position < numSplits; position++) {
                //write multipart files.
                writePartToFile(bytesPerSplit, position * bytesPerSplit, sourceChannel, partFiles,position);
            }

            if (remainingBytes > 0) {
                writePartToFile(remainingBytes, position * bytesPerSplit, sourceChannel, partFiles,position);
            }
        }
        return partFiles;
    }

    private static void writePartToFile(long byteSize, long position, FileChannel sourceChannel, List<Path> partFiles,long nos) throws IOException {
        Path fileName = Paths.get(dir +File.separator+ nos + suffix);
        Files.deleteIfExists(fileName);
        try (RandomAccessFile toFile = new RandomAccessFile(fileName.toFile(), "rw");
             FileChannel toChannel = toFile.getChannel()) {
            sourceChannel.position(position);
            toChannel.transferFrom(sourceChannel, 0, byteSize);
        }
        partFiles.add(fileName);
    }
}

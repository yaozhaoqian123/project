package fileoperation;

import repository.Repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class FileReader {

    /* READING AND WRITING FILE CONTENTS */

    /** Return the entire contents of FILE as a byte array.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return the entire contents of FILE as a String.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    public static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /**
     * Check if the file is already serialized in .git/objects
    * */
    public static boolean objectExists(String id) {
        String path = Repository.getGitDir() + File.separator + "objects";

        File first = new File(path + File.separator + id.substring(0,2));

        if(!first.exists()) return false;
        else {
            File target = new File(path + File.separator + id.substring(0,2) + File.separator + id.substring(2));
            if (target.exists()) return true;
            else return false;
        }

    }

    /** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    static <T extends Serializable> T readObject(String filepath,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(new File(filepath)));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return an object of type T read from compressed FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    public static <T extends Serializable> T readCompressedObj(String filepath, Class<T> expectedclass) {
        try{
            ObjectInputStream ois =
                    new ObjectInputStream(new GZIPInputStream(new FileInputStream(new File(filepath))));
            T result = expectedclass.cast(ois.readObject());
            ois.close();
            return result;
        } catch (IOException | ClassCastException
                | ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get every line in the given file.
     * @param value
     * @return
     */
    public static ArrayList<String> readByBufferReader(String value) throws FileNotFoundException {
        InputStream is = new ByteArrayInputStream(value.getBytes());
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        ArrayList<String> stringList =  new ArrayList<>();
        try{

            String line=br.readLine();
            while (line!=null){
                stringList.add(line);
                line=br.readLine();
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringList;
    }

    /**
     * Get the format of the object. The param "line" is a line in a tree object, like"100644 blob *** a.txt"
     * @param line
     * @return
     */
    public static String readObjectFmt(String line){
        String [] arr = line.split("\\s+");
        return arr[1];
    }

    /**
     * Get the value of the object.
     * @param line
     * @return
     */
    public static String readObjectKey(String line){
        String [] arr = line.split("\\s+");
        return arr[2];
    }

    /**
     * Get the filename of the object.
     * @param line
     * @return
     */
    public static String readObjectFileName(String line){
        String [] arr = line.split("\\s+");
        return arr[3];
    }

    /**
     * Get the tree from a commit value.
     * @param value
     * @return
     * @throws FileNotFoundException
     */
    public static String readCommitTree(String value) throws FileNotFoundException {
        ArrayList<String> stringList = readByBufferReader(value);
        String [] arr = stringList.get(0).split("\\s+");
        return arr[1];
    }
    public static String readCommitParent(String value) throws FileNotFoundException {
        ArrayList<String> stringList = readByBufferReader(value);
        String [] arr = stringList.get(1).split("\\s+");
        return (arr.length > 1) ? arr[1] : null;
    }
    public static String readCommitAuthor(String value) throws FileNotFoundException {
        ArrayList<String> stringList = readByBufferReader(value);
        String [] arr = stringList.get(2).split("\\s+");
        String author = arr[1];
        for(int i = 2; i < arr.length; i++){ author += " " + arr[i]; }
        return author;
    }
    public static String readCommitter(String value) throws FileNotFoundException{
        ArrayList<String> stringList = readByBufferReader(value);
        String [] arr = stringList.get(3).split("\\s+");
        String committer = arr[1];
        for(int i = 2; i < arr.length; i++){ committer += " " + arr[i]; }
        return committer;
    }
    public static String readCommitMsg(String value) throws FileNotFoundException{
        ArrayList<String> stringList = readByBufferReader(value);
        return stringList.get(4);
    }

}

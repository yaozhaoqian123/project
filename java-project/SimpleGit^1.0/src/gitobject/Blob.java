package gitobject;

import fileoperation.FileReader;
import sha1.SHA1;

import java.io.*;

public class Blob extends GitObject{

    public Blob(){};
    /**
     * Constructing blob object from a file
     * @param file
     * @throws Exception
     */
    // 只当为workTree下使用
    public Blob(File file) throws Exception {
        fmt = "blob";
        mode = "100644";
        value = genValue(file);
        name = file.getName();
        key = genKey();
    }

    //主要用于建树时Blob命名.
    public Blob(File file, String name) throws Exception {
        fmt = "blob";
        mode = "100644";
        value = genValue(file);
        this.name = name;
        key = genKey();
    }

    /**
     * Deserialize a blob object from an existed hash file in .jit/objects.
     * @param Id
     * @throws IOException
     */
    public static Blob deserialize(String Id)  {
        try{

            return FileReader.readCompressedObj(path +  File.separator + Id.substring(0,2) + File.separator + Id.substring(2), Blob.class);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public String genValue(File file)  {
        return FileReader.readContentsAsString(file);
    }

    /**
     * Generate key from file.
     * @param
     * @return String
     * @throws Exception
     */
    public String genKey() throws Exception {
        return SHA1.getHash("100644 blob " + value);
    }

    @Override
    public String toString(){
        return "100644 blob " + key;
    }
}

package gitobject;
import fileoperation.FileCreation;
import fileoperation.FileReader;
import repository.Repository;

import java.io.*;
import fileoperation.FileWriter;


public class GitObject implements Serializable {

    protected static String path = Repository.getGitDir() + File.separator + "objects";   //absolute path of objects

    protected String fmt;                  //type of object
    protected String key;                  //key of object
    protected String mode;                 //mode of object
    protected String value;                //value of object
    protected String name;                 // name of object

    public String getFmt(){ return fmt; }
    public String getKey() { return key; }
    public String getMode(){ return mode; }
    public static String getPath() { return path; }
    public String getValue(){ return value; }
    public String getName() { return name; }

    public GitObject(){}
    /**
     * Get the value(content) of file
     * @param file
     * @return String
     * @throws IOException
     */


    /**
     * Todo: Serialize the object to the local repository.
     * @throws Exception
     */
    public void writeObject() throws Exception{
        FileCreation.createDirectory(path, key.substring(0,2));
        FileOutputStream fos = new FileOutputStream(path + File.separator + key.substring(0, 2) + File.separator + key.substring(2));
        ObjectOutputStream objectStream = new ObjectOutputStream(fos);
        objectStream.writeObject(this);
        objectStream.close();
    }

    /**
     * Todo: Serialize the object and compress with gzip.
     * @throws Exception
     */
    public void compressWrite() throws Exception{
        // 已存在对应文件，return
        if (FileReader.objectExists(key)) return;
        // 查看是否存在objects下的前两位文件夹，没有则新建
        File first = new File(path + File.separator + key.substring(0,2));
        if(!first.exists()) FileCreation.createDirectory(path, key.substring(0,2));
        FileWriter.writeCompressedObj(path + File.separator + key.substring(0, 2) + File.separator + key.substring(2), this);
    }

}

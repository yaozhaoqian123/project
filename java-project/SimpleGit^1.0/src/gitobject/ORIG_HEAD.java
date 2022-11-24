package gitobject;
import fileoperation.FileWriter;
import repository.Repository;
import java.io.File;
import java.io.Serializable;

public class ORIG_HEAD implements Serializable {
    static String path = Repository.getGitDir() + File.separator + "ORIG_HEAD";
    private String cur_commit = null;

    public ORIG_HEAD() {}

    public ORIG_HEAD(String key) {
        this.cur_commit = key;
    }

    public String getCurrentCommit() {
        return cur_commit;
    }

    public void compressWrite() {
        FileWriter.writeCompressedObj(path, this);
    }

    public static String getPath() {
        return path;
    }
}
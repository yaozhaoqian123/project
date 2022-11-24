package gitobject;

import fileoperation.FileReader;
import fileoperation.FileWriter;
import repository.Repository;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;

public class Status implements Serializable {
    HashSet<String> trackedSet = new HashSet<>();
    static String path = Repository.getGitDir() + File.separator + "Status";
    public Status () {}

    public static Status deserialize() {
        return FileReader.readCompressedObj(path, Status.class);
    }

    public void add(String name) {
        trackedSet.add(name);
    }

    public void remove(String name) {
        trackedSet.remove(name);
    }

    public void writeStatus() {
        FileWriter.writeCompressedObj(path, this);
    }

    public HashSet<String> getTrackedSet() {
        return trackedSet;
    }
}

package gitobject;

import fileoperation.FileReader;
import fileoperation.FileWriter;
import repository.Repository;

import java.io.File;

import java.io.Serializable;

public class Head implements Serializable {
    static String path = Repository.getGitDir() + File.separator + "HEAD";   //absolute path of HEAD.
    private String targetName = "master";

    public Head() {}

    public void updateTarget(Branch new_target) {
        this.targetName = new_target.getBranchName();
    }

    public static String getCurrentCommit()  {
        Branch curbranch = Branch.getCurBranch();
        return curbranch.getCommitId();

    }

    public String getTargetName() {
        return targetName;
    }

    public void compressWrite() {
        FileWriter.writeCompressedObj(path, this);
    }

    public static String getPath() {
        return path;
    }

    public static Head deserialize() {
        return FileReader.readCompressedObj(path, Head.class);
    }


}

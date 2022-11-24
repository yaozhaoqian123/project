package core;

import fileoperation.FileReader;
import gitobject.Blob;
import gitobject.Index;
import gitobject.Tree;
import repository.Repository;

import java.io.File;

public class JitRm {
    public static void rm(String filename) throws Exception {
        File f = new File(Repository.getWorkTree() + File.separator + filename);
        //文件不存在时
        if (!f.exists()) {
            System.out.println(filename + "does not exist.");
            return;
        }

        //读入index文件
        Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);
        if (f.isFile()) {
            index.deleteFile(filename);
        }
        else if (f.isDirectory()) {
            index.deleteDirectory(filename);
        }
        //覆盖原有文件
        index.compressWrite();
    }

}

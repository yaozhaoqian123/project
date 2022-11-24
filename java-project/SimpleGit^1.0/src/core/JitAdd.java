package core;

import fileoperation.FileReader;
import gitobject.*;
import repository.Repository;

import java.io.File;

public class JitAdd {
    public static void add (String filename) throws Exception {
        File f = new File(Repository.getWorkTree() + File.separator + filename);
        //文件不存在时
        if (!f.exists()) {
            System.out.println(filename + "does not exist.");
        }

        else {
            //读入index文件
            Index index = FileReader.readCompressedObj(Index.getPath(), Index.class);

            if (f.isFile()) {
                try {
                    Blob b = new Blob(f, filename);
                    index.add(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (f.isDirectory()) {
                Tree t = new Tree(f, filename);
                index.add(t);
            }
            //覆盖原有文件
            index.compressWrite();
        }
    }
}

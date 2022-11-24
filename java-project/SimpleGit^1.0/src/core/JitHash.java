package core;

import fileoperation.FileCreation;

import gitobject.Blob;
import gitobject.Tree;
import repository.Repository;

import java.io.File;
import java.io.IOException;

public class JitHash {
    /**
     * Init repository in your working area.
     * @param filename
     * @throws IOException
     */
    public static void hash(String filename) throws IOException {
        /* Todo: You should pass the filename in this function, and generate a hash file in your repository.
        *   Add your code here.*/
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println(filename +  "does not exist.");
            return;
        }

        if(file.isFile()){
            try{
                Blob blob = new Blob(file, filename);
                blob.compressWrite();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(file.isDirectory()){
            try {
                Tree tree = new Tree(file, filename);
                tree.compressWrite();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

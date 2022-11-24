package gitobject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import fileoperation.FileReader;
import sha1.SHA1;

public class Commit extends GitObject{
    protected String tree; 		// the sha1 value of present committed tree
    protected String parent; 	// the sha1 value of the parent commit
    protected String author; 	// the author's name and timestamp
    protected String committer; // the committer's info
    protected String message; 	// the commit memo
    protected String date;      // the commit date

    public String getParent(){return parent;}
    public String getTree(){return tree;}
    public String getAuthor(){return author;}
    public String getCommitter(){return committer;}
    public String getMessage(){return message;}
    public String getDate(){return date;}

    public Commit(){}
    /**
     * Construct a commit directly from a file.
     * @param
     * author, committer, message参数在git commit命令里创建
     * @throws Exception
     */
    public Commit(String treePath, String author, String committer, String message) throws Exception {
        this.fmt = "commit"; 	//type of object
        this.tree = new Tree(new File(treePath)).getKey(); 
        this.parent = getLastCommit() == null ? "" : getLastCommit(); //null means there is no parent commit.
        this.author = author;
        this.committer = committer;
        this.message = message;
        this.name = null;

        //获得命令执行时的时间.
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        this.date = date;

        /*Content of this commit, like this:
         *tree bd31831c26409eac7a79609592919e9dcd1a76f2
         *parent d62cf8ef977082319d8d8a0cf5150dfa1573c2b7
         *author xxx  1502331401 +0800
         *committer xxx  1502331401 +0800
         *修复增量bug
         * */
        this.value = "tree " + this.tree + "\nparent " + this.parent+ "\nauthor " + this.author + "\ncommitter " + this.committer + "\n" + this.message;
        key = genKey();
    }
    
    /**
     * Construct a commit from a built tree.
     * @param
     * author, committer, message参数在git commit命令里创建
     * @throws Exception
     */
    public Commit(Tree t, String author, String committer, String message) throws Exception {
        this.fmt = "commit"; 	//type of object
        this.tree = t.getKey(); 
        this.parent = getLastCommit() == null ? "" : getLastCommit(); //null means there is no parent commit.
        this.author = author;
        this.committer = committer;
        this.message = message;

        //获得命令执行时的时间.
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        this.date = date;

        /*Content of this commit, like this:
         *tree bd31831c26409eac7a79609592919e9dcd1a76f2
         *parent d62cf8ef977082319d8d8a0cf5150dfa1573c2b7
         *author xxx  1502331401 +0800
         *committer xxx  1502331401 +0800
         *修复增量bug
         * */
        this.value = "tree " + this.tree + "\nparent " + this.parent+ "\nauthor " + this.author + "\ncommitter " + this.committer + "\n" + this.message;
        key = genKey();
    }

    /**
     * Generate the hash value of this commit.
     * @return key
     * */
    public String genKey() throws Exception {
        key = SHA1.getHash(value);
        return key;
    }


    /**
     * Get the parent commit from the HEAD file.
     * @return
     * @throws IOException
     */
    public static String getLastCommit()  {
        Head head = FileReader.readCompressedObj(Head.getPath(), Head.class);
        String key = head.getCurrentCommit();
        return key;
    }

    public static Commit deserialize(String Id)  {
        try{
            return FileReader.readCompressedObj(path +  File.separator + Id.substring(0,2) + File.separator + Id.substring(2), Commit.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public Index getIndexTree() {
        Index res = (Index)Tree.deserialize(tree);
        return res;
    }
}


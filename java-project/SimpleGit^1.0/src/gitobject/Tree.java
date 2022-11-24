package gitobject;

import sha1.SHA1;

import fileoperation.FileReader;

import java.io.*;

import java.util.*;


public class Tree extends GitObject{

    protected HashMap<String, String> blobMap = new HashMap<>();          // 对应的blob key值
    protected HashMap<String,Tree> treeMap = new HashMap<>();     // tree下对应的tree <key, Tree>

    public HashMap<String, String> getBlobMap() {
        return blobMap;
    }

    public HashMap<String, Tree> getTreeMap() {
        return treeMap;
    }

    // workTree下文件对应的Blob名称 01.txt
    // workTree下文件夹内对应的Blob名称 test/01.txt ; test/test1/01.txt

    private void constructTree(File dir, String name) {
        List<File> files = sortFile(dir.listFiles());

        for(File f : files) {
            String newname = name + File.separator + f.getName();

            if (f.isFile()) {
                try {
                    // 创建Blob 将对应的对象写入objects
                    Blob b = new Blob(f, newname);
                    b.compressWrite();
                    blobMap.put(b.getKey(), newname);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Tree t = new Tree(f, newname);
                    treeMap.put(t.getKey(), t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Tree(){
        this.fmt = "tree";
        this.mode = "040000";
    }

    public Tree(String name){
        this.fmt = "tree";
        this.mode = "040000";
        this.name = name;
    }
    
    /**
     * Constructor
     * @param dir
     * @throws Exception
     */
    public Tree(File dir) throws Exception {

        if (dir.isFile()) throw new IllegalArgumentException("Must be a directory.");
        this.fmt = "tree";
        this.mode = "040000";
        this.name = dir.getName();
        constructTree(dir, name);
        this.value = genValue();
        this.key = genKey();
    }

    //主要用于建树时Tree命名以及treelist赋值.
    public Tree(File dir, String name) throws Exception {

        if (dir.isFile()) throw new IllegalArgumentException("Must be a directory.");
        this.fmt = "tree";
        this.mode = "040000";
        this.name = name;
        constructTree(dir, name);
        this.value = genValue();
        this.key = genKey();
    }

    /**
     * Deserialize a tree object with treeId and its path.
     * @param Id
     * @param Id
     * @throws IOException
     */
    public static Tree deserialize(String Id)  {
        try{

            return FileReader.readCompressedObj(path +  File.separator + Id.substring(0,2) + File.separator + Id.substring(2), Tree.class);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Sort the files in a certain order. You should consider whether it's a file or a directory.
     * @param fs
     * @return List
     */
    public static List sortFile(File[] fs){
        List fileList = Arrays.asList(fs);
        Collections.sort(fileList, new Comparator<File>() {
            // 按照文件大小进行排序
            @Override
            public int compare(File o1, File o2) {
                long diff = o1.length() - o2.length();
                if (diff > 0) return 1;
                else if (diff == 0) return 0;
                else return -1;

            }
        });
        return fileList;
    }

    public String genValue()  {
        StringBuffer bf = new StringBuffer();
        for (String key : blobMap.keySet()) {
            bf.append("100644 blob " + key + " " + blobMap.get(key) + "\n");
        }
        for (Tree t : treeMap.values()) {
            bf.append(t.toString() + " "  + t.getName() + "\n");
        }
        return bf.toString();
    }


    /**
     * Generate the key of a tree object.
     * @param
     * @return String
     * @throws Exception
     */
    public String genKey() throws Exception{
        return SHA1.getHash("040000 tree " + value);
    }


    protected boolean addBlobWithSameName(String target_key, String new_key, String new_name, HashMap nkmap) {
        if (blobMap.containsKey(target_key)) {
            blobMap.remove(target_key);
            blobMap.put(new_key, new_name);
            update();
            return true;
        }
        else{
            for (Tree t :treeMap.values()) {
                if (t.addBlobWithSameName(target_key, new_key, new_name, nkmap)){
                    update();
                    nkmap.put(t.getName(),t.getKey());
                    return true;
                }
            }
        }
        throw new IllegalArgumentException("No file with such name is found.");
    }

    protected boolean addTreeWithSameName(String target_key, String new_key, Tree new_tree, HashMap nkmap) {
        if (treeMap.containsKey(target_key)) {
            treeMap.remove(target_key);
            treeMap.put(new_key, new_tree);
            update();
            return true;
        }
        else{
            for (Tree t :treeMap.values()) {
                if (t.addTreeWithSameName(target_key, new_key, new_tree, nkmap)){
                    update();
                    nkmap.put(t.getName(),t.getKey());
                    return true;
                }
            }
        }
        throw new IllegalArgumentException("No directory with such name is found.");
    }

    protected boolean addNewBlob(String parent_key, String new_key, String new_name, HashMap nkmap) {
        if (treeMap.containsKey(parent_key)) {
            Tree parent = treeMap.get(parent_key);
            parent.getBlobMap().put(new_key, new_name);
            parent.update();
            treeMap.remove(parent_key);
            treeMap.put(parent.getKey(), parent);
            update();
            nkmap.put(parent.getName(), parent.getKey());
            return true;
        }
        else{
            for (Tree t :treeMap.values()) {
                if (t.addNewBlob(parent_key, new_key, new_name, nkmap)){
                    update();
                    nkmap.put(t.getName(),t.getKey());
                    return true;
                }
            }
        }
        throw new IllegalArgumentException("No file with such parent name is found.");
    }

    protected boolean addNewTree(String parent_key, String new_key, Tree new_tree, HashMap nkmap) {
        if (treeMap.containsKey(parent_key)) {
            Tree parent = treeMap.get(parent_key);
            parent.getTreeMap().put(new_key, new_tree);
            parent.update();
            treeMap.remove(parent_key);
            treeMap.put(parent.getKey(), parent);
            update();
            nkmap.put(parent.getName(), parent.getKey());
            return true;
        }
        else{
            for (Tree t :treeMap.values()) {
                if (t.addNewTree(parent_key, new_key, new_tree, nkmap)){
                    update();
                    nkmap.put(t.getName(),t.getKey());
                    return true;
                }
            }
        }
        throw new IllegalArgumentException("No file with such parent name is found.");
    }

    // 在树中找到并删除Blob key值对应的的gitobject，并更新相应的值.
    // 找到了返回 true 否则 返回 false;
    public boolean deleteBlob (String key) {
        if (blobMap.containsKey(key)) {
            blobMap.remove(key);
            update();
            return true;
        }
        else {
            for (Tree t : treeMap.values()) {
                if (t.deleteBlob(key)) {
                    update();
                    return true;
                }
            }
            return false;
        }
    }

    // 在树中找到并删除Tree key值对应的的gitobject，并更新相应的值.
    // 找到了返回 true 否则 返回 false;
    public boolean deleteTree (String key) {
        if (treeMap.containsKey(key)) {
            treeMap.remove(key);
            update();
            return true;
        }
        else {
            for (Tree t : treeMap.values()) {
                if (t.deleteTree(key)) {
                    update();
                    return true;
                }
            }
            return false;
        }
    }

    public void update() {
        try {
            this.value = genValue();
            this.key = SHA1.getHash("040000 tree " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void traverse() {
        System.out.println(this.toString() + " " + this.getName());
        traverse(this);
    }

    private void traverse(Tree root) {
        System.out.println(root.getValue());
        for (Tree t : root.getTreeMap().values()) {
            traverse(t);
        }
    }

    @Override
    public String toString(){
        return "040000 tree " + key;
    }

}

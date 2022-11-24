package core;

import fileoperation.FileReader;
import gitobject.Index;

public class JitLs {
    public static void lsfiles() {
        Index index = FileReader.readCompressedObj(Index.getPath(), Index.class);
        index.show();
    }
}

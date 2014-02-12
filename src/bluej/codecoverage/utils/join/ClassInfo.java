package bluej.codecoverage.utils.join;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import bluej.extensions.BClass;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;

public interface ClassInfo
{
    String getName();
    String getLine(int lineNum);
    int getNumberOfLines();
}
class FileClassInfo implements ClassInfo {
    private File file;
    private String name;
    private LineNumberReader reader;
    private List<String> fileData;
    public FileClassInfo(File source, String name) throws Exception {
        this.file = source;
        this.name = name;
        reader = new LineNumberReader(new InputStreamReader(new FileInputStream(source)));
        fileData = new ArrayList<String>();
        String input;
        while ((input = reader.readLine()) != null)
        {
            fileData.add(input);
        }
    }
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getLine(int lineNum)
    {
       return fileData.get(lineNum);
    }
    @Override
    public int getNumberOfLines()
    {
        return fileData.size();
    }
}
class BClassInfo implements ClassInfo {
    private BClass clz;
    Editor editor;
    public BClassInfo(BClass clz) throws Exception
    {
        this.clz = clz;   
        editor = clz.getEditor();
    }

    @Override
    public String getName()
    {
        return clz.getName();
    }

    @Override
    public String getLine(int lineNum)
    {
       

        return editor.getText(new TextLocation(lineNum, 0), new TextLocation(lineNum,
            editor.getLineLength(lineNum) - 1));
    }

    @Override
    public int getNumberOfLines()
    {
        return editor.getLineCount();
    }
    
}
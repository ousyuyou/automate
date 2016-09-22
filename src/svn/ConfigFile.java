package svn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @version 1.3
 * @author  TMate Software Ltd.
 */
public class ConfigFile {

    private File myFile;
    private String[] myLines;
    private long myLastModified;

    public ConfigFile(File file) {
        myFile = file.getAbsoluteFile();
    }
    
    protected String[] getLines() {
        return myLines;
    }

    public Map getProperties(String groupName) {
        Map map = new LinkedHashMap();
        load();
        boolean groupMatched = false;
        for (int i = 0; i < myLines.length; i++) {
            String line = myLines[i];
            if (line == null) {
                continue;
            }
            if (!groupMatched && matchGroup(line, groupName)) {
                groupMatched = true;
            } else if (groupMatched) {
                if (matchGroup(line, null)) {
                    break;
                } else if (matchProperty(line, null)) {
                    map.put(getPropertyName(line), null);
                }
            }
        }
        Map result = new LinkedHashMap();
        for (Iterator names = map.keySet().iterator(); names.hasNext();) {
            String propertyName = (String) names.next();
            result.put(propertyName, getPropertyValue(groupName, propertyName));
        }
        return result;
    }

    public String getPropertyValue(String groupName, String propertyName) {
        load();
        boolean groupMatched = false;
        for (int i = 0; i < myLines.length; i++) {
            String line = myLines[i];
            if (line == null) {
                continue;
            }
            if (!groupMatched && matchGroup(line, groupName)) {
                groupMatched = true;
            } else if (groupMatched) {
                if (matchGroup(line, null)) {
                    return null;
                } else if (matchProperty(line, propertyName)) {
                    String firstLine = getPropertyValue(line);
                    
                    if (firstLine != null) {
                        int j = i + 1;
                        while(j < myLines.length && myLines[j] != null) {
                            String nextLine = myLines[j++];
                            if (!matchGroup(nextLine, null) && !matchProperty(nextLine, null)) {
                                if (nextLine.length() > 0 && Character.isWhitespace(nextLine.charAt(0))) {
                                    firstLine += nextLine;
                                    continue;
                                } 
                            } 
                            break;
                        }
                    }
                    return firstLine;
                }
            }
        }
        return null;
    }

    public void setPropertyValue(String groupName, String propertyName, String propertyValue, boolean save) {
        load();
        boolean groupMatched = false;
        for (int i = 0; i < myLines.length; i++) {
            String line = myLines[i];
            if (line == null) {
                continue;
            }
            if (!groupMatched && matchGroup(line, groupName)) {
                groupMatched = true;
            } else if (groupMatched) {
                if (matchGroup(line, null) /* or last line found*/) {
                    // property was not saved!!!
                    if (propertyValue != null) {
                        String[] lines = new String[myLines.length + 1];
                        System.arraycopy(myLines, 0, lines, 0, i);
                        System.arraycopy(myLines, i, lines, i + 1,
                                myLines.length - i);
                        lines[i] = propertyName + "  = " + propertyValue;
                        myLines = lines;
                        if (save) {
                            save();
                        }
                    }

                    return;
                } else if (matchProperty(line, propertyName)) {
                    if (propertyValue == null) {
                        myLines[i] = null;
                    } else {
                        myLines[i] = propertyName + " = " + propertyValue;
                    }
                    if (save) {
                        save();
                    }
                    return;
                } 
            }
        }
        if (propertyValue != null) {
            
            String[] lines = new String[myLines.length + (groupMatched ? 1 : 2)];
            if (!groupMatched) {
                lines[lines.length - 2] = "[" + groupName + "]";
            }
            lines[lines.length - 1] = propertyName + "  = " + propertyValue;
            System.arraycopy(myLines, 0, lines, 0, myLines.length);
            myLines = lines;
            if (save) {
                save();
            }
        }
    }

    public void deleteGroup(String groupName, boolean save) {
        load();
        boolean groupMatched = false;
        for (int i = 0; i < myLines.length; i++) {
            String line = myLines[i];
            if (line == null) {
                continue;
            }
            if (!groupMatched && matchGroup(line, groupName)) {
                groupMatched = true;
                myLines[i] = null;
            } else if (groupMatched) {
                if (matchGroup(line, null) /* or last line found*/) {
                    break;
                }
                myLines[i] = null;
            }
        }
        if (save) {
            save();
        }
    }

    private static boolean matchGroup(String line, String name) {
        line = line.trim();
        if (line.startsWith("[") && line.endsWith("]")) {
            return name == null || line.substring(1, line.length() - 1).equals(name);
        }
        return false;
    }

    private static boolean matchProperty(String line, String name) {
        line = line.trim();
        if (line.startsWith("#")) {
            return false;
        }
        if (line.indexOf('=') < 0) {
            return false;
        }
        line = line.substring(0, line.indexOf('='));
        return name == null || line.trim().equals(name);
    }

    private static String getPropertyValue(String line) {
        line = line.trim();
        if (line.indexOf('=') < 0) {
            return null;
        }
        line = line.substring(line.indexOf('=') + 1);
        return line.trim();
    }

    private static String getPropertyName(String line) {
        line = line.trim();
        if (line.indexOf('=') < 0) {
            return null;
        }
        line = line.substring(0, line.indexOf('='));
        return line.trim();
    }

    // parse all lines from the file, keep them as lines array.
    public void save() {
        if (myLines == null) {
            return;
        }
        if (myFile.isDirectory()) {
            return;
        }
        if (myFile.getParentFile() != null) {
            myFile.getParentFile().mkdirs();
        }
        Writer writer = null;
        String eol = System.getProperty("line.separator");
        eol = eol == null ? "\n" : eol;
        try {
            writer = new FileWriter(myFile);
            for (int i = 0; i < myLines.length; i++) {
                String line = myLines[i];
                if (line == null) {
                    continue;
                }
                writer.write(line);
                writer.write(eol);
            }
        } catch (IOException e) {
            //
        } finally {
        	try{
        		writer.close();
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        myLastModified = myFile.lastModified();
        myLines = doLoad(myFile);
    }

    private void load() {
        if (myLines != null && myFile.lastModified() == myLastModified) {
            return;
        }
        myLastModified = myFile.lastModified();
        myLines = doLoad(myFile);
        myLastModified = myFile.lastModified();
    }

    public boolean isModified() {
        if (myLines == null) {
            return false;
        }
        String[] lines = doLoad(myFile);
        if (lines.length != myLines.length) {
            return true;
        }
        for (int i = 0; i < myLines.length; i++) {
            String line = myLines[i];
            if (line == null) {
                return true;
            }
            if (!line.equals(lines[i])) {
                return true;
            }
        }
        return false;
    }

    private String[] doLoad(File file) {
        if (!file.isFile() || !file.canRead()) {
            return new String[0];
        }
        BufferedReader reader = null;
        Collection lines = new ArrayList();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(myFile), "sjis"));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            lines.clear();
        } finally {
        	try{
        		reader.close();
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        return (String[]) lines.toArray(new String[lines.size()]);
    }
}
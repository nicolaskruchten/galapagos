package ca.utoronto.civ.its.galapagos.controller.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;


public class CustomFileFilter extends FileFilter
{
    private String ext;
    private String desc;

    public CustomFileFilter(String ext, String desc)
    {
        this.ext = ext;
        this.desc = desc;
    }

    public boolean accept(File f)
    {
        if(f.isDirectory())
        {
            return true;
        }

        String extension = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if((i > 0) && (i < (s.length() - 1)))
        {
            extension = s.substring(i + 1).toLowerCase();
        }

        return ((extension != null) && extension.equals(ext));
    }

    public String getDescription()
    {
        return desc;
    }
}

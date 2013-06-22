package ca.utoronto.civ.its.galapagos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;


public class ParamTreeNode
{
    private String value;
    private Hashtable children = null;
    private Vector childnames = null;

    public ParamTreeNode(String text)
    {
        this.value = text;
    }

    public String getString()
    {
        return value;
    }

    public int getInt()
    {
        return Integer.parseInt(value);
    }

    public float getFloat()
    {
        return Float.parseFloat(value);
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public int getNumChildren(String name) throws ParamException
    {
        if(!childExists(name))
        {
            throw new ParamException("\n\nParameter missing: " + name + "\n");
        }

        return ((Vector)children.get(name)).size();
    }

    public ParamTreeNode getChild(String name) throws ParamException
    {
        return getChild(name, 0);
    }

    public ParamTreeNode getChild(String name, int index) throws ParamException
    {
        if(!childExists(name))
        {
            throw new ParamException("\n\nParameter missing: " + name + "\n");
        }

        return (ParamTreeNode)((Vector)children.get(name)).elementAt(index);
    }

    public void addChild(String name, ParamTreeNode element)
    {
        if(children == null)
        {
            children = new Hashtable();
            childnames = new Vector();
        }

        if(!childExists(name))
        {
            children.put(name, new Vector());
            ((Vector)children.get(name)).addElement(element);
            childnames.addElement(name);
        }
        else
        {
            ((Vector)children.get(name)).addElement(element);
        }
    }

    public boolean childExists(String name)
    {
        return ((children != null) && (children.get(name) != null));
    }

    public String toXMLString()
    {
        String theXMLString = "";

        if(!value.equals(""))
        {
            theXMLString += (value + " ");
        }

        if(children != null)
        {
            for(int i = 0; i < childnames.size(); i++)
            {
                String childname = (String)childnames.elementAt(i);

                for(int j = 0; j < ((Vector)children.get(childname)).size();
                        j++)
                {
                    theXMLString += ("<" + childname + "> ");
                    theXMLString += ((ParamTreeNode)((Vector)children.get(childname)).elementAt(j)).toXMLString();
                    theXMLString += ("</" + childname + "> ");
                }
            }
        }

        return theXMLString;
    }

    public static ParamTreeNode parse(String theString) throws ParamException
    {
        return ParamTreeNode.parse(new BufferedReader(new StringReader(theString)));
    }

    public static ParamTreeNode parse(Reader theReader) throws ParamException
    {
        return (new SimpleDOMParser(theReader)).parse();
    }

    private static class SimpleDOMParser
    {
        private final int[] cdata_start = 
        {
            '<', '!', '[', 'C', 'D', 'A', 'T', 'A', '['
        };
        private final int[] cdata_end = { ']', ']', '>' };
        private Reader reader;
        private Stack elements;
        private Stack tagNames;
        private ParamTreeNode openElement;
        private String openTag;

        public SimpleDOMParser(Reader reader)
        {
            this.elements = new Stack();
            this.tagNames = new Stack();
            this.openElement = null;
            this.openTag = null;
            this.reader = reader;
        }

        public ParamTreeNode parse() throws ParamException
        {
            try
            {
                // skip xml declaration or DocTypes
                skipPrologs();

                while(true)
                {
                    // remove the prepend or trailing white spaces
                    String thisTag = readTag().trim();

                    if(!thisTag.startsWith("</"))
                    {
                        // open tag
                        thisTag = thisTag.substring(1, thisTag.length() - 1);

                        // create new element
                        ParamTreeNode thisElement;

                        thisElement = new ParamTreeNode(readText().trim());

                        // add new element as a child element of
                        // the current element
                        if(openElement != null)
                        {
                            openElement.addChild(thisTag, thisElement);
                            elements.push(openElement);
                            tagNames.push(openTag);
                        }

                        openElement = thisElement;
                        openTag = thisTag;
                    }
                    else
                    {
                        // close tag
                        thisTag = thisTag.substring(2, thisTag.length() - 1);

                        // no open tag
                        if(openElement == null)
                        {
                            throw new ParamException("Got close tag '" + thisTag + "' without open tag.");
                        }

                        // close tag does not match with open tag
                        if(!thisTag.equals(openTag))
                        {
                            throw new ParamException("Expected close tag for '" + openTag + "' but got '" + thisTag + "'.");
                        }

                        if(!elements.empty())
                        {
                            // pop up the previous open tag
                            openElement = (ParamTreeNode)elements.pop();
                            openTag = (String)tagNames.pop();
                        }
                        else
                        {
                            // document processing is over
                            return openElement;
                        }
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();

                return null;
            }
        }

        private int peek() throws IOException
        {
            reader.mark(1);

            int result = reader.read();
            reader.reset();

            return result;
        }

        private void peek(int[] buffer) throws IOException
        {
            reader.mark(buffer.length);

            for(int i = 0; i < buffer.length; i++)
            {
                buffer[i] = reader.read();
            }

            reader.reset();
        }

        private void skipWhitespace() throws IOException
        {
            while(Character.isWhitespace((char)peek()))
            {
                reader.read();
            }
        }

        private void skipProlog() throws IOException
        {
            // skip "<?" or "<!"
            reader.skip(2);

            while(true)
            {
                int next = peek();

                if(next == '>')
                {
                    reader.read();

                    break;
                }
                else if(next == '<')
                {
                    // nesting prolog
                    skipProlog();
                }
                else
                {
                    reader.read();
                }
            }
        }

        private void skipPrologs() throws ParamException, IOException
        {
            while(true)
            {
                skipWhitespace();

                int[] next = new int[2];
                peek(next);

                if(next[0] != '<')
                {
                    throw new ParamException("Expected '<' but got '" + (char)next[0] + "'.");
                }

                if((next[1] == '?') || (next[1] == '!'))
                {
                    skipProlog();
                }
                else
                {
                    break;
                }
            }
        }

        private String readTag() throws ParamException, IOException
        {
            skipWhitespace();

            StringBuffer sb = new StringBuffer();

            int next = peek();

            if(next != '<')
            {
                throw new ParamException("Expected < but got " + (char)next);
            }

            sb.append((char)reader.read());

            while(peek() != '>')
            {
                sb.append((char)reader.read());
            }

            sb.append((char)reader.read());

            return sb.toString();
        }

        private String readText() throws IOException
        {
            StringBuffer sb = new StringBuffer();

            int[] next = new int[cdata_start.length];
            peek(next);

            if(compareIntArrays(next, cdata_start) == true)
            {
                // CDATA
                reader.skip(next.length);

                int[] buffer = new int[cdata_end.length];

                while(true)
                {
                    peek(buffer);

                    if(compareIntArrays(buffer, cdata_end) == true)
                    {
                        reader.skip(buffer.length);

                        break;
                    }
                    else
                    {
                        sb.append((char)reader.read());
                    }
                }
            }
            else
            {
                while(peek() != '<')
                {
                    sb.append((char)reader.read());
                }
            }

            return sb.toString();
        }

        private boolean compareIntArrays(int[] a1, int[] a2)
        {
            if(a1.length != a2.length)
            {
                return false;
            }

            for(int i = 0; i < a1.length; i++)
            {
                if(a1[i] != a2[i])
                {
                    return false;
                }
            }

            return true;
        }
    }
}

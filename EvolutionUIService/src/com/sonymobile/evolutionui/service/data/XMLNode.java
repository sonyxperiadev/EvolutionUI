/*
 * Copyright (C) 2014 Sony Mobile Communications AB
 *
 * This file is part of EvolutionUI.
 *
 * EvolutionUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * EvolutionUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EvolutionUI. If not, see <http://www.gnu.org/licenses/>.
 */
package com.sonymobile.evolutionui.service.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;

import com.sonymobile.evolutionui.service.Util;

/**
 * A simple generic xml node class.
 */
public class XMLNode {

    public static final String TEXT_ATTR = "<text>";

    private XMLNode mParent;
    private Vector<XMLNode> mChildren = new Vector<XMLNode>();
    private HashMap<String, String> mAttributes = new HashMap<String, String>();
    private String mName;

    public XMLNode(String name, XMLNode parent) {
        mName = name;
        mParent = parent;
        if (mParent != null) {
            mParent.addChild(this);
        }
    }

    public String getName() {
        return mName;
    }

    public XMLNode getParent() {
        return mParent;
    }

    public int getChildCount() {
        return mChildren.size();
    }

    public XMLNode getChild(int idx) {
        return mChildren.get(idx);
    }

    public void setAttribute(String key, String value) {
        mAttributes.put(key, value);
    }

    public void setAttribute(String key, int value) {
        setAttribute(key, Integer.toString(value));
    }

    public void setAttribute(String key, boolean value) {
        setAttribute(key, Boolean.toString(value));
    }

    public String getAttribute(String key) {
        return mAttributes.get(key);
    }

    public void addChild(XMLNode data) {
        if (data == null) return;
        mChildren.add(data);
        data.mParent = this;
    }

    public void removeChild(XMLNode oldData) {
        if (oldData == null) return;
        mChildren.remove(oldData);
        oldData.mParent = null;
    }

    public XMLNode findChild(String name) {
        for (XMLNode child : mChildren) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public static XMLNode parse(Context context, XmlPullParser parser) throws XmlPullParserException, IOException {
        XMLNode cur = null;
        XMLNode root = null;

        // Read the next tag
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                // Found a new tag
                cur = new XMLNode(parser.getName(), cur);
                if (root == null) {
                    root = cur;
                }

                // Read it's parameters
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                    String key = parser.getAttributeName(i);
                    String value = parser.getAttributeValue(i);
                    value = Util.checkStringRes(context, value);
                    cur.setAttribute(key, value);
                }
            } else if (event == XmlPullParser.END_TAG) {
                if (cur == null) {
                    throw new XmlPullParserException("Incorrect config XML");
                }
                cur = cur.getParent();
            } else if (event == XmlPullParser.TEXT) {
                cur.setAttribute(TEXT_ATTR, parser.getText());
            }

            // Fetch the next token
            event = parser.next();
        }
        return root;
    }

    /**
     * Read an entire XML document and return it as a tree
     * @param context An android context to access resources
     * @param f The xml file to read
     * @return The tree or null if the file is missing
     */
    public static XMLNode readDocument(Context context, File f) {
        if (!f.exists()) {
            return null;
        }

        // parse the configuration file
        XMLNode ret = null;
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();

            FileInputStream fs = new FileInputStream(f);
            try {
                parser.setInput(fs, null);
                ret = XMLNode.parse(context, parser);
            } finally {
                fs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Writes the current node (and it's subnode) into an xml file
     * @param out The xml file
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    public void writeTo(XmlSerializer out) throws IllegalArgumentException, IllegalStateException, IOException {
        out.startTag(null, mName);
        for (String key : mAttributes.keySet()) {
            String value = mAttributes.get(key);
            out.attribute(null, key, value);
        }
        for (XMLNode child : mChildren) {
            child.writeTo(out);
        }
        out.endTag(null, mName);
    }

    /**
     * Writes the whole tree into an XML file.
     * @param f The file to write
     * @return true if it was successful
     */
    public boolean writeDocumentTo(File f) {
        try {
            XmlSerializer out = XmlPullParserFactory.newInstance().newSerializer();
            FileOutputStream fo = new FileOutputStream(f);
            try {
                out.setOutput(fo, "UTF-8");
                out.startDocument("UTF-8", true);
                writeTo(out);
                out.endDocument();
            } finally {
                fo.close();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public XMLNode findNodeByName(XMLNode config, String name) {
        for (int i = 0; i < getChildCount(); i++) {
            XMLNode child = getChild(i);
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public XMLNode findNodeByAttr(String key, String value) {
        for (int i = 0; i < getChildCount(); i++) {
            XMLNode child = getChild(i);
            String val = child.getAttribute(key);
            if (val != null && val.equals(value)) {
                return child;
            }
        }
        return null;
    }

}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.ext.media;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.i18n.iri.IRI;

public class MediaContent extends ExtensibleElementWrapper {

    public MediaContent(Element internal) {
        super(internal);
    }

    public MediaContent(Factory factory) {
        super(factory, MediaConstants.CONTENT);
    }

    public IRI getUrl() {
        String url = getAttributeValue("url");
        return (url != null) ? new IRI(url) : null;
    }

    public void setUrl(String url) {
        if (url != null)
            setAttributeValue("url", (new IRI(url)).toString());
        else
            removeAttribute(new QName("url"));
    }

    public long getFilesize() {
        String size = getAttributeValue("filesize");
        return (size != null) ? Long.parseLong(size) : -1;
    }

    public void setFilesize(long size) {
        if (size > -1)
            setAttributeValue("filesize", String.valueOf(size));
        else
            removeAttribute(new QName("filesize"));
    }

    public MimeType getType() {
        try {
            String type = getAttributeValue("type");
            return (type != null) ? new MimeType(type) : null;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera.util.MimeTypeParseException(e);
        }
    }

    public void setType(String type) {
        if (type != null)
            setAttributeValue("type", type);
        else
            removeAttribute(new QName(type));
    }

    public MediaConstants.Medium getMedium() {
        String medium = getAttributeValue("medium");
        return (medium != null) ? MediaConstants.Medium.valueOf(medium.toUpperCase()) : null;
    }

    public void setMedium(MediaConstants.Medium medium) {
        if (medium != null)
            setAttributeValue("medium", medium.name().toLowerCase());
        else
            removeAttribute(new QName("medium"));
    }

    public boolean isDefault() {
        String def = getAttributeValue("isDefault");
        return (def != null) ? def.equalsIgnoreCase("true") : false;
    }

    public MediaConstants.Expression getExpression() {
        String exp = getAttributeValue("expression");
        return (exp != null) ? MediaConstants.Expression.valueOf(exp.toUpperCase()) : null;
    }

    public void setExpression(MediaConstants.Expression exp) {
        if (exp != null)
            setAttributeValue("expression", exp.name().toLowerCase());
        else
            removeAttribute(new QName("expression"));
    }

    public int getBitrate() {
        String bitrate = getAttributeValue("bitrate");
        return (bitrate != null) ? Integer.parseInt(bitrate) : -1;
    }

    public void setBitrate(int bitrate) {
        if (bitrate > -1)
            setAttributeValue("bitrate", String.valueOf(bitrate));
        else
            removeAttribute(new QName("bitrate"));
    }

    public int getFramerate() {
        String framerate = getAttributeValue("framerate");
        return (framerate != null) ? Integer.parseInt(framerate) : -1;
    }

    public void setFramerate(int framerate) {
        if (framerate > -1)
            setAttributeValue("framerate", String.valueOf(framerate));
        else
            removeAttribute(new QName("framerate"));
    }

    public double getSamplingRate() {
        String rate = getAttributeValue("samplingrate");
        return (rate != null) ? Double.parseDouble(rate) : -1;
    }

    public void setSamplingRate(double samplingrate) {
        if (samplingrate > Double.parseDouble("-1"))
            setAttributeValue("samplingrate", String.valueOf(samplingrate));
        else
            removeAttribute(new QName("samplingrate"));
    }

    public int getChannels() {
        String c = getAttributeValue("channels");
        return (c != null) ? Integer.parseInt(c) : -1;
    }

    public void setChannels(int channels) {
        if (channels > -1)
            setAttributeValue("channels", String.valueOf(channels));
        else
            removeAttribute(new QName("channels"));
    }

    public int getDuration() {
        String c = getAttributeValue("duration");
        return (c != null) ? Integer.parseInt(c) : -1;
    }

    public void setDuration(int duration) {
        if (duration > -1)
            setAttributeValue("duration", String.valueOf(duration));
        else
            removeAttribute(new QName("duration"));
    }

    public int getWidth() {
        String width = getAttributeValue("width");
        return (width != null) ? Integer.parseInt(width) : -1;
    }

    public void setWidth(int width) {
        if (width > -1) {
            setAttributeValue("width", String.valueOf(width));
        } else {
            removeAttribute(new QName("width"));
        }
    }

    public int getHeight() {
        String height = getAttributeValue("height");
        return (height != null) ? Integer.parseInt(height) : -1;
    }

    public void setHeight(int height) {
        if (height > -1) {
            setAttributeValue("height", String.valueOf(height));
        } else {
            removeAttribute(new QName("height"));
        }
    }

    public String getLang() {
        return getAttributeValue("lang");
    }

    public void setLang(String lang) {
        if (lang != null)
            setAttributeValue("lang", lang);
        else
            removeAttribute(new QName("lang"));
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.05.10 at 07:00:16 PM MSK 
//


package com.notjuststudio.engine2dgame.xml.spr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Sprite complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Sprite">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="x_offset" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="y_offset" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *       &lt;attribute name="source" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="accurateCollusionCheck" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sprite", propOrder = {
    "xOffset",
    "yOffset"
})
public class Sprite {

    @XmlElement(name = "x_offset", defaultValue = "0")
    protected int xOffset;
    @XmlElement(name = "y_offset", defaultValue = "0")
    protected int yOffset;
    @XmlAttribute(name = "source")
    protected String source;
    @XmlAttribute(name = "accurateCollusionCheck")
    protected Boolean accurateCollusionCheck;

    /**
     * Gets the value of the xOffset property.
     * 
     */
    public int getXOffset() {
        return xOffset;
    }

    /**
     * Sets the value of the xOffset property.
     * 
     */
    public void setXOffset(int value) {
        this.xOffset = value;
    }

    /**
     * Gets the value of the yOffset property.
     * 
     */
    public int getYOffset() {
        return yOffset;
    }

    /**
     * Sets the value of the yOffset property.
     * 
     */
    public void setYOffset(int value) {
        this.yOffset = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the accurateCollusionCheck property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isAccurateCollusionCheck() {
        if (accurateCollusionCheck == null) {
            return false;
        } else {
            return accurateCollusionCheck;
        }
    }

    /**
     * Sets the value of the accurateCollusionCheck property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAccurateCollusionCheck(Boolean value) {
        this.accurateCollusionCheck = value;
    }

}

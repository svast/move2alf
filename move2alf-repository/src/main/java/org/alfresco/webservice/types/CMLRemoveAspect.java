/**
 * CMLRemoveAspect.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.alfresco.webservice.types;

public class CMLRemoveAspect  implements java.io.Serializable {
    private java.lang.String aspect;

    private org.alfresco.webservice.types.Predicate where;

    private java.lang.String where_id;

    public CMLRemoveAspect() {
    }

    public CMLRemoveAspect(
           java.lang.String aspect,
           org.alfresco.webservice.types.Predicate where,
           java.lang.String where_id) {
           this.aspect = aspect;
           this.where = where;
           this.where_id = where_id;
    }


    /**
     * Gets the aspect value for this CMLRemoveAspect.
     * 
     * @return aspect
     */
    public java.lang.String getAspect() {
        return aspect;
    }


    /**
     * Sets the aspect value for this CMLRemoveAspect.
     * 
     * @param aspect
     */
    public void setAspect(java.lang.String aspect) {
        this.aspect = aspect;
    }


    /**
     * Gets the where value for this CMLRemoveAspect.
     * 
     * @return where
     */
    public org.alfresco.webservice.types.Predicate getWhere() {
        return where;
    }


    /**
     * Sets the where value for this CMLRemoveAspect.
     * 
     * @param where
     */
    public void setWhere(org.alfresco.webservice.types.Predicate where) {
        this.where = where;
    }


    /**
     * Gets the where_id value for this CMLRemoveAspect.
     * 
     * @return where_id
     */
    public java.lang.String getWhere_id() {
        return where_id;
    }


    /**
     * Sets the where_id value for this CMLRemoveAspect.
     * 
     * @param where_id
     */
    public void setWhere_id(java.lang.String where_id) {
        this.where_id = where_id;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CMLRemoveAspect)) return false;
        CMLRemoveAspect other = (CMLRemoveAspect) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.aspect==null && other.getAspect()==null) || 
             (this.aspect!=null &&
              this.aspect.equals(other.getAspect()))) &&
            ((this.where==null && other.getWhere()==null) || 
             (this.where!=null &&
              this.where.equals(other.getWhere()))) &&
            ((this.where_id==null && other.getWhere_id()==null) || 
             (this.where_id!=null &&
              this.where_id.equals(other.getWhere_id())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAspect() != null) {
            _hashCode += getAspect().hashCode();
        }
        if (getWhere() != null) {
            _hashCode += getWhere().hashCode();
        }
        if (getWhere_id() != null) {
            _hashCode += getWhere_id().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CMLRemoveAspect.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/cml/1.0", ">CML>removeAspect"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aspect");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/cml/1.0", "aspect"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("where");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/cml/1.0", "where"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.alfresco.org/ws/model/content/1.0", "Predicate"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("where_id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.alfresco.org/ws/cml/1.0", "where_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

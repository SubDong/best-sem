/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.18.0.3036 modeling language!*/

package com.perfect.autosdk_v4.sms.service;
import com.perfect.autosdk_v4.common.*;
import java.util.*;
import java.util.*;

// line 30 "../../../../../../../SDK.ump"
public class UpdateCreativeRequest
{
  @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
  public @interface umplesourcefile{int[] line();String[] file();int[] javaline();int[] length();}

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //UpdateCreativeRequest Attributes
  private List<CreativeType> creativeTypes;

  //------------------------
  // INTERFACE
  //------------------------

  public void setCreativeTypes(List<CreativeType> acreativeTypes){
    creativeTypes = acreativeTypes;
  }

  public boolean addCreativeType(CreativeType aCreativeType)
  {
    boolean wasAdded = false;
    wasAdded = creativeTypes.add(aCreativeType);
    return wasAdded;
  }

  public boolean removeCreativeType(CreativeType aCreativeType)
  {
    boolean wasRemoved = false;
    wasRemoved = creativeTypes.remove(aCreativeType);
    return wasRemoved;
  }

  public CreativeType getCreativeType(int index)
  {
    CreativeType aCreativeType = creativeTypes.get(index);
    return aCreativeType;
  }

  public List<CreativeType> getCreativeTypes()
  {
    return creativeTypes;
  }

  public int numberOfCreativeTypes()
  {
    int number = creativeTypes.size();
    return number;
  }

  public boolean hasCreativeTypes()
  {
    boolean has = creativeTypes.size() > 0;
    return has;
  }

  public int indexOfCreativeType(CreativeType aCreativeType)
  {
    int index = creativeTypes.indexOf(aCreativeType);
    return index;
  }

  public void delete()
  {}


  public String toString()
  {
	  String outputString = "";
    return super.toString() + "["+ "]"
     + outputString;
  }
}
/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.18.0.3036 modeling language!*/

package com.perfect.autosdk_v4.sms.service;
import com.perfect.autosdk_v4.common.*;
import java.util.*;

// line 56 "../../../../../../../SDK.ump"
public class GetKRFilePath
{
  @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
  public @interface umplesourcefile{int[] line();String[] file();int[] javaline();int[] length();}

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //GetKRFilePath Attributes
  private String filePath;

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setFilePath(String aFilePath)
  {
    boolean wasSet = false;
    filePath = aFilePath;
    wasSet = true;
    return wasSet;
  }

  public String getFilePath()
  {
    return filePath;
  }

  public void delete()
  {}


  public String toString()
  {
	  String outputString = "";
    return super.toString() + "["+
            "filePath" + ":" + getFilePath()+ "]"
     + outputString;
  }
}
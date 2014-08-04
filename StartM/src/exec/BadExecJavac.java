package exec;
import java.util.*;
import java.io.*;
public class BadExecJavac
{
    public static void main(String args[])
    {
        try
        {            
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("javac");
            int exitVal = proc.exitValue();
            System.out.println("Process exitValue: " + exitVal);
        } catch (Throwable t)
          {
            t.printStackTrace();
          }
    }
}
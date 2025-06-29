package org.example.jowgo.utility;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class Write
{
    private Write() {}

    /**
     * Overwrites a files content with new data.
     */
    public static void to_file(String file_path, String data)
    {
        try (BufferedWriter file_writer = new BufferedWriter(new FileWriter(file_path)))
        {
            file_writer.write(data);
        }
        catch (IOException e) { System.err.print("\nError writing lines to file: " + e.getMessage() + "\n\n"); }
    }
}
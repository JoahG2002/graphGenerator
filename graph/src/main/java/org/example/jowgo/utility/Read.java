package org.example.jowgo.utility;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.FileReader;
import java.util.Optional;
import java.io.IOException;
import java.io.BufferedReader;


public class Read
{
    private Read() {}

    /**
     * Reads all a file's lines, and returns them as a list of strings.
     */
    public static Optional<List<String>> file_lines(File file)
    {
        try (BufferedReader file_reader = new BufferedReader(new FileReader(file)))
        {
            String file_line;
            List<String> file_lines_ = new ArrayList<String>();

            while ((file_line = file_reader.readLine()) != null)
                file_lines_.add(file_line);

            if (file_lines_.isEmpty())
                return Optional.empty();

            return Optional.of(file_lines_);
        }
        catch (IOException e)
        {
            System.err.print("\nError reading file lines: " + e.getMessage() + "\n\n");

            return Optional.empty();
        }
    }
    /**
     * Reads all a file's lines, and returns them as an array of N strings.
     */
    public static Optional<String[]> file_lines(File file, int line_count)
    {
        try (BufferedReader file_reader = new BufferedReader(new FileReader(file)))
        {
            String file_line;
            String[] file_lines_ = new String[line_count];

            byte i = 0;

            while ((i < line_count) && ((file_line = file_reader.readLine()) != null))
            {
                file_lines_[i] = file_line;

                i++;
            }

            if (i < line_count)
                return Optional.empty();

            return Optional.of(file_lines_);
        }
        catch (IOException e)
        {
            System.err.print("\nError reading file lines: " + e.getMessage() + "\n\n");

            return Optional.empty();
        }
    }
}